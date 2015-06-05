package com.flipkart;

import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * Created by gopi.vishwakarma on 05/06/15.
 */
public class ArtificialRanker {
    private final JSONObject weights;

    public ArtificialRanker() {
        weights = buildCompSchema();
    }

    private JSONObject buildCompSchema() {
        try {
            return ResourceUtils.loadResourceAsJson("/weights.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Map<String, Collection<Document>> doIntelligentRanking(Map<String, Collection<Document>> args) {

        Map<String, Collection<Document>> result = new HashMap<>();

        for (Map.Entry<String, Collection<Document>> entry : args.entrySet()) {
            List<Document> list = new ArrayList<>(entry.getValue());

            Collections.sort(list, new Comparator<Document>() {
                public int compare(Document idx1, Document idx2) {
                    double w1 = idx1.getContents().split("\\|\\|").length;
                    double w2 = idx2.getContents().split("\\|\\|").length;
                    for (UserInfo userInfo : idx1.getParticipants()) {
                        w1 += (weights.optDouble(userInfo.getDesignation()) == Double.NaN) ? 0.0 : weights.optDouble(userInfo.getDesignation());
                    }
                    for (UserInfo userInfo : idx2.getParticipants()) {
                        w2 += (weights.optDouble(userInfo.getDesignation()) == Double.NaN) ? 0.0 : weights.optDouble(userInfo.getDesignation());
                    }

                    return w1 < w2 ? 1 : (w1 > w2 ? -1 : 0);
                }
            });
            result.put(entry.getKey(), list);

        }
        return result;
    }
}
