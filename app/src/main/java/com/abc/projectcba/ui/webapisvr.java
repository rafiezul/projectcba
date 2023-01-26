package com.abc.projectcba.ui;



import android.os.AsyncTask;
import android.util.Base64;
import android.os.Bundle;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.EventListener;

public class webapisvr {
    private IAsyncFetchListener fetchListener;
    private webApiAccessTask task;
    public String errormsg = "";


    public void onCreate(Bundle savedInstanceState) {
        fetchListener = null;
    }

    public interface IAsyncFetchListener extends EventListener {
        void onInit();
        void onComplete(String item);
        void onError(Throwable error);
        void onCancelled();
    }
    public  void setListener(IAsyncFetchListener listener){
        this.fetchListener = listener;
    }
    public void cancelTask(){
        task.cancelTask();
        task.cancel(true);
    }

    private  class webApiAccessTask extends AsyncTask <String, Void,String> {
        private boolean isTaskCancelled = false;
        public void cancelTask(){
            isTaskCancelled = true;
        }
        private  boolean isTaskCancelled(){
            return isTaskCancelled;
        }

        @Override
        protected void onPreExecute(){
            //if you want, start progress dialog here
            if(fetchListener != null)
                fetchListener.onInit();
        }

        @Override
        protected void onPostExecute(String result){
            if(fetchListener !=null)
                fetchListener.onComplete(result);
        }

        @Override
        protected  void onCancelled(){
            if(fetchListener!=null)
                fetchListener.onCancelled();
        }

        @Override
        protected  String doInBackground(String... urls){
            String returnvalue ="";
            String sendtxt = urls[0];

            try{
                URL url = new URL("https://obe.ums.edu.my/obe/example_api.ashx"); //in the real code, there is an ip and a port
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.connect();

                //send json data to api
                JSONObject jsonParam = new JSONObject();
                String base64 ="";
                byte[] dataByte;
                try{
                    dataByte = sendtxt.getBytes("UTF-8");
                    base64 = Base64.encodeToString(dataByte, Base64.DEFAULT);
                }catch (UnsupportedEncodingException e1){
                    //todo auto-generated catch block
                    e1.printStackTrace();
                }

                jsonParam.put("sendTxtbase64", base64);
                OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream());
                os.write(jsonParam.toString());
                os.flush();

                // receive json data from server
                InputStream inputStream = conn.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
                String line ="";
                StringBuilder sb = new StringBuilder();
                while ((line=rd.readLine())!=null){
                    sb.append((line + "\n"));
                }

                returnvalue = sb.toString();
                rd.close();
                os.close();

                conn.disconnect();

                //validate the return data in json format
                JSONObject json;
                try{
                    json = new JSONObject(returnvalue);

                }catch(Exception e){
                    errormsg = e.getMessage();
                    return  e.getMessage();
                }





            } catch(Exception e){
                if(fetchListener!=null)
                    fetchListener.onError(e);
            }


            return returnvalue;
        }



    }

    public void AppLogin(String xUserid, String xpassword){
        //cancel previous task if any
        try{
            cancelTask();
        }catch (Exception e){

        }

        //prepare json data to send
        JSONObject jsonParam;
        String sendtxt = "";
        try{
            jsonParam = new JSONObject();
            jsonParam.put("action","LOGIN");
            jsonParam.put("userid", xUserid);
            jsonParam.put("password",xpassword);
            sendtxt = jsonParam.toString();
        }catch (Exception e){

        }

        task = new webApiAccessTask();
        task.execute(new String[]{sendtxt});


    }
}












