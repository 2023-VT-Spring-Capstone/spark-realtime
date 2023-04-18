package com.capstone.realtime.app

import com.alibaba.fastjson.serializer.SerializeConfig
import com.alibaba.fastjson.{JSON, JSONArray, JSONObject}
import com.capstone.realtime.bean.{PostLog}
import com.capstone.realtime.util.{MyKafkaUtils, MyOffsetsUtils}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object OdsBaseLogApp {
  def main(args: Array[String]): Unit = {
    //1. prep for real-time env: concurrency & entry point for Spark Streaming
    val sparkConf: SparkConf = new SparkConf().setAppName("ods_base_log_app").setMaster("local[4]")//4個並行度
    val ssc: StreamingContext = new StreamingContext(sparkConf , Seconds(5))

    //2. Consume data from Kafka: topic name & group name
    val topicName : String = "DWD_NEW_LOG"
    val groupId : String = "DWD_NEW_LOG_GROUP"
    // Read offset from Redis (from driver)
    val offsets: Map[TopicPartition, Long] = MyOffsetsUtils.readOffset(topicName, groupId)

    var kafkaDStream: InputDStream[ConsumerRecord[String, String]] = null
    if (offsets != null && offsets.nonEmpty) { //assign offsets value
      kafkaDStream = MyKafkaUtils.getKafkaDStream(ssc, topicName, groupId, offsets)
    } else { //use default offset
      kafkaDStream = MyKafkaUtils.getKafkaDStream(ssc, topicName, groupId)
    }

    // extract offsets from the fetched data before manipulating it.
    var offsetRanges: Array[OffsetRange] = null
    val offsetRangesDStream: DStream[ConsumerRecord[String, String]] = kafkaDStream.transform(
      rdd => {
        offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
        rdd
      }
    )

    //DATA PROCESSING
    //DATA PROCESSING - 1.transform the form from object to json
    val jsonObjDStream: DStream[JSONObject] = offsetRangesDStream.map(
      consumerRecord => {
        val log: String = consumerRecord.value()
        val jsonObj: JSONObject = JSON.parseObject(log)
        jsonObj
      }
    )

//    TODO: try consume data from kafka here
    jsonObjDStream.print(1000)

//    DATA PROCESSING - 2. SPLIT the stream to substreams based on topics
    val DWD_BASE_LOG_TOPIC: String = "DWD_BASE_LOG"
    val DWD_HOT_LOG_TOPIC: String = "DWD_HOT_LOG"
    val DWD_TOP_TOPIC: String = "DWD_PAGE_DISPLAY_TOPIC"
    val DWD_CONTROVERSIAL_TOPIC: String = "DWD_PAGE_ACTION_TOPIC"
    val DWD_ERROR_LOG_TOPIC: String = "DWD_ERROR_LOG_TOPIC"

    jsonObjDStream.foreachRDD(
      rdd => {
        rdd.foreachPartition(
          jsonObjIter => {
            for (jsonObj <- jsonObjIter) {
              //SPLIT the stream
              //if error, directly send to DWD_ERROR_LOG_TOPIC, so we save the time to parse it
              val errObj: JSONObject = jsonObj.getJSONObject("err")
              if (errObj != null) {
                //send data to DWD_ERROR_LOG_TOPIC
//                MyKafkaUtils.send(DWD_ERROR_LOG_TOPIC, jsonObj.toJSONString)
              } else {

              }
            }
            MyKafkaUtils.flush()
          }
        )
        MyOffsetsUtils.saveOffset(topicName, groupId, offsetRanges)
      }
    )
    ssc.start()
    ssc.awaitTermination()
  }
}
