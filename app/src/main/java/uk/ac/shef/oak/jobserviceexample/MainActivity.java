/*
 * Copyright (c) 2019. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.jobserviceexample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import uk.ac.shef.oak.jobserviceexample.restarter.RestartServiceBroadcastReceiver;

public class MainActivity extends Activity {

    public static String url="https://10.0.2.2:5000/getjobs";
    ArrayList<HashMap<String,String>> valueList;
    TextView txtJson;
    ProgressDialog pd;
    static String JSONString;
    static JSONArray JArray;
    static String jsonstring;
    public static final String TAG= MainActivity.class.getSimpleName();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);
        //new yourDataTask().execute();
        new JsonTask().execute("http://10.0.2.2:5000/getjobs");
        new PingTask().execute();
        //valueList=new ArrayList<>();
       // new getValues().execute();

        finish();
    }

//public class getValues extends AsyncTask<Void,Void,Void> {
//
//    @Override
//    protected Void doInBackground(Void... voids) {
//        jsonstring = uk.ac.shef.oak.jobserviceexample.utilities.Handler.httpServiceCall(url);
//        if (jsonstring != null) {
//            try {
//                JSONObject jobj = new JSONObject(jsonstring);
//                JSONArray values = jobj.getJSONArray("Values");
//                for (int i = 0; i < values.length(); i++) {
//                    JSONObject jobj1 = values.getJSONObject(i);
//                    String date = jobj1.getString("date");
//                    String host = jobj1.getString("host");
//                    String count = jobj1.getString("count");
//                    String packetSize = jobj1.getString("packetSize");
//                    String jobPeriod = jobj1.getString("jobPeriod");
//                    String jobType = jobj1.getString("jobType");
//
//                    HashMap<String, String> valueMap = new HashMap<>();
//
//                    valueMap.put("date", date);
//                    valueMap.put("host", host);
//                    valueMap.put("count", count);
//                    valueMap.put("packetSize", packetSize);
//                    valueMap.put("jobPeriod", jobPeriod);
//                    valueMap.put("jobType", jobType);
//
//                    valueList.add(valueMap);
//                }
//            } catch (JSONException e) {
//                Toast.makeText(getApplicationContext(), "ParsingError", Toast.LENGTH_LONG).show();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), "ParsingError", Toast.LENGTH_LONG).show();
//                    }
//                });
//            }
//        } else {
//            Toast.makeText(getApplicationContext(), "ParsingError", Toast.LENGTH_LONG).show();
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(getApplicationContext(), "ParsingError", Toast.LENGTH_LONG).show();
//                }
//            });
//        }
//        return null;
//    }
//}


    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            RestartServiceBroadcastReceiver.scheduleJob(getApplicationContext());
        } else {
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(getApplicationContext());
        }
    }

    /*protected class yourDataTask extends AsyncTask<Void, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(Void... params) {

            String str = "http://10.0.2.2:5000/getjobs";
            URLConnection urlConn = null;
            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(str);
                urlConn = url.openConnection();
                Log.d(TAG,"Connection to http://10.0.2.2:5000/getjobs");
                bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

                StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                JSONString=stringBuffer.toString();
                JSONObject obj=new JSONObject();
                obj.put("the_array" , JSONString);
                //return obj;

            } catch (Exception ex) {
                Log.e("App", "yourDataTask", ex);
                return null;
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG,"JSON susscesfully get.");
            }

            return null;
        }



        @Override
        protected void onPostExecute(JSONArray response) {
            if (response != null) {
                try {
                    Log.e("App", "Success: " + response.getString(Integer.parseInt("yourJsonElement")));
                } catch (JSONException ex) {
                    Log.e("App", "Failure", ex);
                }
            }
        }
    }*/

    private class JsonTask extends AsyncTask<String, String, String> {

//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            /*pd = new ProgressDialog(MainActivity.this);
//            pd.setMessage("Please wait");
//            pd.setCancelable(false);
//            pd.show();*/
//        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

JSONString=buffer.toString();
                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

      /*  @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }
            txtJson.setText(result);
        }*/
    }
}


