package Classes;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;


/**
 * Created by Ashley Morris on 14/01/2015.
 */
public class JSONParser {

    static InputStream inputStream = null;
    static JSONObject jsonObject = null;
    static String json = "";

    public JSONParser() {
    }

    /**
     * Function returns a JSON object from a url.
     *
     * @param url The url to read JSON from.
     * @return
     */

    public JSONObject getJSONFromUrl(final String url){

        //Make a http request and open an input stream
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            StatusLine statusLine = httpResponse.getStatusLine();

            if(statusLine.getStatusCode() == 200) {
                HttpEntity httpEntity = httpResponse.getEntity();
                inputStream = httpEntity.getContent();
            }
        }
        catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Use buffered reader to read the InputStream into a string.
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader
                    (inputStream, "iso-8859-1"), 8);
            StringBuilder stringBuilder = new StringBuilder();

            //Holds the JSON object as a string
            String line = null;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "n");
            }
            inputStream.close();
            json = stringBuilder.toString();

        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Parse the retrieved string to a JSONObject.
        try{
            jsonObject = new JSONObject(json);
        }
        catch (JSONException ex){
            Log.e("JSON Parser", "Error parsing data " + ex.toString());
        }

        return jsonObject;

    }

    /**
     * Get JSON from a url and specify the method used. (POST OR GET)
     * @param url The url to make a call on.
     * @param method The http method to call on the url.
     * @param params Any additional parameters to be passed. Optional parameter.
     * @return
     */
    public JSONObject makeHttpRequest(String url, String method, List<NameValuePair> params){

        try{
            if(method == "POST"){
                //request is post
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                if(params != null){
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                inputStream = httpEntity.getContent();

            }

            else if(method == "GET"){
                DefaultHttpClient httpClient = new DefaultHttpClient();

                if(params != null) {
                    String paramString = URLEncodedUtils.format(params, "utf-8");

                    //Concatenate the parameters onto the url
                    url += "?" +paramString;
                }

                //Get the url with the additional parameters
                HttpGet httpGet = new HttpGet(url);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                inputStream = httpEntity.getContent();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //Use buffered reader to read the InputStream into a string.
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader
                    (inputStream, "iso-8859-1"), 8);
            StringBuilder stringBuilder = new StringBuilder();

            //Holds the JSON object as a string
            String line = null;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "n");
            }
            inputStream.close();
            json = stringBuilder.toString();

        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Parse the retrieved string to a JSONObject.
        try{
            jsonObject = new JSONObject(json);
        }
        catch (JSONException ex){
            Log.e("JSON Parser", "Error parsing data " + ex.toString());
        }

        return jsonObject;

    }

}

