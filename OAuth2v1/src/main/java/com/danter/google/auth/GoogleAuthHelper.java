package com.danter.google.auth;

import com.flipkart.Document;
import com.flipkart.LoadData;
import com.flipkart.OrgDirectory;
import com.flipkart.UserInfo;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * A helper class for Google's OAuth2 authentication API.
 *
 * @author Matyas Danter
 * @version 20130224
 */
public final class GoogleAuthHelper {

    /**
     * Please provide a value for the CLIENT_ID constant before proceeding, set this up at https://code.google.com/apis/console/
     */
    private static final String CLIENT_ID = "1043164348577-4vpjr45l35ut9nh68msccj3udtvild8q.apps.googleusercontent.com";
    /**
     * Please provide a value for the CLIENT_SECRET constant before proceeding, set this up at https://code.google.com/apis/console/
     */
    private static final String CLIENT_SECRET = "gXZDGvOoFlx_tm0ZQQk4CzFy";

    /**
     * Callback URI that google will redirect to after successful authentication
     */
    private static final String CALLBACK_URI = "http://localhost:8080/OAuth2v1/data.jsp";

    // start google authentication constants
    private static final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
    private static final String THREAD_INFO_URL = "https://www.googleapis.com/gmail/v1/users/me/threads?labelIds=SENT";
    private static final String THREAD_DATA_URL = "https://www.googleapis.com/gmail/v1/users/me/threads/";
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    // end google authentication constants

    private static final Collection<String> SCOPE = Arrays.asList("https://www.googleapis.com/auth/gmail.readonly;https://www.googleapis.com/auth/calendar.readonly;https://www.googleapis.com/auth/userinfo.profile;https://www.googleapis.com/auth/userinfo.email".split(";"));

    private String stateToken;

    private final GoogleAuthorizationCodeFlow flow;

    private GoogleTokenResponse response;
    private Credential credential;
    private HttpRequestFactory requestFactory;


    /**
     * Constructor initializes the Google Authorization Code Flow with CLIENT ID, SECRET, and SCOPE
     */
    public GoogleAuthHelper(String authCode) throws IOException {
        flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT,
                JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, SCOPE).build();

