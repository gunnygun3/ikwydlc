package com.flipkart;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

/**
 * Created by gopi.vishwakarma on 05/06/15.
 */
public class ResourceUtils {
    /**
     * Helper method to load schema from resource as JSON
     */
    public static JSONObject loadResourceAsJson(String resourcePath) throws JSONException, IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader br = null;
        InputStreamReader inputStreamReader = null;
        try {
            InputStream ips = ResourceUtils.class.getResourceAsStream(resourcePath);
            inputStreamReader = new InputStreamReader(ips);
            br = new BufferedReader(inputStreamReader);
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }
        } finally {
            closeQuietly(br);
            closeQuietly(inputStreamReader);
        }
        return new JSONObject(stringBuilder.toString());
    }

    private static void closeQuietly(Reader reader) {
        try {
            if (reader != null) reader.close();
        } catch (Exception ignore) {

        }
    }
}
