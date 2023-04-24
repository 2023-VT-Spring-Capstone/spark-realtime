package com.capstone.realtimebackend.mapper.impl;

import com.capstone.realtimebackend.mapper.PublisherMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
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
    public Map<String, Object> searchDetailByItem(String date, String keyWord, int from, Integer pageSize) {
        HashMap<String, Object> results = new HashMap<>();

        String indexName  = indexNamePrefix + date ;
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //明细字段
        searchSourceBuilder.fetchSource(new String[]{
                "created_time", "date", "id", "is_origin", "is_self", "label", "num_comments", "body",
                "permalink","score","bearish", "neutral", "bullish", "sentiment","subreddit","ticker","title", "upvote_ratio", "url"}, null );

        // title + body
        MatchQueryBuilder titleQueryBuilder = QueryBuilders.matchQuery("title", keyWord).operator(Operator.OR);
        MatchQueryBuilder bodyQueryBuilder = QueryBuilders.matchQuery("body", keyWord).operator(Operator.OR);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().should(titleQueryBuilder).should(bodyQueryBuilder);

        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(pageSize);
        HighlightBuilder highlightBuilder = new HighlightBuilder().field("title").field("body");

        searchSourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            long total = searchResponse.getHits().getTotalHits().value;
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            ArrayList<Map<String, Object>> sourceMaps = new ArrayList<>();

            for (SearchHit searchHit : searchHits) {
                //提取source
                Map<String, Object> sourceMap = searchHit.getSourceAsMap();
                //提取高亮
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
            results.put("total",total );
            results.put("detail", sourceMaps);
            return results ;
        } catch (ElasticsearchStatusException ese){
            if(ese.status() == RestStatus.NOT_FOUND){
                log.warn( indexName +" Not exists......");
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
