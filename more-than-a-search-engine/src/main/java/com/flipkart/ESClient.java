package com.flipkart;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

/**
 * User: amittal
 * Date: 06/06/15
 * Time: 2:16 AM
 */
public class ESClient {

    private static Client client;


    public static void initialize() {

        String clusterName = "hack";

        Settings settings = ImmutableSettings.settingsBuilder()
                .put("discovery.zen.ping.multicast.enabled", "false")
                .put("discovery.zen.ping.unicast.hosts", "localhost")
                .put("node.name", "local-app")
                .put("cluster.name", clusterName)
                .build();
        Node node = new NodeBuilder()
                .clusterName(clusterName)
                .client(true)
                .data(false)
                .settings(settings)
                .build();
        node.start();
        client = node.client();

    }

    public static Client getClient() {
        if (client == null) {
            initialize();
        }
        return client;
    }


}
