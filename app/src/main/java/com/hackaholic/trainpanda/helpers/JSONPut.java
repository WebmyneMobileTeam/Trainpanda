package com.hackaholic.trainpanda.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.Reader;

/**
 * Created by Android on 03-04-2015.
 */
public class JSONPut {

    private ProgressDialog progressDialog;
    private String responseMsg;
    private ResponseListener responseListener;
    private POSTResponseListener postResponseListener;

    public void setResponseListener(ResponseListener listener){
        this.responseListener = listener;
    }

    public void setPostResponseListener(POSTResponseListener listener){
        this.postResponseListener = listener;
    }


    public void POST(final Context ctx, final String link, final String jsonstring,final String dialogText) {

        Log.e("JSON", "POST link: " + link + " json: " + jsonstring);

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog =new ProgressDialog(ctx);
                progressDialog.setMessage(dialogText);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try{
                    Reader readerForNone = PUTAPI.callWebservicePost(link, jsonstring);
                    StringBuffer response = new StringBuffer();
                    int i = 0;
                    do {
                        i = readerForNone.read();
                        char character = (char) i;
                        response.append(character);

                    } while (i != -1);
                    readerForNone.close();

                    response.setLength(response.length() - 1);
                    responseMsg = response.toString();

                    Log.e("JSON", "JSON response: " + responseMsg);

                } catch (Exception e){
                    Log.e("JSON", "JSON response exception: " + e.toString());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                postResponseListener.onPost(responseMsg);
                progressDialog.dismiss();
            }
        }.execute();
    }




}
