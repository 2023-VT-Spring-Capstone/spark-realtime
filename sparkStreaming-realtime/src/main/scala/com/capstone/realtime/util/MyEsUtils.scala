package com.capstone.realtime.util

import java.util
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializeConfig
import org.apache.http.auth.{AuthScope, UsernamePasswordCredentials}
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.HttpHost
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.elasticsearch.action.bulk.BulkRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.search.{SearchRequest, SearchResponse}
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback
import org.elasticsearch.client.indices.GetIndexRequest
import org.elasticsearch.client.{RequestOptions, RestClient, RestClientBuilder, RestHighLevelClient}
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.builder.SearchSourceBuilder

import scala.collection.mutable.ListBuffer

/**
 * ES工具类， 用于对ES进行读写操作
 */
object MyEsUtils {

  /**客户端对象*/
  val esClient : RestHighLevelClient = build()

  /**创建ES客户端对象*/
  def build(): RestHighLevelClient ={
    val host: String = MyPropsUtils(MyConfig.ES_HOST)
    val port: String = MyPropsUtils(MyConfig.ES_PORT)
//    val credentialsProvider = new BasicCredentialsProvider()
//    credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "lIaOuoKHcJcM173Ei8U7"))
//    val restClientBuilder = RestClient.builder(new HttpHost("localhost", 9200)).setHttpClientConfigCallback(new HttpClientConfigCallback() {
//      def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder = httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
//    })
////    val restClientBuilder: RestClientBuilder = RestClient.builder(new HttpHost(host,port.toInt))
//    val client: RestHighLevelClient = new RestHighLevelClient(restClientBuilder)
    val restClientBuilder: RestClientBuilder = RestClient.builder(new HttpHost(host, port.toInt))
    val client: RestHighLevelClient = new RestHighLevelClient(restClientBuilder)
    client
  }

  /**关闭ES对象*/
  def close(): Unit ={
    if(esClient != null ) esClient.close()
  }

  /**
   * 1. batch write
   * 2. idempotent
    * List[(String , AnyRef)] dockID, data
   */
  def bulkSave(indexName : String , docs :  List[(String , AnyRef)]  ): Unit ={
    val bulkRequest: BulkRequest = new BulkRequest(indexName)
    for ((docId, docObj) <- docs) {
      val indexRequest: IndexRequest = new IndexRequest()
      val dataJson: String = JSON.toJSONString(docObj , new SerializeConfig(true))
      indexRequest.source(dataJson , XContentType.JSON)
      indexRequest.id(docId)
      bulkRequest.add(indexRequest)
    }
    esClient.bulk(bulkRequest ,RequestOptions.DEFAULT)
  }

  /**
   * 查询指定的字段
   */
  def searchField(indexName: String, fieldName: String): List[String] = {
    //判断索引是否存在
    val getIndexRequest: GetIndexRequest = new GetIndexRequest(indexName)
    val isExists: Boolean =
      esClient.indices().exists(getIndexRequest,RequestOptions.DEFAULT)
    if(!isExists){
      return null
    }
    //正常从ES中提取指定的字段
    val mids: ListBuffer[String] = ListBuffer[String]()
    val searchRequest: SearchRequest = new SearchRequest(indexName)
    val searchSourceBuilder: SearchSourceBuilder = new SearchSourceBuilder()
    searchSourceBuilder.fetchSource(fieldName,null).size(100000)
    searchRequest.source(searchSourceBuilder)
    val searchResponse: SearchResponse =
      esClient.search(searchRequest , RequestOptions.DEFAULT)
    val hits: Array[SearchHit] = searchResponse.getHits.getHits
    for (hit <- hits) {
      val sourceMap: util.Map[String, AnyRef] = hit.getSourceAsMap
      val mid: String = sourceMap.get(fieldName).toString
      mids.append(mid)
    }
    mids.toList
  }

  def main(args: Array[String]): Unit = {
    println(searchField("reddit_post", "id"))
  }
}



