package com.example.a1002732.iotc.task;

import android.os.AsyncTask;
import android.util.Log;

import com.example.a1002732.iotc.rest.RestSender;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by 1002732 on 2018. 4. 4..
 */

public class BlockChainListTask extends AsyncTask<Void, Void, List<String>> {

    @Override
    protected List<String> doInBackground(Void... voids) {

        RestSender sender = new RestSender();

        String jsonStr = sender.sendGet("http://localhost:3082/getBlockChain");
        boolean res = false;
        String msg = "";
        JSONObject resObject = null;

        List<String> list = null;
        try {
            resObject = new JSONObject(jsonStr);

            res = (boolean) resObject.get("result");

            if(res) {
                list = sender.parseJSONBlockList(resObject);
            }else{
                return null;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("이준환", "BlockChainListTask JSONException Error: "+e.getMessage());

            return null;

        } catch (Exception e){
            e.printStackTrace();
            Log.d("이준환", "BlockChainListTask Exception Error: "+e.getMessage());

            return null;
        }
        return list;
    }
}
