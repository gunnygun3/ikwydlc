package com.danter.google.auth;

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
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;


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
     * @param authCode authentication code provided by google
     * @return JSON formatted user profile information
     */
    public String getUserInfoJson(final String authCode) throws IOException {
        // Make an authenticated request
        final GenericUrl url = new GenericUrl(USER_INFO_URL);
        final HttpRequest request = requestFactory.buildGetRequest(url);
        request.getHeaders().setContentType("application/json");
        final String jsonIdentity = request.execute().parseAsString();

        return jsonIdentity;

    }

    public String getGmailData(String authCode) throws Exception {
        // Make an authenticated request
        final GenericUrl url = new GenericUrl(THREAD_INFO_URL);
        final HttpRequest request = requestFactory.buildGetRequest(url);
        request.getHeaders().setContentType("application/json");
        final String jsonIdentity = request.execute().parseAsString();

        List<String> threadIDs = getThreadIDs( new JSONObject(jsonIdentity));
            getThreadData(threadIDs);

        return jsonIdentity;
    }

    public void getThreadData(List<String> threadIDs) throws Exception {

        for (String threadID : threadIDs) {
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
                        if ("X-Originating-Email".equals(headerName)) {
                            mailData.setOrganiser((String) header.get("value"));
                        }
                        if ("To".equals(headerName)) {
                            mailData.setParticipants(getParticipant((String) header.get("value")));
                        }
                        if ("Date".equals(headerName)) {
                            DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("EEE, DD MMM YYYY HH:mm:ss ZZ");
                            mailData.setTimestamp(dateTimeFormatter.parseDateTime("Fri, 5 Jun 2015 19:18:01 +0530").toDate());
                        }
                        if ("X-Originating-Email".equals(headerName)) {
                            mailData.setOrganiser((String) header.get("value"));
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

                System.out.println(mailData.toString());


            }


        }



    }

    public List<String> getParticipant (String toField){
        return Arrays.asList(toField.split(","));
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

    public String importData(String code) throws Exception {
        String userInfo = getUserInfoJson(code);
        String gmailData = getGmailData(code);
        System.out.println(userInfo);
        System.out.println(gmailData);
        return "Data Imported!";
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
