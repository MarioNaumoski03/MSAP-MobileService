package uk.ac.shef.oak.jobserviceexample;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class PostRequestAsync extends AsyncTask<String,Void,String> {

    private static String response = null;
    private static final String TAG = PostRequestAsync.class.getSimpleName();



    public PostRequestAsync() {
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            URL url = new URL("http://10.0.2.2:5000/postresults");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type","application/json; utf-8");
            conn.setRequestProperty("Accept","application/json");
            conn.setDoOutput(true);

            JSONObject msg = new JSONObject();
            msg.put("result",strings[0]);
            String jsonInputString = msg.toString();

            OutputStream os = conn.getOutputStream();
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input,0,input.length);

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(),"utf-8"));
            StringBuffer buffer = new StringBuffer();

            String responseLine = null;

            while((responseLine = br.readLine())!= null){
                buffer.append(responseLine.trim());
            }

            response = buffer.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d(TAG, s);
        Service.responseString += s;
    }
}

