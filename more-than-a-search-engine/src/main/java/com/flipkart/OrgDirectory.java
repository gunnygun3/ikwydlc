package com.flipkart;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.json.JSONObject;

import javax.jws.soap.SOAPBinding;
import javax.ws.rs.core.MediaType;

/**
 * Created by gopi.vishwakarma on 05/06/15.
 */
public class OrgDirectory {
    private final Client client;

    public OrgDirectory() {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getProperties().put(ClientConfig.PROPERTY_READ_TIMEOUT, 10000);
        clientConfig.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, 10000);

        client = Client.create(clientConfig);
    }

    public UserInfo getInfo(String userName) {
        try {
            ClientResponse response = client.resource("http://org-dir.nm.flipkart.com:38700/employeeData/email")
                    .path(userName)
                    .accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
            if (200 == response.getStatus()) {
                String responseEntity = response.getEntity(String.class);
                JSONObject json = new JSONObject(responseEntity);
                UserInfo userInfo = new UserInfo();
                userInfo.setDesignation(json.optString("designation"));
                userInfo.setTeam(json.optString("department"));
                userInfo.setName(json.optString("name"));
                userInfo.setEmail(json.optString("email"));
                return userInfo;
            } else {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus() + " for request " + userName);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
