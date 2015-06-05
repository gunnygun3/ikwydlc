package com.flipkart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;

import java.util.List;

/**
 * Created by charu.jain on 05/06/15.
 */
public class LoadData {
    private final Client client;
    private final ObjectMapper objectMapper;

    public LoadData() {
        objectMapper = new ObjectMapper();
        client = ESClient.getClient();
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
