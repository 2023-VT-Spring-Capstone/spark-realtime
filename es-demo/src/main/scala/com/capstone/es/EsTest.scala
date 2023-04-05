package com.capstone.es

import java.util
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializeConfig
import org.apache.http.HttpHost
import org.apache.http.auth.{AuthScope, UsernamePasswordCredentials}
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.elasticsearch.action.bulk.BulkRequest
import org.elasticsearch.action.delete.DeleteRequest
import org.elasticsearch.action.get.{GetRequest, GetResponse}
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.search.{SearchRequest, SearchResponse}
import org.elasticsearch.action.update.UpdateRequest
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback
import org.elasticsearch.client.{RequestOptions, RestClient, RestClientBuilder, RestHighLevelClient}
import org.elasticsearch.common.text.Text
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.index.query.{BoolQueryBuilder, MatchQueryBuilder, QueryBuilders, RangeQueryBuilder, TermQueryBuilder}
import org.elasticsearch.index.reindex.UpdateByQueryRequest
import org.elasticsearch.script.{Script, ScriptType}
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.aggregations.{Aggregation, AggregationBuilders, Aggregations, BucketOrder}
import org.elasticsearch.search.aggregations.bucket.terms.{ParsedTerms, Terms, TermsAggregationBuilder}
import org.elasticsearch.search.aggregations.metrics.{AvgAggregationBuilder, ParsedAvg}
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.elasticsearch.search.fetch.subphase.highlight.{HighlightBuilder, HighlightField}
import org.elasticsearch.search.sort.SortOrder

/**
 * ES Client
 */
object EsTest {
  def main(args: Array[String]): Unit = {
    //println(client)
    put()
    //post()
    //bulk()
    //update()
    //updateByQuery()
    //delete()
    //getById()
    //searchByFilter
    //    searchByAggs

    close()
  }

  /**
   * single query
   */
  def getById(): Unit ={
    val getRequest: GetRequest = new GetRequest("movie" , "1001")
    val getResponse: GetResponse = client.get(getRequest , RequestOptions.DEFAULT)
    val dataStr: String = getResponse.getSourceAsString
    println(dataStr)
  }


  /**
   * Query - conditional query
   * search:
   * query for doubanScore >= 5.0 and keyword search for 'red sea'
   * highlight the keyword in search results
   * display the first page with 2 results per page
   * sort by doubanScore in descending order
   */

  def searchByFilter(): Unit ={
    val searchRequest: SearchRequest = new SearchRequest("movie_index")
    val searchSourceBuilder: SearchSourceBuilder = new SearchSourceBuilder()
    //query
    //bool
    val boolQueryBuilder: BoolQueryBuilder = QueryBuilders.boolQuery()
    //filter
    val rangeQueryBuilder: RangeQueryBuilder =
      QueryBuilders.rangeQuery("doubanScore").gte(5.0)
    boolQueryBuilder.filter(rangeQueryBuilder)
    //must
    val matchQueryBuilder: MatchQueryBuilder =
      QueryBuilders.matchQuery("name","red sea")
    boolQueryBuilder.must(matchQueryBuilder)
    searchSourceBuilder.query(boolQueryBuilder)

    //page break
    searchSourceBuilder.from(0)
    searchSourceBuilder.size(1)
    //sorting
    searchSourceBuilder.sort("doubanScore",SortOrder.DESC)

    //highlight
    val highlightBuilder: HighlightBuilder = new HighlightBuilder()
    highlightBuilder.field("name")
    searchSourceBuilder.highlighter(highlightBuilder)

    searchRequest.source(searchSourceBuilder)
    val searchResponse: SearchResponse =
      client.search(searchRequest , RequestOptions.DEFAULT)

    // obtain total data
    val totalDocs: Long = searchResponse.getHits.getTotalHits.value

    // detail
    val hits: Array[SearchHit] = searchResponse.getHits.getHits
    for (hit <- hits) {
      // data
      val dataJson: String = hit.getSourceAsString
      //hit.getSourceAsMap
      // extract high light
      val highlightFields: util.Map[String, HighlightField] = hit.getHighlightFields
      val highlightField: HighlightField = highlightFields.get("name")
      val fragments: Array[Text] = highlightField.getFragments
      val highLightValue: String = fragments(0).toString

      println("Detailed Data: " +  dataJson)
      println("High Light: " + highLightValue)

    }
  }


  /**
   * aggregate query
   *
   * 查询每位演员参演的电影的平均分，倒叙排序
   */

