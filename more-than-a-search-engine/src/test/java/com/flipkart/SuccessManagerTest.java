package com.flipkart;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.junit.Test;

/**
 * Created by gopi.vishwakarma on 05/06/15.
 */
public class SuccessManagerTest {
    @Test
    public void test() throws Exception {
        SuccessManager manager = new MySuccessManager(new OrgDirectory(), new ESSearch(new ObjectMapper()), new ArtificialRanker());
        JSONArray result = manager.tellMeWhatIdid("amittal@flipkart.com");

        System.out.println(result);
    }
}
