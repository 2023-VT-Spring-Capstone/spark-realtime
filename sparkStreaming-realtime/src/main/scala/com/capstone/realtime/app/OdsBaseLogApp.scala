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
    val sparkConf: SparkConf = new SparkConf().setAppName("ods_base_log_app").setMaster("local[1]")//4個並行度
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
    jsonObjDStream.print(1000)

    ssc.start()
    ssc.awaitTermination()
  }
}
