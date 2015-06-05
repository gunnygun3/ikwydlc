package com.flipkart;

import org.json.JSONObject;
import org.junit.Test;

/**
 * Created by gopi.vishwakarma on 05/06/15.
 */
public class OrgDirectoryTest {
    @Test
    public void testOrgDirectory() throws Exception{
        OrgDirectory orgDirectory = new OrgDirectory();
        UserInfo res = orgDirectory.getInfo("charu.jain@flipkart.com");
        System.out.println(res);
    }
}