        generateStateToken();
        if (authCode != null) {
            response = flow.newTokenRequest(authCode).setRedirectUri(CALLBACK_URI).execute();
            credential = flow.createAndStoreCredential(response, null);
            requestFactory = HTTP_TRANSPORT.createRequestFactory(credential);
        }
    }

    /**
     * Builds a login URL based on client ID, secret, callback URI, and scope
     */
    public String buildLoginUrl() {

        final GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();

        return url.setRedirectUri(CALLBACK_URI).setState(stateToken).build();
    }

    /**
     * Generates a secure state token
     */
    private void generateStateToken() {

        SecureRandom sr1 = new SecureRandom();

        stateToken = "google;" + sr1.nextInt();

    }

    /**
     * Accessor for state token
     */
    public String getStateToken() {
        return stateToken;
    }

    /**
     * Expects an Authentication Code, and makes an authenticated request for the user's profile information
     *
     * @return JSON formatted user profile information
     */
    public String getUserInfoJson() throws IOException {
        // Make an authenticated request
        final GenericUrl url = new GenericUrl(USER_INFO_URL);
        final HttpRequest request = requestFactory.buildGetRequest(url);
        request.getHeaders().setContentType("application/json");
        final String jsonIdentity = request.execute().parseAsString();

        return jsonIdentity;

    }

    public List<Document> getGmailData(JspWriter out) throws Exception {
        // Make an authenticated request
        final GenericUrl url = new GenericUrl(THREAD_INFO_URL);
        final HttpRequest request = requestFactory.buildGetRequest(url);
        request.getHeaders().setContentType("application/json");
        final String jsonIdentity = request.execute().parseAsString();
        String emailId = getEmailId();

        List<String> threadIDs = getThreadIDs( new JSONObject(jsonIdentity));
        List<MailData> threadData = getThreadData(threadIDs, out);
        OrgDirectory orgDirectory = new OrgDirectory();

        List<Document> gmailDocument = new ArrayList<Document>();
        for (MailData mailData: threadData){
            Document document = new Document();
            document.setTimestamp(mailData.getTimestamp());
            document.setTitle(mailData.getTitle());
            document.setAttended(false);
            document.setContents(StringUtils.join(mailData.getBodyList(), "||"));
            document.setOrganiser(orgDirectory.getInfo( mailData.getOrganiser()));
            document.setSource("EMAIL");
            document.setUserId(emailId);

            List<UserInfo> particpantInfo = new ArrayList<UserInfo>();

            for (String participant : mailData.getParticipants()){
                particpantInfo.add(orgDirectory.getInfo(participant));
            }
            document.setParticipants(particpantInfo);
            gmailDocument.add(document);
        }
        return gmailDocument;
    }

    public List<MailData> getThreadData(List<String> threadIDs, JspWriter out) throws Exception {

        List<MailData> completeMailData = new ArrayList<MailData>();

        int cnt = 0;
        for (String threadID : threadIDs) {
            if (cnt++ % 10 == 0) {
                out.println("Imported email thread..." + cnt);
                out.flush();
            }
            MailData mailData = new MailData();
            GenericUrl url = new GenericUrl(THREAD_DATA_URL + threadID );
            HttpRequest request = requestFactory.buildGetRequest(url);
            request.getHeaders().setContentType("application/json");
            JSONObject jsonResult = new JSONObject(request.execute().parseAsString());
            JSONArray messages = jsonResult.getJSONArray("messages");
            for (int i = 0; i < messages.length(); i++)
            {
                JSONObject objectInArray = messages.getJSONObject(i);
                JSONObject midPayload = (JSONObject) objectInArray.get("payload");
                JSONArray headers = (JSONArray) midPayload.get("headers");
                if (i == 0){
                    for (int j =0; j< headers.length(); j++){
                        JSONObject header = headers.getJSONObject(j);
                        String headerName = (String) header.get("name");
                        if ("From".equals(headerName)) {
                            mailData.setOrganiser(getParticipant((String)header.get("value")).get(0));
                        }
                        if ("To".equals(headerName)) {
                            mailData.setParticipants(getParticipant((String) header.get("value")));
                        }
                        if ("Date".equals(headerName)) {
                            try {
                                String d = (String) header.get("value");
                                d = d.replaceAll(" \\(.*?\\)","");
                                DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("EEE, DD MMM YYYY HH:mm:ss ZZ");
                                mailData.setTimestamp(dateTimeFormatter.parseDateTime(d).toDate());
                            } catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                        if ("Subject".equals(headerName)) {
                            mailData.setTitle((String) header.get("value"));
                        }
                    }
                }

                try {

                    JSONArray messagePart = (JSONArray) midPayload.get("parts");
                    JSONObject partObject = (JSONObject) messagePart.get(0);
                    JSONObject bodyObject = (JSONObject) partObject.get("body");

                    try {

                        String encodedMessage = (String) bodyObject.get("data");
                        byte[] valueDecoded = Base64.decodeBase64(encodedMessage);
                        mailData.addMessage(new String(valueDecoded));

                    } catch (Exception ignore) {

                    }

                }
                catch (Exception ignore){

                }
                completeMailData.add(mailData);


            }


        }

        return completeMailData;

    }

    public List<String> getParticipant (String toField){
        List<String> participants = Arrays.asList(toField.split(","));
        List<String> outParticipants = new ArrayList<String>();

        for(String participant:participants){
            if (participant.contains("<")){
                outParticipants.add(participant.substring(participant.indexOf("<")+1, participant.indexOf(">")));
            }
            else if (participant.contains("@")){
                outParticipants.add(participant);
            }
        }
        return outParticipants;
    }

    public List<String> getThreadIDs (JSONObject object ){
        List<String> threadIDs = new ArrayList<String>();

        JSONArray threadArray = (JSONArray) object.get("threads");

        for (int i = 0; i < threadArray.length(); i++)
        {
            JSONObject objectInArray = threadArray.getJSONObject(i);
            threadIDs.add((String) objectInArray.get("id"));
        }
        return threadIDs;
    }
    

    private static final String CALANDER_INFO_URL = "https://www.googleapis.com/calendar/v3/calendars/<EMAIL_ID>/events?timeMax=<END_TIME>&timeMin=<ST_TIME>";


    private List<Document> getCalanderData(String stDate, String endDate) throws Exception {
        // Make an authenticated request
        String userEmailId = getEmailId();
        DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime();
        String urlStr  = CALANDER_INFO_URL.replace("<EMAIL_ID>", URLEncoder.encode(getEmailId(), "UTF-8")).replace("<ST_TIME>", URLEncoder.encode(dateTimeFormatter.print(DateTime.parse(stDate)), "UTF-8")).replace("<END_TIME>", URLEncoder.encode(dateTimeFormatter.print(DateTime.parse(endDate)), "UTF-8"));
        System.out.println(urlStr);
        final GenericUrl url = new GenericUrl(urlStr);
        final HttpRequest request = requestFactory.buildGetRequest(url);
        request.getHeaders().setContentType("application/json");
        final String jsonStr = request.execute().parseAsString();
        JsonNode calNode = mapper.readTree(jsonStr);
        JsonNode items = calNode.get("items");

        List<Document> esDocumentList = Lists.newArrayList();


        OrgDirectory orgDirectory = new OrgDirectory();

        for (JsonNode item : items) {
            JsonNode organizer = item.get("organizer");
            String createdBy = null;
            if (organizer != null) {
                createdBy = organizer.get("email") != null ? organizer.get("email").getTextValue(): null;
            }
            String title = item.get("summary") != null ? item.get("summary").getTextValue() : null;
            String desc = item.get("description") != null ? item.get("description").getTextValue() : null;
            String timestamp = null;
            if (item.get("start") != null) {
                JsonNode jsonNode = item.get("start").get("date");
                if (jsonNode != null)
                    timestamp = jsonNode.getTextValue();
            }
            boolean accepted = false;
            List<String> participants = Lists.newArrayList();
            JsonNode parts = item.get("attendees");
            if (parts != null) {
                for (JsonNode part : parts) {
                    String email = part.get("email").getTextValue();
                    if (!email.startsWith("flipkart.com")) {
                        participants.add(email);
                    }
                    if (email.equalsIgnoreCase(userEmailId)) {
                        if ("accepted".equalsIgnoreCase(part.get("responseStatus").getTextValue())) {
                            accepted = true;
                        }
                    }
                }
            }
            Document doc = new Document();

            if (createdBy != null)
                doc.setOrganiser(orgDirectory.getInfo(createdBy));
            doc.setTitle(title);
            doc.setContents(desc);
            doc.setSource("CALENDAR");
            if (timestamp != null)
                doc.setTimestamp(new DateTime(timestamp).toDate());
            doc.setUserId(userEmailId);

            List<UserInfo> attendants = Lists.newArrayList();
            for (String part : participants) {
                attendants.add(orgDirectory.getInfo(part));
            }
            doc.setParticipants(attendants);
            doc.setAttended(accepted);

            esDocumentList.add(doc);
        }
        return esDocumentList;
    }

    ObjectMapper mapper = new ObjectMapper();

    public String importData(String stDate, String endDate, JspWriter out) throws Exception {
        String userInfo = getUserInfoJson();
        out.println("Started import for user " + userInfo);

        List<Document> gmailData = getGmailData(out);
        out.println("Total sent email retrieved = " + gmailData.size());

        List<Document> calendarData = getCalanderData(stDate, endDate);

        out.println("Total calendar events retrieved: " + calendarData.size());

        LoadData loadData = new LoadData();
        loadData.loadData(gmailData);
        loadData.loadData(calendarData);

        return "Data Imported!";
    }

    public String getEmailId() throws IOException {
        String userInfo = getUserInfoJson();
        JsonNode userNode = mapper.readTree(userInfo);
        String emailId = userNode.get("email").getTextValue();
        return emailId;
    }

    public String getName() throws IOException {
        String userInfo = getUserInfoJson();
        JsonNode userNode = mapper.readTree(userInfo);
        String emailId = userNode.get("name").getTextValue();
        return emailId;
    }

	/*public static void listThreadsWithLabels (Gmail service, String userId,
                                              List<String> labelIds) throws IOException {
		ListThreadsResponse response = service.users().threads().list(userId).setLabelIds(labelIds).execute();
		List<Thread> threads = new ArrayList<Thread>();
		while(response.getThreads() != null) {
			threads.addAll(response.getThreads());
			if(response.getNextPageToken() != null) {
				String pageToken = response.getNextPageToken();
				response = service.users().threads().list(userId).setLabelIds(labelIds)
						.setPageToken(pageToken).execute();
			} else {
				break;
			}
		}

		List<String> toret = Lists.newArrayList();
		for(Thread thread : threads) {
			toret.add(thread.toPrettyString());
		}
	}*/
    
}
