package com.flipkart;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by gopi.vishwakarma on 05/06/15.
 */
public class MySuccessManager implements SuccessManager {
    private final OrgDirectory orgDirectory;
    private final Search search;

    public MySuccessManager(OrgDirectory orgDirectory, Search search) {
        this.orgDirectory = orgDirectory;
        this.search = search;
    }

    @Override
    public JSONObject tellMeWhatIdid(String userName) throws Exception {
        UserInfo userInfo = orgDirectory.getInfo(userName);
        SearchResponse responses = search.search(userInfo);

        return null;
    }

}
