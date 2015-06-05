package com.flipkart;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;
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
                    double w1 = (StringUtils.isNotEmpty(idx1.getContents()))? idx1.getContents().split("\\|\\|").length : 0.0;
                    double w2 = (StringUtils.isNotEmpty(idx2.getContents()))? idx2.getContents().split("\\|\\|").length : 0.0;

                    w1 = getWeights(idx1, w1);
                    w2 = getWeights(idx2, w2);

                    if (idx1.isAttended()) w1 += 5.0;
                    if (idx2.isAttended()) w2 += 5.0;

                    if(idx1.getUserId().equalsIgnoreCase(idx1.getOrganiser() != null ? idx1.getOrganiser().getEmail() : "")) w1 += 7.0;
                    if(idx2.getUserId().equalsIgnoreCase(idx2.getOrganiser()!=null ? idx2.getOrganiser().getEmail() : "")) w2 += 7.0;

                    return w1 < w2 ? 1 : (w1 > w2 ? -1 : 0);
                }

                private double getWeights(Document idx2, double w2) {
                    for (UserInfo userInfo : idx2.getParticipants()) {
                        w2 += (weights.optDouble(userInfo.getDesignation()) == Double.NaN) ? 0.5 : weights.optDouble(userInfo.getDesignation());
                    }
                    return w2;
                }
            });
            result.put(entry.getKey(), list);

        }
        return result;
    }
}
