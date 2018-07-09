package com.example.a1002732.iotc.task;

import android.os.AsyncTask;
import android.util.Log;

import com.example.a1002732.iotc.rest.RestSender;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 1002732 on 2018. 4. 4..
 */

public class InitChainTask extends AsyncTask<Void, Void, String> {

    @Override
    protected String doInBackground(Void... voids) {

        RestSender sender = new RestSender();

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("CN", "mobile");

        //String jsonStr = sender.sendPost("http://localhost:3082/initChain", params);
        String response = sender.httpPost("http://localhost:3082/initChain","CN","mobile");

        Log.d("이준환", "response: "+response);

        boolean res = false;
        String msg = "";
        JSONObject resObject = null;
        try {
            //Log.d("이준환", "InitChain : json : "+jsonStr);
            resObject = new JSONObject(response);

            res = (boolean) resObject.get("result");
            if(res) msg = "OK";
            else msg = "FAIL";
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("이준환", "InitChain JSONException Error: "+e.getMessage());

            return "101";

        } catch (Exception e){
            e.printStackTrace();
            Log.d("이준환", "InitChain Exception Error: "+e.getMessage());

            return "102";
        }
        return msg;
    }
}
