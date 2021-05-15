package uk.ac.shef.oak.jobserviceexample;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PingTask extends AsyncTask<Void,Void,String> {

    private static final String TAG = PingTask.class.getSimpleName();

    public PingTask() {

    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            Thread.sleep(600000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return MainActivity.JSONString;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try {
            JSONArray jarray = new JSONArray(s);
            for (int i=0; i < jarray.length(); i++) {
                JSONObject currentItem = jarray.getJSONObject(i);

                Log.d(TAG, "json = " + currentItem.toString());
                Log.d(TAG, "type = " + currentItem.getString("jobType"));
                Log.d(TAG, "host = " + currentItem.getString("host"));
                Log.d(TAG, "count = " + currentItem.getString("count"));
                Log.d(TAG, "packetSize = " + currentItem.getString("packetSize"));
                Log.d(TAG, "Period = " + currentItem.getString("jobPeriod"));
                Log.d(TAG, "date = " + currentItem.getString("date"));


                String pingCmd = "ping  -c  " + currentItem.getString("count");
                pingCmd = pingCmd + " -s " + currentItem.getString("packetSize");
                pingCmd = pingCmd + " " + currentItem.getString("host");
                String pingResult = "";
                Runtime r = Runtime.getRuntime();
                Process p = r.exec(pingCmd);
                BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    pingResult += inputLine;
                }
                in.close();
                Log.d(TAG, "pingResult " + pingResult);
                Service.pingResult = pingResult;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}