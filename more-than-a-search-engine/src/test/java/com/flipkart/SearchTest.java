package com.flipkart;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by gopi.vishwakarma on 05/06/15.
 */
public class SearchTest {
    @Test
    public void test() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Search search =  new ESSearch(objectMapper);
        JSONObject userInfo = new JSONObject();
        userInfo.put("email","charu.jain@flipkart.com");
        userInfo.put("designation","Architect");

        SearchResponse response = search.search(userInfo);
        System.out.println(response.getDocuments().size());
        System.out.println(response.getDocuments());

    }
}
