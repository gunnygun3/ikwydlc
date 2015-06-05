package com.flipkart;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by charu.jain on 05/06/15.
 */
public class LoadDataTest {
    @Test
    public void loadDataTest() throws Exception{
        Document document = new Document();
        List<Document> list = new ArrayList<>();
        document.setTitle("xyz");
        document.setAttended(true);
        document.setContents("subject1");
        document.setOrganiser(new UserInfo());
        document.setTimestamp(new Date());
        document.setSource("CALENDAR");
        document.setUserId("charu.jain@flipkart.com");
        document.setParticipants(null);
        list.add(document);
        LoadData loadData = new LoadData();
        loadData.loadData(list);
    }
}
