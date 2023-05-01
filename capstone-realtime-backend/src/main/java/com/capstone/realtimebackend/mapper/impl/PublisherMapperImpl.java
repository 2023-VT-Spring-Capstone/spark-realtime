package com.capstone.realtimebackend.mapper.impl;

import com.capstone.realtimebackend.mapper.PublisherMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.management.Query;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class PublisherMapperImpl implements PublisherMapper {

    @Autowired
    RestHighLevelClient esClient;
    private String indexNamePrefix = "reddit_post_";

    @Override
    public Map<String, Object> searchDetailByItem(String startDate, String endDate, String keyWord, int from, Integer pageSize) {
        HashMap<String, Object> results = new HashMap<>();

        List<String> datesList = new ArrayList<>();
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        while (!start.isAfter(end)) {

            String indexNameFull = indexNamePrefix + start.toString();
            GetIndexRequest request = new GetIndexRequest().indices(indexNameFull);
            boolean exists = false;
            try {
                exists = esClient.indices().exists(request, RequestOptions.DEFAULT);
            } catch (IOException e) {
                log.error("Error while checking index existence for " + indexNameFull, e);
            }
            if (exists) {
                datesList.add(indexNameFull);
                log.warn("Index " + indexNameFull + " exists.");
            } else {
                log.warn("Index " + indexNameFull + " does not exist.");
            }
            start = start.plusDays(1);
        }

        String[] datesArray = datesList.toArray(new String[0]);
        // Create a search request with the array of dates
        SearchRequest searchRequest = new SearchRequest(datesArray);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.fetchSource(new String[]{
                "created_time", "date", "id", "is_origin", "is_self", "label", "num_comments", "body",
                "permalink","score","bearish", "neutral", "bullish", "sentiment","subreddit","ticker","title", "upvote_ratio", "url"}, null );

        // title + body
        MatchQueryBuilder titleQueryBuilder = QueryBuilders.matchQuery("title", keyWord).operator(Operator.OR);
        MatchQueryBuilder bodyQueryBuilder = QueryBuilders.matchQuery("body", keyWord).operator(Operator.OR);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().should(titleQueryBuilder).should(bodyQueryBuilder);

//        searchSourceBuilder.query(QueryBuilders.boolQuery().must(boolQueryBuilder).must(indicesQueryBuilder));

        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(pageSize);
        HighlightBuilder highlightBuilder = new HighlightBuilder().field("title").field("body");

        searchSourceBuilder.highlighter(highlightBuilder);

        // Create a search source builder with a terms aggregation
        searchSourceBuilder.aggregation(
                AggregationBuilders.terms("sentiment_count").field("sentiment.keyword"));

        searchSourceBuilder.aggregation(
                AggregationBuilders.terms("subreddit_source").field("subreddit.keyword"));

        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);

            // Get the terms aggregation from the search response and retrieve the number of buckets
            Terms sentimentAgg = searchResponse.getAggregations().get("sentiment_count");
            Terms subredditAgg = searchResponse.getAggregations().get("subreddit_source");

            Map<String, Integer> dataSourceSubredditCount = new HashMap<>();
            for (Terms.Bucket bucket : subredditAgg.getBuckets()) {
                String subreddit = bucket.getKeyAsString();
                Integer originCount = Math.toIntExact(bucket.getDocCount());
                log.warn(subreddit + " count: " + originCount);
                if (dataSourceSubredditCount.containsKey(subreddit)) {
                    int existingValue = dataSourceSubredditCount.get(subreddit);
                    dataSourceSubredditCount.put(subreddit, existingValue + originCount);
                } else {
                    dataSourceSubredditCount.put(subreddit, originCount);
                }
            }

            long total = searchResponse.getHits().getTotalHits().value;
            long bullishCount = 0;
            long neutralCount = 0;
            long bearishCount = 0;
            // backward compatibility, some sentiment still classified as positive / negative in previous ver.
            for (Terms.Bucket bucket : sentimentAgg.getBuckets()) {
                String sentimentValue = bucket.getKeyAsString();
                if (sentimentValue.equals("bullish") || sentimentValue.equals("positive")) {
                    bullishCount += bucket.getDocCount();
                } else if (sentimentValue.equals("neutral")) {
                    neutralCount += bucket.getDocCount();
                } else if (sentimentValue.equals("bearish") || sentimentValue.equals("negative")) {
                    bearishCount += bucket.getDocCount();
                }
            }


            SearchHit[] searchHits = searchResponse.getHits().getHits();
            ArrayList<Map<String, Object>> sourceMaps = new ArrayList<>();


            for (SearchHit searchHit : searchHits) {
                //hits._source
                Map<String, Object> sourceMap = searchHit.getSourceAsMap();

                //highlight
                Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();

                HighlightField highlightFieldTitle = highlightFields.get("title");
                String highLightTitle = null;
                if (highlightFieldTitle != null) {
                    Text[] fragmentsTitle = highlightFieldTitle.getFragments();
                    if (fragmentsTitle != null && fragmentsTitle.length > 0) {
                        highLightTitle = fragmentsTitle[0].toString();
                    }
                }

                HighlightField highlightFieldBody = highlightFields.get("body");
                String highLightBody = null;
                if (highlightFieldBody != null) {
                    Text[] fragmentsBody = highlightFieldBody.getFragments();
                    if (fragmentsBody != null && fragmentsBody.length > 0) {
                        highLightBody = fragmentsBody[0].toString();
                    }
                }
                if (highLightTitle != null)
                    sourceMap.put("title",highLightTitle);
                if (highLightBody != null)
                    sourceMap.put("body", highLightBody);
                sourceMaps.add(sourceMap);
            }
            results.put("total", total);
            results.put("bullishCount", bullishCount);
            results.put("neutralCount", neutralCount);
            results.put("bearishCount", bearishCount);
            results.put("detail", sourceMaps);
            results.put("dataOrigin", dataSourceSubredditCount);
            return results ;
        } catch (ElasticsearchStatusException ese){
            if(ese.status() == RestStatus.NOT_FOUND){
                log.warn( " Not exists......");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed querying ES....");
        }
        return results;
    }


    /*
    * get post number by date
    * */
    @Override
    public List<Map<String, Object>> searchPost(String td) {
        List<Map<String, Object>> res = new ArrayList<>();
        Long postTotal = searchPostTotal(td);

        Map<String, Object> map1 = new HashMap<>();
        map1.put("total Posts", postTotal);
        res.add(map1);
//        Map<String, Object> map2 = new HashMap<>();
//        map2.put("post1", 123L);
//        map2.put("post2", 666L);
//        res.add(map2);

        return res;
    }



    public Long searchPostTotal(String td) {
        String indexName = indexNamePrefix + td;
        System.out.println("indexName ====== " + indexName);
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 不要明細
        searchSourceBuilder.size(0);
        searchRequest.source(searchSourceBuilder);


        try {
            SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            long postTotals = searchResponse.getHits().getTotalHits().value;
            return postTotals;
        } catch(ElasticsearchStatusException ese) {
            if (ese.status() == RestStatus.NOT_FOUND) {
                log.warn(indexName + "not exists......");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to query ES...");
        }
        return 0L;
    }
}
