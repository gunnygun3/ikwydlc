package com.flipkart;

import java.util.Collection;
import java.util.Map;

/**
 * Created by gopi.vishwakarma on 05/06/15.
 */
public class SearchResponse {
    private Map<String, Collection<Document>> documents;

    public SearchResponse(Map<String, Collection<Document>> documents) {
        this.documents = documents;
    }

    public Map<String, Collection<Document>> getDocuments() {
        return documents;
    }

}
