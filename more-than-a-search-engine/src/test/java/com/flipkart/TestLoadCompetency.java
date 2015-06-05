package com.flipkart;

import org.json.JSONObject;
import org.junit.Test;

/**
 * Created by gopi.vishwakarma on 05/06/15.
 */
public class TestLoadCompetency {
    @Test
    public void test() throws Exception{
        JSONObject comp = ResourceUtils.loadResourceAsJson("/competencies.json");
        System.out.println(comp);
    }
}
