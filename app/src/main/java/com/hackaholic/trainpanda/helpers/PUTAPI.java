package com.hackaholic.trainpanda.helpers;

import android.util.Log;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;


public class PUTAPI {

    public static Reader callWebservicePost(String SERVER_URL,String jsonString) {

        Reader reader = null;
        InputStream is=null;

        try {

            HttpClient client = new DefaultHttpClient();
            HttpPut put = new HttpPut(SERVER_URL);

            // parse without any json body parameters
            if(jsonString.equalsIgnoreCase("")) {
                put.setHeader("Accept", "application/text");
                put.setHeader("Content-type", "application/text");
            } else { // with json body parameters
                put.setHeader("Accept", "application/json");
                put.setHeader("Content-type", "application/json");
            }

            StringEntity e = new StringEntity(jsonString.toString(), HTTP.UTF_8);
            e.setContentType("application/json");

            put.setEntity(e);

            HttpResponse response = client.execute(put);
            Log.e("jsonString:", jsonString + "");
         //   Log.e("response:",response+"");


            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            reader = new InputStreamReader(is);

        }catch (JsonSyntaxException e) {
            Log.e("Error: ", e + "");
            e.printStackTrace();
        } catch (JsonIOException e) {
            Log.e("Error: ", e + "");
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            Log.e("Error: ", e + "");
            e.printStackTrace();
        } catch (IllegalStateException e) {
            Log.e("Error: ", e + "");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Error: ", e + "");
            e.printStackTrace();
        } catch (Exception e) {
            Log.e("Error: ", e + "");
            e.printStackTrace();
        }
        return reader;
    }


}
