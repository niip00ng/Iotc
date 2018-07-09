package com.example.a1002732.iotc.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a1002732.iotc.MainActivity;
import com.example.a1002732.iotc.R;
import com.example.a1002732.iotc.rest.RestSender;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class PincodeDialog extends Dialog implements View.OnClickListener{

    ImageView pinButton;
    ImageView robotPinButton;
    TextView pinCode;
    String ip;
    JSONObject returnObj = null;
    Context context=null;
    String title = "";
    LinearLayout layout = null;
    Timer timer = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.activity_pincode_dialog);
        robotPinButton = (ImageView) findViewById(R.id.robotPinButton);
        pinButton = (ImageView) findViewById(R.id.pinButton);
        pinCode = (TextView) findViewById(R.id.pinText);
        layout= (LinearLayout) findViewById(R.id.pin_layout);


        Log.d("이준환", "TITLE / IP : "+ title+" / "+ip);

        if(title.equals("robot")){
            layout.setBackgroundResource(R.drawable.robot_tag);
            pinCode.setVisibility(View.GONE);
            pinButton.setVisibility(View.GONE);
            robotPinButton.setVisibility(View.VISIBLE);
        }else if(title.equals("mobile")){
            layout.setBackgroundResource(R.drawable.myphone);
            pinCode.setVisibility(View.GONE);
            pinButton.setVisibility(View.GONE);
            robotPinButton.setVisibility(View.GONE);
        }else if(title.equals("attacker")){
            layout.setBackgroundResource(R.drawable.unknown);
            pinButton.setImageResource(R.drawable.pinreq);
            pinCode.setVisibility(View.GONE);
            robotPinButton.setVisibility(View.GONE);
        }else if(title.equals("light")){
            robotPinButton.setVisibility(View.GONE);
        }else {
            layout.setBackgroundResource(R.drawable.unknown);
            pinButton.setImageResource(R.drawable.pinreq);
            pinCode.setVisibility(View.GONE);
            robotPinButton.setVisibility(View.GONE);
        }
        pinCode.setOnClickListener(this);
        pinButton.setOnClickListener(this);
        robotPinButton.setOnClickListener(this);

    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public PincodeDialog(Context context, String title, String ip) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        this.ip = ip;
        this.title = title;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pinButton:
                Toast.makeText(context, "Pincode 요청을 보냈습니다", Toast.LENGTH_SHORT).show();
                Log.d("이준환", "pinCodeListener: ip"+ip);

                PinCodeTask task = new PinCodeTask();
                try {
                    task.execute(ip).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.pinText:
                Toast.makeText(context, "IOTC 추가 요청합니다", Toast.LENGTH_SHORT).show();
                alertPopup("");
                dismiss();
                break;
            case R.id.robotPinButton:

                Toast.makeText(context, "Pincode 요청을 보냈습니다", Toast.LENGTH_SHORT).show();
                Log.d("이준환", "pinCodeListener: ip"+ip);

                RobotPinCodeTask robotTask = new RobotPinCodeTask();
                try {
                    robotTask.execute(ip).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                TimerTask myTask = new TimerTask() {
                    public void run() {
                        CheckTaging conFirmtask = new CheckTaging();
                        try {
                            conFirmtask.execute(ip).get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                };
                timer = new Timer();
                timer.schedule(myTask, 0000, 1000); // 5초후 첫실행, 3초마다 계속실행
                break;
        }
    }

    class RobotPinCodeTask extends AsyncTask<String, Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            RestSender sender = new RestSender();
            Log.d("이준환", "RobotPinCodeTask: "+strings[0]);
            String jsonStr = sender.sendGet("http://localhost:3082/pincode/"+strings[0]);
            return jsonStr;
        }

        @Override
        protected void onPostExecute(String strings) {
            Log.d("이준환", "RobotPinCodeTask: jsonStr : "+strings);
            JSONObject resObject = null;
            try {
                resObject = new JSONObject(strings);
                String pin = (String) resObject.get("pin");
                returnObj = resObject;
                pinCode.setVisibility(View.VISIBLE);
                robotPinButton.setVisibility(View.GONE);
                pinCode.setText("Tag...");

            } catch (JSONException e) {
                e.printStackTrace();
            }



            super.onPostExecute(strings);
        }
    }


    class CheckTaging extends AsyncTask<String, Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            RestSender sender = new RestSender();
            Log.d("이준환", "체크 URL http://"+strings[0]+":3082/nfcCheck");
            String jsonStr = sender.sendGetRobot("http://"+strings[0]+":3082/nfcCheck");
            if(jsonStr.trim().equals("1")) timer.cancel();
            return jsonStr;
        }

        @Override
        protected void onPostExecute(String strings) {

            if(strings.trim().equals("1")){
                //성공
                Log.d("이준환", "CheckTaging22: jsonStr : "+strings.trim());
                alertPopup("");

            }else{
                Log.d("이준환", "CheckTaging 시발: jsonStr : "+strings.trim());
            }

            super.onPostExecute(strings);
        }
    }

    class PinCodeTask extends AsyncTask<String, Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            RestSender sender = new RestSender();
            Log.d("이준환", "PinCodeTask: "+strings[0]);
            String jsonStr = sender.sendGet("http://localhost:3082/pincode/"+strings[0]);

            return jsonStr;
        }

        @Override
        protected void onPostExecute(String strings) {
            Log.d("이준환", "PinCodeTask: jsonStr : "+strings);
            JSONObject resObject = null;
            try {
                resObject = new JSONObject(strings);
                String pin = (String) resObject.get("pin");
                returnObj = resObject;
                pinCode.setVisibility(View.VISIBLE);
                pinButton.setVisibility(View.GONE);
                pinCode.setText(pin);

            } catch (JSONException e) {
                e.printStackTrace();
            }



            super.onPostExecute(strings);
        }
    }

    public void alertPopup(String arg) {
        dismiss();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // 제목셋팅
        alertDialogBuilder.setTitle("기기를 추가하시겠습니까?");

        // AlertDialog 셋팅
        alertDialogBuilder
                .setMessage(arg)
                .setCancelable(false)
                .setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 프로그램을 종료한다

                                SendPinCodeTask sendPinCodeTask = new SendPinCodeTask();
                                try {
                                    boolean res = sendPinCodeTask.execute().get();
                                    if(res) resultPopup("등록 성공");
                                    else resultPopup("등록 실패");
                                    dialog.cancel();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                .setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                // 다이얼로그를 취소한다
                                dialog.cancel();
                            }
                        });

        // 다이얼로그 생성
        AlertDialog alertDialog = alertDialogBuilder.create();

        // 다이얼로그 보여주기
        alertDialog.show();

    }

    class SendPinCodeTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            RestSender sender = new RestSender();
            String jsonStr = sender.httpPostPin("http://localhost:3082/confirmPin",returnObj);

            Log.d("이준환", "SendPinCodeTask: "+jsonStr);

            JSONObject resObject = null;
            boolean res = false;
            try {
                resObject = new JSONObject(jsonStr);
                res = (boolean) resObject.get("result");
                Log.d("이준환", "SendPinCodeTask: res "+res);

            }catch (Exception e){
                Log.d("이준환", "SendPinCodeTask: "+e.getMessage());
            }
            return res;
        }
    }


    public void resultPopup(String arg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // 제목셋팅
        alertDialogBuilder.setTitle(arg);
        String msg = "";
        if(arg.equals("등록 성공 ")) msg = "기기가 정상적으로 등록 되었습니다.";
        else msg = "정상 디바인스인지 확인바랍니다\n 악의 사용자의 기기가 등록될 경우 \n 사용자의 모든 IOTC 기기를 초기화 해야합니다. \n";

        // AlertDialog 셋팅
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 프로그램을 종료한다
                                dialog.cancel();
                            }
                        });

        // 다이얼로그 생성
        AlertDialog alertDialog = alertDialogBuilder.create();

        // 다이얼로그 보여주기
        alertDialog.show();

    }
}