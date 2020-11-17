package ca.retrylife.mc.remoteplayers.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;

/**
 * HTTP utils
 */
public class HTTP {

    /**
     * Make an HTTP request, and deserialize
     * 
     * @param <T>      Type
     * @param endpoint URL to request from
     * @param clazz    Type class
     * @return Deserialized object
     * @throws IOException
     */
    public static <T> T makeJSONHTTPRequest(URL endpoint, Class clazz) throws IOException {

        // Open an HTTP request
        HttpURLConnection request = (HttpURLConnection) endpoint.openConnection();
        request.setRequestMethod("GET");
        request.setRequestProperty("Content-Type", "application/json");
        request.setInstanceFollowRedirects(true);

        // Get the content
        BufferedReader responseReader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder response = new StringBuilder();
        String output;
        while ((output = responseReader.readLine()) != null) {
            response.append(output);
        }

        // Turn to a Java object
        Gson gson = new Gson();
        return (T) gson.fromJson(response.toString(), clazz);

    }

}