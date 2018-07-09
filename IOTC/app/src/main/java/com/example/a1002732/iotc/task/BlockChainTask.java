package com.example.a1002732.iotc.task;

import android.os.AsyncTask;
import android.util.Log;

import com.example.a1002732.iotc.rest.RestSender;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 1002732 on 2018. 4. 4..
 */

public class BlockChainTask extends AsyncTask<Void, Void, String> {

    @Override
    protected String doInBackground(Void... voids) {

        RestSender sender = new RestSender();

        String jsonStr = sender.sendGet("http://localhost:3082/getBlockChain");
        String msg = "";
        JSONObject resObject = null;
        try {
            Log.d("이준환", "BlockChainTask: json : "+jsonStr);
            resObject = new JSONObject(jsonStr);
            JSONArray dataList = (JSONArray) resObject.get("data");

            if(dataList.length() == 0){
                msg = "FAIL";
            }else {
                Log.d("이준환", "BlockChainTask: resObject :" + resObject.get("data"));
                msg = "OK";
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("이준환", "BlockChainTask JSONException Error: "+e.getMessage());

            return "101";

        } catch (Exception e){
            e.printStackTrace();
            Log.d("이준환", "BlockChainTask Exception Error: "+e.getMessage());

            return "102";
        }
        return msg;
    }
}
