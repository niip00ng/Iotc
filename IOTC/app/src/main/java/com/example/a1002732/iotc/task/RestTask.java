package com.example.a1002732.iotc.task;

import android.os.AsyncTask;

import com.example.a1002732.iotc.rest.RestSender;
import com.example.a1002732.iotc.rest.Transaction;

import java.util.List;

/**
 * Created by 1002732 on 2018. 4. 4..
 */

public class RestTask extends AsyncTask<Integer,Void, List<Transaction>> {

    @Override
    protected List<Transaction> doInBackground(Integer... integers) {
        RestSender sender = new RestSender();

        List<Transaction> transactions = sender.getTransactiom();

        return transactions;
    }
}


