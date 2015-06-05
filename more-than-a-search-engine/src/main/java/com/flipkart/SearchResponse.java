package com.flipkart;

import org.json.JSONObject;

import java.util.Collection;
import java.util.List;

/**
 * Created by gopi.vishwakarma on 05/06/15.
 */
public class SearchResponse {
    private Collection<ESDocument> documents;

    public SearchResponse(Collection<ESDocument> documents) {
        this.documents = documents;
    }

    public Collection<ESDocument> getDocuments() {
        return documents;
    }

}
