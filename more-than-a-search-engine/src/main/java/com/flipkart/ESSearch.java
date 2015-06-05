package com.flipkart;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
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
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("discovery.zen.ping.multicast.enabled", "false")
                .put("discovery.zen.ping.unicast.hosts", "localhost")
                .put("node.name", "local-app")
                .put("cluster.name", "zulu-hack")
                .build();
        Node node = new NodeBuilder()
                .clusterName("zulu-hack")
                .client(true)
                .data(false)
                .settings(settings)
                .build();
        node.start();
        client = node.client();
        this.competencies = buildCompSchema();
    }

    @Override
    public SearchResponse search(JSONObject userInfo) throws Exception {
        String userId = userInfo.optString("email");
        String designation = userInfo.optString("designation");

        return buildSearchRequest(userId, designation);

    }

    private SearchResponse buildSearchRequest(String userId, String designation) throws Exception {

        Set<ESDocument> docs = new HashSet<>();

        JSONObject comp = competencies.optJSONObject(designation);
        Iterator<String> keys = comp.keys();
        while (keys.hasNext()) {
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
                    .setSize(-1).execute().actionGet();
            if (response.getHits().getHits().length > 0) {
                SearchHit searchHits[] = response.getHits().getHits();
                for (int i = 0; i < searchHits.length; i++) {
                    SearchHit searchHit = searchHits[i];
                    ESDocument esDocument = objectMapper.readValue(searchHit.getSourceAsString(), ESDocument.class);
                    docs.add(esDocument);
                }
            }

        }

        return new SearchResponse(docs);
    }

    private JSONObject buildCompSchema() {
        try {
            return ResourceUtils.loadResourceAsJson("/competencies.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
