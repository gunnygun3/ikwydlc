package com.flipkart;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Singleton;
import org.json.JSONArray;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by gopi.vishwakarma on 06/06/15.
 */
@Path("/super-service")
@Singleton
public class SuperServiceResource {

    private final SuccessManager successManager;
    private final ObjectMapper objectMapper;

    public SuperServiceResource(SuccessManager successManager, ObjectMapper objectMapper) {
        this.successManager = successManager;
        this.objectMapper = objectMapper;
    }

    @Timed
    @GET
    @Path("{userId}")
    public String tellMeWhatIdid(@PathParam("userId") String userId) {
        try {
            JSONArray jsonArray = successManager.tellMeWhatIdid(userId);
            return objectMapper.writeValueAsString(jsonArray);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
