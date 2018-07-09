package com.example.a1002732.iotc.rest;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 1002732 on 2018. 4. 4..
 */

public class RestSender {

    final static String openWeatherURL = "http://api-ropsten.etherscan.io/api?module=account&action=txlist&address=0x6F213A598Be7058a4248eaf0a2593210Fa8B71c3&startblock=0&endblock=99999999&sort=asc&apikey=YourApiKeyToken";


    public List<Transaction> getTransactiom() {

        List<Transaction> transactions = null;

        String urlString = openWeatherURL;

        try {
            // call API by using HTTPURLConnection
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            JSONObject json = new JSONObject(getStringFromInputStream(in));

            System.out.println(json.toString());

            // parse JSON
            transactions = parseJSON(json);
        } catch (MalformedURLException e) {
            System.err.println("Malformed URL");
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            System.err.println("JSON parsing error");
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            System.err.println("URL Connection failed");
            e.printStackTrace();
            return null;
        }


        // set Weather Object
        return transactions;

    }


    public String sendGet(String requestUrl) {
        String urlString = requestUrl;
        JSONObject json =null;
        URL url = null;

        try {
            url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            json = new JSONObject(getStringFromInputStream(in));

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return e.getMessage();
        } catch (JSONException e) {
            e.printStackTrace();
            return e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }

        return json.toString();
    }

    public String sendGetRobot(String requestUrl) {
        String urlString = requestUrl;
        URL url = null;
        String res = "";
        try {
            url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            res = getStringFromInputStream(in);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return e.getMessage();
        }  catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }

        return res;
    }


    public String httpPost(String pUrl, String key, String val){
        StringBuilder sb = new StringBuilder();

        String http = pUrl;


        HttpURLConnection urlConnection=null;
        try {
            URL url = new URL(http);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.setRequestProperty("Host", "android.schoolportal.gr");
            urlConnection.connect();

            //Create JSONObject here
            JSONObject jsonParam = new JSONObject();
            jsonParam.put(key, val);
            OutputStreamWriter out = new   OutputStreamWriter(urlConnection.getOutputStream());
            out.write(jsonParam.toString());
            out.close();

            int HttpResult =urlConnection.getResponseCode();
            if(HttpResult ==HttpURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream(),"utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();

                System.out.println(""+sb.toString());

            }else{
                System.out.println(urlConnection.getResponseMessage());
            }
        } catch (MalformedURLException e) {

            e.printStackTrace();
        }
        catch (IOException e) {

            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }finally{
            if(urlConnection!=null)
                urlConnection.disconnect();
        }

        return sb.toString();
    }


    public String httpPostPin(String pUrl, JSONObject jsonParam){
        StringBuilder sb = new StringBuilder();

        String http = pUrl;
        //{"pin":"98aecc","secret":"FiK\/mJIo448+qDfFnvz\/\/eLX2tY13fOQyE8itUJrQVMzka7QHcllXt8hZ4zbfX9M161TI9e850v1F7jhxAuW+k5BwaF3B0be5OV0oatYz6dpboW6iLM3iJAcd\/rWz9l\/8HdtOzcy3YFVluLZYXABwR2wVhwMY+ehfdQmel3CLADcNNsj\/T8YK8yyTwOKZOIBO+DSgT6xKCihzkvW7VdvdC6\/XGQfJkgWnCmtsA==","ip":"192.168.255.70","CN":"light"}

        HttpURLConnection urlConnection=null;
        try {
            URL url = new URL(http);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.setRequestProperty("Host", "android.schoolportal.gr");
            urlConnection.connect();

            //Create JSONObject here
            OutputStreamWriter out = new   OutputStreamWriter(urlConnection.getOutputStream());
            out.write(jsonParam.toString());
            out.close();

            int HttpResult =urlConnection.getResponseCode();
            if(HttpResult ==HttpURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream(),"utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();

                System.out.println(""+sb.toString());

            }else{
                System.out.println(urlConnection.getResponseMessage());
            }
        } catch (MalformedURLException e) {

            e.printStackTrace();
        }
        catch (IOException e) {

            e.printStackTrace();
        }finally{
            if(urlConnection!=null)
                urlConnection.disconnect();
        }

        return sb.toString();
    }

    public String sendPost(String requestUrl, Map<String, Object> pParams){

        URL url = null;
        JSONObject json = null;
        try {
            url = new URL(requestUrl);
            Map<String, Object> params = pParams;

            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }




            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes); // POST 호출

            InputStream in = new BufferedInputStream(conn.getInputStream());
            json = new JSONObject(getStringFromInputStream(in));

            in.close();

        } catch (MalformedURLException e) {
            Log.d("이준환", "MalformedURLException: " + e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            Log.d("이준환", "UnsupportedEncodingException: " + e.getMessage());
            e.printStackTrace();
        } catch (ProtocolException e) {
            Log.d("이준환", "ProtocolException: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("이준환", "IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            Log.d("이준환", "JSONException: " + e.getMessage());
            e.printStackTrace();
        }


        return json.toString();
    }


    private List<Transaction> parseJSON(JSONObject json) throws JSONException {

        List<Transaction> transactions = new ArrayList<>();

        JSONArray array = (JSONArray) json.get("result");


        int size = array.length();

        for (int i = 0; i < size; i++) {
            JSONObject obj = (JSONObject) array.get(i);

            Transaction transaction = new Transaction();
            transaction.setBlockNumber((String) obj.get("blockNumber"));
            transaction.setHash((String) obj.get("hash"));
            transaction.setTimeStamp((String) obj.get("timeStamp"));

            transactions.add(transaction);
        }
        return transactions;
    }

    public List<String> parseJSONBlockList(JSONObject json) throws JSONException {
        //Log.d("이준환", "parseJSONBlockList JSONObject : "+String.valueOf(String.valueOf(json)));
        JSONArray nodeList = (JSONArray) json.get("data");

        List<String> cnList = new ArrayList<>();
        for(int i = 0 ; i< nodeList.length(); i++){
            JSONObject node = nodeList.getJSONObject(i);
            JSONObject pubkeyObj = new JSONObject(node.getString("pubkey"));

            // 나의 device 등록
            cnList.add(pubkeyObj.getString("CN"));
        }

        //String [] t = String.valueOf(json).split("###");
        //Log.d("이준환", "parseJSONBlockList t[1] : "+t[1]);



        return cnList;
    }

    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;

        try {
            br = new BufferedReader(new InputStreamReader(is));

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

}
