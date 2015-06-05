package com.flipkart;

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
    public JSONArray tellMeWhatIdid(String userEmail) throws Exception {
        UserInfo userInfo = orgDirectory.getInfo(userEmail);
        SearchResponse responses = search.search(userInfo);
        Map<String, Collection<Document>> sortedDocs = ranker.doIntelligentRanking(responses.getDocuments());

        JSONArray toReturn = new JSONArray();
        for (Map.Entry<String, Collection<Document>> entry : sortedDocs.entrySet()) {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (Document document : entry.getValue()) {
                JSONObject review = new JSONObject();
                Map<String,Integer> potentialReviewers = new HashMap<>();
                for (UserInfo ui : document.getParticipants()) {
                    String participant = ui.getName();
                    if(potentialReviewers.containsKey(participant)){
                        potentialReviewers.put(participant,potentialReviewers.get(participant)+1);
                    }
                    else{
                        potentialReviewers.put(participant,1);
                    }
                }
                Map<String, Integer> sortedPotentialReviewers = sortByComparator(potentialReviewers);
                List<String> people = new ArrayList<>();
                int count=0;
                for(Map.Entry<String,Integer> entry1: sortedPotentialReviewers.entrySet()) {
                    if(count<5){
                        if(!entry1.getKey().equalsIgnoreCase(userInfo.getName())) {
                            people.add(entry1.getKey());
                            count++;
                        }
                    }
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
        return toReturn;
    }

    private Map<String, Integer> sortByComparator(Map<String, Integer> potentialReviewers) {
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(potentialReviewers.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // Convert sorted map back to a Map
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }


}
