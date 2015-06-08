package com.hackaholic.trainpanda.helpers;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * Created by Dhruvil on 04-05-2015.
 */

public abstract class GetPostClass implements Interaction {

    public abstract void response(String response);

    public abstract void error(String error);

    private String url;
    private EnumType type;
    private List<NameValuePair> pairs;


    public GetPostClass(String url, EnumType type) {
        this.url = url;
        this.type = type;
    }

    public GetPostClass(String url, List<NameValuePair> pairs, EnumType type) {
        this.url = url;
        this.type = type;
        this.pairs = pairs;
    }


    public synchronized final GetPostClass call2() {

                new OperationGet2().execute();

        return this;

    }


    public synchronized final GetPostClass call() {

        switch (type) {
            case GET:
                new OperationGet().execute();
                break;
            case POST:
                new OperationPost().execute();
                break;

        }
        return this;

    }

    public static String httpPost(String url, List<NameValuePair> params) {

        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        String responseBody = null;
        JSONObject jObject = null;
        try {

            httppost.setEntity(new UrlEncodedFormEntity(params));
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            int responseCode = response.getStatusLine().getStatusCode();
            responseBody = EntityUtils.toString(response.getEntity());
            jObject = new JSONObject(responseBody);

        } catch (ClientProtocolException e) {

        } catch (IOException e) {

        } catch (JSONException e) {
            // Oops
        } catch (Exception e) {
        }
        return responseBody.toString();
    }

    public static String httpGet(String url) {

        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httppost = new HttpGet(URI.create(url));
        String responseBody = null;
        JSONObject jObject = null;
        try {
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            int responseCode = response.getStatusLine().getStatusCode();
            responseBody = EntityUtils.toString(response.getEntity());
            jObject = new JSONObject(responseBody);

        } catch (ClientProtocolException e) {

        } catch (IOException e) {

        } catch (JSONException e) {
            // Oops
        } catch (Exception e) {
        }
        return responseBody.toString();
    }


    public class OperationGet extends AsyncTask<Void, Void, Void> {

        String response = null;

        @Override
        protected Void doInBackground(Void... params) {

            try {
                response = httpGet(url);
            } catch (Exception e) {
                e.printStackTrace();
                error("Error");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e("Response:", response.toString());
            if (response == null) {
                error("Server Error");
            } else {

                try{
                    JSONObject jobj = new JSONObject(response);
                    if(jobj.getString("message").equalsIgnoreCase("success")){
                        response(jobj.getJSONObject("description").toString());
                    }else{
                        JSONObject object = jobj.getJSONObject("description");
                        error(object.toString());
                    }
                }catch(Exception e){
                }
            }
        }
    }

    public class OperationGet2 extends AsyncTask<Void, Void, Void> {

        String response = null;

        @Override
        protected Void doInBackground(Void... params) {

            try {
                response = httpGet(url);
            } catch (Exception e) {
                e.printStackTrace();
                error("Error");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Log.e("Response:", response.toString());
            if (response == null) {
                error("Server Error");
            } else {
                response(response.toString());
            }
        }
    }




    public class OperationPost extends AsyncTask<Void, Void, Void> {

        String response = null;

        @Override
        protected Void doInBackground(Void... params) {

            try {
                response = httpPost(url, pairs);
            } catch (Exception e) {
                e.printStackTrace();
                error("Error");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e("Response:", response.toString());
            if (response == null) {
                error("Server Error");
            } else {
                try{
                    JSONObject jobj = new JSONObject(response);
                    if(jobj.getString("message").equalsIgnoreCase("success")){
                        response(jobj.getJSONObject("description").toString());
                    }else{
                        JSONObject object = jobj.getJSONObject("description");
                        error(object.toString());
                    }
                }catch(Exception e){
                }
            }
        }
    }


}