  def searchByAggs(): Unit ={
    val searchRequest: SearchRequest = new SearchRequest("movie_index")
    val searchSourceBuilder: SearchSourceBuilder = new SearchSourceBuilder()
    // no detail
    searchSourceBuilder.size(0)
    //group
    val termsAggregationBuilder: TermsAggregationBuilder = AggregationBuilders.terms("groupbyactorname").
      field("actorList.name.keyword").
      size(10).
      order(BucketOrder.aggregation("doubanscoreavg",false))
    //avg
    val avgAggregationBuilder: AvgAggregationBuilder = AggregationBuilders.avg("doubanscoreavg").field("doubanScore")
    termsAggregationBuilder.subAggregation(avgAggregationBuilder)
    searchSourceBuilder.aggregation(termsAggregationBuilder)
    searchRequest.source(searchSourceBuilder)
    val searchResponse: SearchResponse =
      client.search(searchRequest , RequestOptions.DEFAULT)
    val aggregations: Aggregations = searchResponse.getAggregations
    //val groupbyactornameAggregation: Aggregation =
    //    aggregations.get[Aggregation]("groupbyactorname")
    val groupbyactornameParsedTerms: ParsedTerms =
    aggregations.get[ParsedTerms]("groupbyactorname")
    val buckets: util.List[_ <: Terms.Bucket] = groupbyactornameParsedTerms.getBuckets
    import scala.collection.JavaConverters._
    for (bucket <- buckets.asScala) {
      // actor name
      val actorName: String = bucket.getKeyAsString
      //num of film
      val moviecount: Long = bucket.getDocCount

      // score average
      val aggregations: Aggregations = bucket.getAggregations
      val doubanscoreavgParsedAvg: ParsedAvg =
        aggregations.get[ParsedAvg]("doubanscoreavg")
      val avgScore: Double = doubanscoreavgParsedAvg.getValue

      println(s"$actorName appeared in $moviecount movies， average score is $avgScore")
    }
  }

  /**
   * delete
   */
  def delete(): Unit ={
    val deleteRequest: DeleteRequest = new DeleteRequest("movie" , "PF8hz38BQnSG2BfXDtnK")
    client.delete(deleteRequest , RequestOptions.DEFAULT)
  }

  /**
   * single update
   */
  def update(): Unit ={
    val updateRequest: UpdateRequest =
      new UpdateRequest("movie_test", "1001")
    updateRequest.doc("movie_name" , "Kung Fu")
    client.update(updateRequest , RequestOptions.DEFAULT);
  }

  /**
   * conditional update
   */

  def updateByQuery(): Unit ={
    val updateByQueryRequest: UpdateByQueryRequest = new UpdateByQueryRequest("movie")
    //query
    //val termQueryBuilder: TermQueryBuilder =
    //new TermQueryBuilder("movie_name.keyword","operation red sea")
    val boolQueryBuilder: BoolQueryBuilder = QueryBuilders.boolQuery()
    val termQueryBuilder: TermQueryBuilder = QueryBuilders.termQuery("movie_name.keyword", "red sea operation")
    boolQueryBuilder.filter(termQueryBuilder)
    updateByQueryRequest.setQuery(boolQueryBuilder)
    //update
    val params: util.HashMap[String, AnyRef] = new util.HashMap[String,AnyRef]()
    params.put("newName" , "mekong operation")
    val script: Script = new Script(
      ScriptType.INLINE ,
      Script.DEFAULT_SCRIPT_LANG,
      "ctx._source['movie_name']=params.newName",
      params
    )
    updateByQueryRequest.setScript(script)

    client.updateByQuery(updateByQueryRequest , RequestOptions.DEFAULT)
  }



  /**
   *  batch writing
   */
  def bulk(): Unit ={
    val bulkRequest: BulkRequest = new BulkRequest()
    val movies: List[Movie] = List[Movie](
      Movie("1002", "sun moon lake"),
      Movie("1003", "Taipei bridge"),
      Movie("1004", "sniper"),
      Movie("1005", "bear presence")
    )
    for (movie <- movies) {
      val indexRequest: IndexRequest = new IndexRequest("movie") // specifying index
      val movieJson: String = JSON.toJSONString(movie, new SerializeConfig(true))
      indexRequest.source(movieJson ,XContentType.JSON)
      //idempotent - specifying id , non-idempotent - without specifying id
      indexRequest.id(movie.id)
      //add indexRequest to bulk
      bulkRequest.add(indexRequest)
    }
    client.bulk(bulkRequest , RequestOptions.DEFAULT);
  }



  /**
   * create - idempotent - specifying docid
   */
  def put(): Unit ={
    val indexRequest: IndexRequest = new IndexRequest()
    //specify index
    indexRequest.index("movie")
    //specify doc
    val movie: Movie = Movie("1003","fast and furious 3")
    val movieJson: String = JSON.toJSONString(movie, new SerializeConfig(true))
    indexRequest.source(movieJson,XContentType.JSON)
    //specify doc
    indexRequest.id("1003")

    client.index(indexRequest , RequestOptions.DEFAULT)
  }

  /**
   * create - non-idempotent - without specifying docid
   */
  def post(): Unit ={
    val indexRequest: IndexRequest = new IndexRequest()
    //specify index
    indexRequest.index("movie")
    //specify doc
    val movie: Movie = Movie("1001","fast and furious")
    val movieJson: String = JSON.toJSONString(movie, new SerializeConfig(true))
    indexRequest.source(movieJson,XContentType.JSON)
    client.index(indexRequest , RequestOptions.DEFAULT)
  }












  /**client object*/
  var client : RestHighLevelClient = create()

  /**create client object*/
  def create(): RestHighLevelClient ={


    val credentialsProvider = new BasicCredentialsProvider()
    credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "lIaOuoKHcJcM173Ei8U7"))



    //    val restClientBuilder: RestClientBuilder =
    //      RestClient.builder(new HttpHost("localhost", 9200))

    val restClientBuilder = RestClient.builder(new HttpHost("localhost", 9200)).setHttpClientConfigCallback(new HttpClientConfigCallback() {
      def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder = httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
    })

    val client = new RestHighLevelClient(restClientBuilder)
    client
  }

  /**close client object*/
  def close(): Unit ={
    if(client!=null) client.close()
  }

}

case class Movie(id : String , movie_name : String )

