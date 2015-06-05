package com.flipkart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import com.flipkart.config.SuperServiceConfig;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * Created by gopi.vishwakarma on 06/06/15.
 */
public class SuperService extends Application<SuperServiceConfig> {

    public static void main(String[] args) throws Exception {
        new SuperService().run(args);
    }

    @Override
    public void initialize(Bootstrap<SuperServiceConfig> bootstrap) {

    }

    @Override
    public void run(SuperServiceConfig configuration, Environment environment) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JsonOrgModule());
        SuccessManager successManager = new MySuccessManager(new OrgDirectory(), new ESSearch(objectMapper), new ArtificialRanker());
        SuperServiceResource resource = new SuperServiceResource(successManager, objectMapper);

        environment.jersey().register(resource);
    }

    @Override
    public String getName() {
        return "super-service";
    }
}
