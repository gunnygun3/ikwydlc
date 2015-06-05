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
        UserInfo userInfo = new UserInfo();
        userInfo.setEmail("charu.jain@flipkart.com");
        userInfo.setDesignation("Architect");

        SearchResponse response = search.search(userInfo);
        System.out.println(response.getDocuments().size());
        System.out.println(response.getDocuments());

    }

    @Test
    public void testPipe() throws  Exception{
        String str = "abc pqr";
        String ans [] = str.split("\\|\\|");
        System.out.println(ans.length + " === " + str.length());
    }
}
