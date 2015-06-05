package com.flipkart;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * Created by gopi.vishwakarma on 05/06/15.
 */
public class ESSearch implements Search {

    private final JSONObject competencies;
    private final Client client;
    private final ObjectMapper objectMapper;

    public ESSearch(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        client = ESClient.getClient();
        this.competencies = buildCompSchema();
    }

    @Override
    public SearchResponse search(UserInfo userInfo) throws Exception {
        String userId = userInfo.getEmail();
        String designation = userInfo.getDesignation();
        return search(userId, designation);
    }

    private SearchResponse search(String userId, String designation) throws Exception {

        Map<String, Collection<Document>> map = new HashMap<>();

        JSONObject comp = competencies.optJSONObject(designation);
        Iterator<String> keys = comp.keys();
        while (keys.hasNext()) {
            Set<Document> docs = new HashSet<>();
            String competency = keys.next();
            JSONArray keywords = comp.optJSONArray(competency);
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

            for (int i = 0; i < keywords.length(); i++) {
                String keyword = keywords.getString(i);
                boolQueryBuilder.must(QueryBuilders.termQuery("userId", userId));
                boolQueryBuilder.should(QueryBuilders.matchQuery("title", keyword).boost(2 * (keywords.length() - i)));
                boolQueryBuilder.should(QueryBuilders.matchQuery("contents", keyword).boost(keywords.length() - i));
            }

            boolQueryBuilder.minimumNumberShouldMatch(1);

            org.elasticsearch.action.search.SearchResponse response = client.prepareSearch("hackday")
                    .setTypes("hackday")
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .setQuery(boolQueryBuilder)
                    .setSize(30).execute().actionGet();
            if (response.getHits().getHits().length > 0) {
                SearchHit searchHits[] = response.getHits().getHits();
                for (int i = 0; i < searchHits.length; i++) {
                    SearchHit searchHit = searchHits[i];
                    Document document = objectMapper.readValue(searchHit.getSourceAsString(), Document.class);
                    docs.add(document);
                }
            }
            map.put(competency, docs);
        }

        return new SearchResponse(map);
    }

    private JSONObject buildCompSchema() {
        try {
            return ResourceUtils.loadResourceAsJson("/competencies.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
