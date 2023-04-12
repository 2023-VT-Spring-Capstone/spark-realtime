package com.capstone.realtime.app

import java.util

import com.alibaba.fastjson.{JSON, JSONObject}
import com.capstone.realtime.util.{MyKafkaUtils, MyOffsetsUtils, MyRedisUtils}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import org.apache.spark.SparkConf
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import redis.clients.jedis.Jedis

object OdsBaseDbApp {
  def main(args: Array[String]): Unit = {
    //1. Prepare real-time environment
    val sparkConf: SparkConf = new SparkConf().setAppName("ods_base_db_app").setMaster("local[4]")
    val ssc: StreamingContext = new StreamingContext(sparkConf , Seconds(5))

    val topicName : String = "ODS_BASE_DB"
    val groupId : String = "ODS_BASE_DB_GROUP"

    //2. Read the offset from Redis
    val offsets: Map[TopicPartition, Long] = MyOffsetsUtils.readOffset(topicName, groupId)

    //3. Consume data from Kafka
    var kafkaDStream: InputDStream[ConsumerRecord[String, String]] = null
    if(offsets != null && offsets.nonEmpty){
      kafkaDStream = MyKafkaUtils.getKafkaDStream(ssc,topicName,groupId,offsets)
    }else{
      kafkaDStream = MyKafkaUtils.getKafkaDStream(ssc,topicName,groupId)
    }

    //4. Extract the offset endpoint
    var offsetRanges: Array[OffsetRange] = null
    val offsetRangesDStream : DStream[ConsumerRecord[String, String]] = kafkaDStream.transform(
      rdd => {
        offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
        rdd
      }
    )
    //5. Process data
    // 5.1 Convert data structure
    val jsonObjDStream: DStream[JSONObject] = offsetRangesDStream.map(
      consumerRecord => {
        val dataJson: String = consumerRecord.value()
        val jSONObject: JSONObject = JSON.parseObject(dataJson)
        jSONObject
      }
    )
//    jsonObjDStream.print(100)

    //5.2 分流


    //事实表清单
    //val factTables : Array[String] = Array[String]( "order_info","order_detail" /*缺啥补啥*/)
    //维度表清单
    //val dimTables : Array[String] = Array[String]("user_info", "base_province" /*缺啥补啥*/)

    //Redis连接写到哪里???
    // foreachRDD外面:  driver  ，连接对象不能序列化，不能传输
    // foreachRDD里面, foreachPartition外面 : driver  ，连接对象不能序列化，不能传输
    // foreachPartition里面 , 循环外面：executor ， 每分区数据开启一个连接，用完关闭.
    // foreachPartition里面,循环里面:  executor ， 每条数据开启一个连接，用完关闭， 太频繁。
    //
    jsonObjDStream.foreachRDD(
      rdd => {
        //如何动态配置表清单???
        // 将表清单维护到redis中，实时任务中动态的到redis中获取表清单.
        // 类型: set
        // key:  FACT:TABLES   DIM:TABLES
        // value : 表名的集合
        // 写入API: sadd
        // 读取API: smembers
        // 过期: 不过期

        val redisFactKeys : String = "FACT:TABLES"
        val redisDimKeys : String = "DIM:TABLES"
        val jedis: Jedis = MyRedisUtils.getJedisFromPool()
        // Fact table list
        val factTables: util.Set[String] = jedis.smembers(redisFactKeys)
        println("factTables: " + factTables)
        // Make it a broadcast variable
        val factTablesBC: Broadcast[util.Set[String]] = ssc.sparkContext.broadcast(factTables)

        // Dimension table list
        val dimTables: util.Set[String] = jedis.smembers(redisDimKeys)
        println("dimTables: " + dimTables)
        // Make it a broadcast variable
        val dimTablesBC: Broadcast[util.Set[String]] = ssc.sparkContext.broadcast(dimTables)
        jedis.close()

        rdd.foreachPartition(
          jsonObjIter => {
            // Open Redis connection
            val jedis: Jedis = MyRedisUtils.getJedisFromPool()
            for (jsonObj <- jsonObjIter) {
              // Extract operation type
              val operType: String = jsonObj.getString("type")

              val opValue: String = operType match {
                case "bootstrap-insert" => "I"
                case "insert" => "I"
                case "update" => "U"
                case "delete" => "D"
                case _ => null
              }
              // Identifying operation types: 1. Determine the specific operation; 2. Filter out uninteresting data.
              if(opValue != null){
                // Extract table name
                val tableName: String = jsonObj.getString("table")

                if(factTablesBC.value.contains(tableName)){
                  // Fact data
                  // Extract data
                  val data: String = jsonObj.getString("data")
                  // DWD_ORDER_INFO_I  DWD_ORDER_INFO_U  DWD_ORDER_INFO_D
                  val dwdTopicName : String = s"DWD_${tableName.toUpperCase}_${opValue}"
                  MyKafkaUtils.send(dwdTopicName ,  data )

//                  // Simulate data delay
//                  if(tableName.equals("fast_info")){
//                    Thread.sleep(200)
//                  }
                }

                if(dimTablesBC.value.contains(tableName)){
                  // Dimensional Data
                  // 类型 : string  hash
                  //        hash ： 整个表存成一个hash。 要考虑目前数据量大小和将来数据量增长问题 及 高频访问问题.
                  //        hash :  一条数据存成一个hash.
                  //        String : 一条数据存成一个jsonString.
                  // key :  DIM:表名:ID
                  // value : 整条数据的jsonString
                  // 写入API: set
                  // 读取API: get
                  // 过期:  不过期
                  // Extract the "id" from the data
                  val dataObj: JSONObject = jsonObj.getJSONObject("data")
                  val id: String = dataObj.getString("id")
                  val redisKey : String = s"DIM:${tableName.toUpperCase}:$id"
                  // Switching the Redis connection too frequently here.
                  //val jedis: Jedis = MyRedisUtils.getJedisFromPool()
                  jedis.set(redisKey, dataObj.toJSONString)
                  //jedis.close()
                }
              }
            }
            // Close Redis connection
            jedis.close()
            // Flush Kafka buffer
            MyKafkaUtils.flush()
          }
        )
        // Submit offset
        MyOffsetsUtils.saveOffset(topicName, groupId,offsetRanges)
      }
    )
    ssc.start()
    ssc.awaitTermination()
  }
}















