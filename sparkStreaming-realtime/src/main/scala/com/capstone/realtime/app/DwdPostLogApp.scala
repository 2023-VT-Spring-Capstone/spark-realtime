package com.capstone.realtime.app

import java.{lang, util}
import java.util.Date
import com.alibaba.fastjson.{JSON, JSONObject}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import com.capstone.realtime.bean.{PostInfo, PostLog}
import com.capstone.realtime.util.{MyBeanUtils, MyEsUtils, MyKafkaUtils, MyOffsetsUtils, MyRedisUtils}
import redis.clients.jedis.Jedis

import java.text.SimpleDateFormat
import java.time.{LocalDate, Period}
import scala.collection.mutable.ListBuffer


object DwdPostLogApp {
  def main(args: Array[String]): Unit = {
    //1. environment
    val sparkConf: SparkConf = new SparkConf().setAppName("dwd_post_app").setMaster("local[4]")
    val ssc: StreamingContext = new StreamingContext(sparkConf,Seconds(5))

    //2. read offset from Redis
//    val topicName : String = "DWD_BASE_LOG"//DWD_ANALYZED_LOG
//    val groupId : String = "DWD_BASE_LOG_GROUP"
    val topicName: String = "DWD_ANALYZED_LOG" //DWD_ANALYZED_LOG
    val groupId: String = "DWD_ANALYZED_LOG_GROUP"
    val offsets: Map[TopicPartition, Long] = MyOffsetsUtils.readOffset(topicName, groupId)

    //3. consume data from Kafka
    var kafkaDStream: InputDStream[ConsumerRecord[String, String]] = null
    if(offsets != null && offsets.nonEmpty){
      kafkaDStream = MyKafkaUtils.getKafkaDStream(ssc,topicName,groupId,offsets)
    }else {
      kafkaDStream = MyKafkaUtils.getKafkaDStream(ssc,topicName,groupId)
    }

    //4. get finished offset value
    var offsetRanges: Array[OffsetRange] = null
    val offsetRangesDStream: DStream[ConsumerRecord[String, String]] = kafkaDStream.transform(
      rdd => {
        offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
        rdd
      }
    )
    //5. Data manipulation
    // 5.1 change structure
    val pageLogDStream: DStream[PostLog] = offsetRangesDStream.map(
      consumerRecord => {
        val value: String = consumerRecord.value()
        val postLog: PostLog = JSON.parseObject(value, classOf[PostLog])
        postLog
      }
    )
    // PageLog(mid_130,7,1,huawei,0,Xiaomi 9,Android 8.1,v2.1.111,Xiaomi,trade,cart,31,sku_ids,1728,null,1647779796000)
    //    pageLogDStream.print(100)

    pageLogDStream.cache()
    pageLogDStream.foreachRDD(
      rdd => println("Before self examination: " + rdd.count())
    )
//    // 5.2
//    // 5.2.1 self examination: delete if title is empty
//    val filterDStream:DStream[PostLog] = pageLogDStream.filter(
//      postLog => postLog.title == null
//    )
//
//    filterDStream.cache()
//    filterDStream.foreachRDD(
//      rdd => {
//        println("After self examination: " + rdd.count())
//        println("----------------------------")
//      }
//    )
    // 5.3 Parse the date
    val dauInfoDStream: DStream[PostInfo] = pageLogDStream.mapPartitions(
      postLogIter => {
        val postInfoFinal: ListBuffer[PostInfo] = ListBuffer[PostInfo]()

        val sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        for (postLog <- postLogIter) {
          val postInfo: PostInfo = new PostInfo()
          MyBeanUtils.copyProperties(postLog, postInfo)

          val dateTime: String = postLog.created_time
          val dtHrArr: Array[String] = dateTime.split(" ")
          val date: String = dtHrArr(0)
          postInfo.date = date
          postInfoFinal.append(postInfo)
        }

        postInfoFinal.iterator
      }
    )

//    dauInfoDStream.print(100)

    //write to OLAP
    //save to different indices by date
    dauInfoDStream.foreachRDD(
      rdd => {
        rdd.foreachPartition(
          postInfoIter => {
            val docs: List[(String, PostInfo)] = postInfoIter.map(postInfo => (postInfo.id, postInfo)).toList
            if (docs.size > 0){
              val head: (String, PostInfo) = docs.head
              val d: String = head._2.date
//              val sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
//              val dateStr: String = sdf.format(new Date(ts))
              val indexName : String = s"reddit_post_$d"
              //write into ES
              MyEsUtils.bulkSave(indexName, docs)
            }
          }
        )
        //commit offset
        MyOffsetsUtils.saveOffset(topicName, groupId , offsetRanges)
      }
    )
    ssc.start()
    ssc.awaitTermination()
  }
}