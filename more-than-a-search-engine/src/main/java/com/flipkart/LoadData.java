package com.flipkart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import java.util.List;

/**
 * Created by charu.jain on 05/06/15.
 */
public class LoadData {
    private final Client client;
    private final ObjectMapper objectMapper;

    public LoadData() {
        objectMapper = new ObjectMapper();
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("discovery.zen.ping.multicast.enabled", "false")
                .put("discovery.zen.ping.unicast.hosts", "localhost")
                .put("node.name", "local-app121")
                .put("cluster.name", "hack")
                .build();
        Node node = new NodeBuilder()
                .clusterName("hack")
                .client(true)
                .data(false)
                .settings(settings)
                .build();
        node.start();
        client = node.client();
    }

    public void loadData(List<Document> documentList) throws JsonProcessingException {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        for(Document document : documentList) {
            bulkRequest.add(client.prepareIndex("hackday", "hackday")
                            .setSource(objectMapper.writeValueAsString(document)
                            )
            );
        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if (bulkResponse.hasFailures()) {
            // process failures by iterating through each bulk response item
        }
    }

}
