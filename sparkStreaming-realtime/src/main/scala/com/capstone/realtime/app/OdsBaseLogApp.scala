package com.capstone.realtime.app

import com.alibaba.fastjson.serializer.SerializeConfig
import com.alibaba.fastjson.{JSON, JSONArray, JSONObject}
import com.capstone.realtime.bean.{PageActionLog, PageDisplayLog, PageLog, StartLog}
import com.capstone.realtime.util.{MyKafkaUtils, MyOffsetsUtils}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object OdsBaseLogApp {
  def main(args: Array[String]): Unit = {
    //1. 准备实时环境
    //TODO 注意并行度与Kafka中topic的分区个数的对应关系(希望能one to one, 因此希望kafka能保持四個分區)
    val sparkConf: SparkConf = new SparkConf().setAppName("ods_base_log_app").setMaster("local[4]")//4個並行度
    val ssc: StreamingContext = new StreamingContext(sparkConf , Seconds(5))

    //2. Consume data from Kafka
    val topicName : String = "ODS_BASE_LOG"  //application.yaml 对应生成器配置中的主题名
    val groupId : String = "ODS_BASE_LOG_GROUP" //隨便寫

    var kafkaDStream: InputDStream[ConsumerRecord[String, String]] =
                  MyKafkaUtils.getKafkaDStream(ssc, topicName, groupId)

    //object to json
    val jsonObjDStream: DStream[JSONObject] = kafkaDStream.map(
      consumerRecord => {
        //获取ConsumerRecord中的value,value就是日志数据
        val log: String = consumerRecord.value()
        //转换成Json对象
        val jsonObj: JSONObject = JSON.parseObject(log)
        //返回
        jsonObj
      }
    )
//    //TODO test consuming data from kafka
//    jsonObjDStream.print(1000)
    //3.2 SPLIT the STREAM
    // 日志数据：
    //   页面访问数据 visit data(dependency: page -> displays -> actions)
    //      公共字段 common
    //      页面数据 page
    //      曝光数据 displays
    //      事件数据 actions
    //      (错误数据 err)
    //      ts
    //   启动数据
    //      公共字段 common
    //      启动数据 start
    //      (错误数据 err)
    //      ts
    val DWD_PAGE_LOG_TOPIC: String = "DWD_PAGE_LOG_TOPIC" // 页面访问
    val DWD_PAGE_DISPLAY_TOPIC: String = "DWD_PAGE_DISPLAY_TOPIC" //页面曝光
    val DWD_PAGE_ACTION_TOPIC: String = "DWD_PAGE_ACTION_TOPIC" //页面事件
    val DWD_START_LOG_TOPIC: String = "DWD_START_LOG_TOPIC" // 启动数据
    val DWD_ERROR_LOG_TOPIC: String = "DWD_ERROR_LOG_TOPIC" // 错误数据

    // How to split the stream:
    // error log: do not split. If contains error，send the whole data to identical topic
    // 页面数据: 拆分成页面访问， 曝光， 事件 分别发送到对应的topic
    // 启动数据: 发动到对应的topic
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
                MyKafkaUtils.send(DWD_ERROR_LOG_TOPIC, jsonObj.toJSONString)
              } else {
                // #0. common section
                val commonObj: JSONObject = jsonObj.getJSONObject("common")
                val ar: String = commonObj.getString("ar")
                val uid: String = commonObj.getString("uid")
                val os: String = commonObj.getString("os")
                val ch: String = commonObj.getString("ch")
                val isNew: String = commonObj.getString("is_new")
                val md: String = commonObj.getString("md")
                val mid: String = commonObj.getString("mid")
                val vc: String = commonObj.getString("vc")
                val ba: String = commonObj.getString("ba")
                //time stamp
                val ts: Long = jsonObj.getLong("ts")
                // #1. 页面数据
                val pageObj: JSONObject = jsonObj.getJSONObject("page")
                if (pageObj != null) {
                  //提取page字段
                  val pageId: String = pageObj.getString("page_id")
                  val pageItem: String = pageObj.getString("item")
                  val pageItemType: String = pageObj.getString("item_type")
                  val duringTime: Long = pageObj.getLong("during_time")
                  val lastPageId: String = pageObj.getString("last_page_id")
                  val sourceType: String = pageObj.getString("source_type")

                  //common section + page section-> 封装成PageLog bean object
                  var pageLog = PageLog(mid, uid, ar, ch, isNew, md, os, vc, ba,
                    pageId, lastPageId, pageItem, pageItemType, duringTime, sourceType, ts)
                  //send to Kafka topic = DWD_PAGE_LOG_TOPIC
                  //fastJSON requires object has get/set method.
                  //but our scala bean object pageLog has no get/set method
                  MyKafkaUtils.send(DWD_PAGE_LOG_TOPIC, JSON.toJSONString(pageLog, new SerializeConfig(true)))

                  //提取曝光数据
                  val displaysJsonArr: JSONArray = jsonObj.getJSONArray("displays")
                  if (displaysJsonArr != null && displaysJsonArr.size() > 0) {
                    for (i <- 0 until displaysJsonArr.size()) {
                      //循环拿到每个曝光
                      val displayObj: JSONObject = displaysJsonArr.getJSONObject(i)
                      //提取曝光字段
                      val displayType: String = displayObj.getString("display_type")
                      val displayItem: String = displayObj.getString("item")
                      val displayItemType: String = displayObj.getString("item_type")
                      val posId: String = displayObj.getString("pos_id")
                      val order: String = displayObj.getString("order")

                      //封装成PageDisplayLog
                      val pageDisplayLog =
                        PageDisplayLog(mid, uid, ar, ch, isNew, md, os, vc, ba, pageId, lastPageId,
                          pageItem, pageItemType, duringTime, sourceType, displayType, displayItem,
                          displayItemType, order, posId, ts)
                      // 写到 DWD_PAGE_DISPLAY_TOPIC
                      MyKafkaUtils.send(DWD_PAGE_DISPLAY_TOPIC, JSON.toJSONString(pageDisplayLog, new SerializeConfig(true)))
                    }
                  }

                  //提取事件数据
                  val actionJsonArr: JSONArray = jsonObj.getJSONArray("actions")
                  if (actionJsonArr != null && actionJsonArr.size() > 0) {
                    for (i <- 0 until actionJsonArr.size()) {
                      val actionObj: JSONObject = actionJsonArr.getJSONObject(i)
                      //提取字段
                      val actionId: String = actionObj.getString("action_id")
                      val actionItem: String = actionObj.getString("item")
                      val actionItemType: String = actionObj.getString("item_type")
                      val actionTs: Long = actionObj.getLong("ts")

                      //封装PageActionLog
                      var pageActionLog =
                        PageActionLog(mid, uid, ar, ch, isNew, md, os, vc, ba, pageId, lastPageId, pageItem, pageItemType, duringTime, sourceType, actionId, actionItem, actionItemType, actionTs, ts)
                      //写出到DWD_PAGE_ACTION_TOPIC
                      MyKafkaUtils.send(DWD_PAGE_ACTION_TOPIC, JSON.toJSONString(pageActionLog, new SerializeConfig(true)))
                    }
                  }
                }
                // #2. 启动数据
                val startJsonObj: JSONObject = jsonObj.getJSONObject("start")
                if (startJsonObj != null) {
                  //提取字段
                  val entry: String = startJsonObj.getString("entry")
                  val loadingTime: Long = startJsonObj.getLong("loading_time")
                  val openAdId: String = startJsonObj.getString("open_ad_id")
                  val openAdMs: Long = startJsonObj.getLong("open_ad_ms")
                  val openAdSkipMs: Long = startJsonObj.getLong("open_ad_skip_ms")

                  //封装StartLog
                  var startLog =
                    StartLog(mid, uid, ar, ch, isNew, md, os, vc, ba, entry, openAdId, loadingTime, openAdMs, openAdSkipMs, ts)
                  //写出DWD_START_LOG_TOPIC
                  MyKafkaUtils.send(DWD_START_LOG_TOPIC, JSON.toJSONString(startLog, new SerializeConfig(true)))
                }
              }
            }
            MyKafkaUtils.flush()
          }
        )
//TODO: save the offset
      }
    )
    ssc.start()
    ssc.awaitTermination()
  }
}
