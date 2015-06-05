package com.flipkart;

import org.json.JSONObject;

/**
 * Created by gopi.vishwakarma on 05/06/15.
 */
public interface Search {
    /**
     * do search
     * @param userInfo
     * @return
     */
    SearchResponse search(UserInfo userInfo) throws Exception;
}
