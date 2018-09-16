package com.ehp.rutas.Navigation.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TaskRequestDirections extends AsyncTask<String, Void, String> {

    private static final String TAG = "TaskRequestDirections";
    GoogleMap googleMap;
    Context context;


    public TaskRequestDirections(GoogleMap googleMap, Context context) {
        this.googleMap = googleMap;
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        String responseString = "";
        try {
            responseString = requestDirection(strings[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  responseString;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        TaskParser taskParser = new TaskParser(googleMap, context);
        taskParser.execute(s);
    }

    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        Log.w(TAG, "Request Url: " + reqUrl);

        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }

        Log.w(TAG, "Reponse Directions: " + responseString);

        return responseString;
    }
}
