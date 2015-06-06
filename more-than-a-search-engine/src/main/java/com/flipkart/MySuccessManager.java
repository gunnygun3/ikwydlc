package com.flipkart;

import org.apache.lucene.util.CollectionUtil;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.*;

/**
 * Created by gopi.vishwakarma on 05/06/15.
 */
public class MySuccessManager implements SuccessManager {
    private final OrgDirectory orgDirectory;
    private final Search search;
    private final ArtificialRanker ranker;

    public MySuccessManager(OrgDirectory orgDirectory, Search search, ArtificialRanker ranker) {
        this.orgDirectory = orgDirectory;
        this.search = search;
        this.ranker = ranker;
    }

    @Override
    public JSONArray tellMeWhatIdid(String userName) throws Exception {
        UserInfo userInfo = orgDirectory.getInfo(userName);
        SearchResponse responses = search.search(userInfo);
        Map<String, Collection<Document>> sortedDocs = ranker.doIntelligentRanking(responses.getDocuments());

        JSONArray toReturn = new JSONArray();

        for (Map.Entry<String, Collection<Document>> entry : sortedDocs.entrySet()) {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (Document document : entry.getValue()) {
                JSONObject review = new JSONObject();
                List<String> people = new ArrayList<>();
                for (UserInfo ui : document.getParticipants()) {
                    people.add(ui.getName());
                }
                review.put("Peers", people);
                review.put("Summary", document.getTitle());
                jsonArray.put(review);
            }
            jsonObject.put("Competency", entry.getKey());
            if (jsonArray.length() == 0) {
                jsonObject.put("Review", "Not Enough Data Points, please sync with your EM");
            } else {
                jsonObject.put("Review", jsonArray);
            }

            toReturn.put(jsonObject);
        }
        _filter(toReturn);
        return toReturn;
    }

    private void _filter(JSONArray args) {
        for (int i = 0; i < args.length(); i++) {
            JSONObject item = args.getJSONObject(i);
            JSONArray review = item.getJSONArray("Review");
            for (int j = 0; j < review.length() - 1; j++) {
                Set<String> words = new HashSet<>();
                String current = review.getJSONObject(j).getString("Summary").replaceAll("[0-9]|-|\\+","");
                String splitCurrent[] = current.split(" ");
                Collections.addAll(words, splitCurrent);

                for (int k = j + 1; k < review.length(); ) {
                    Set<String> wordsK = new HashSet<>();
                    String currentK = review.getJSONObject(k).getString("Summary").replaceAll("[0-9]|-|\\+","");
                    String splitCurrentK[] = currentK.split(" ");
                    Collections.addAll(wordsK, splitCurrentK);

                    wordsK.retainAll(words);
                    double score = ((double) wordsK.size() / (double) (splitCurrentK.length + words.size()));
                    if (score > 0.35) {
                        review.remove(k);
                    } else {
                        k++;
                    }
                }
                review.getJSONObject(j).put("Summary",current.replaceAll("Schedule| st | nd | th |\\(|\\)|Phone:.*cell",""));
            }
        }
    }


}
