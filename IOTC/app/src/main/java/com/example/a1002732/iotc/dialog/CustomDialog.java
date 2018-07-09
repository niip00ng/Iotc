package com.example.a1002732.iotc.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.a1002732.iotc.R;
import com.example.a1002732.iotc.rest.RestSender;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class CustomDialog extends Dialog implements View.OnClickListener{

    LinearLayout layout = null;
    private ImageView lightOn;
    private ImageView lightOff;
    private ImageView lightTop;
    private ImageView lightBottom;
    Context context=null;
    String title = "";
    String ip = "";
    String value = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.activity_custom_dialog);

        lightOn =  (ImageView) findViewById(R.id.light_on_button);
        lightOff = (ImageView)findViewById(R.id.light_off_button);
        lightTop = (ImageView)findViewById(R.id.light_top);
        lightBottom = (ImageView)findViewById(R.id.light_bottom);

        layout = (LinearLayout) findViewById(R.id.funtion_layout);


        if(title.equals("robot")){
            layout.setBackgroundResource(R.drawable.robot_detail);
            lightOn.setVisibility(View.GONE);
            lightOff.setVisibility(View.GONE);
            lightBottom.setVisibility(View.GONE);
            lightTop.setVisibility(View.GONE);
        }


        lightOn.setOnClickListener(this);
        lightOff.setOnClickListener(this);
    }

    // 클릭버튼이 확인과 취소 두개일때 생성자 함수로 이벤트를 받는다
    public CustomDialog(Context context, String title,String ip) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        this.title = title;
        this.ip = ip;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.light_off_button:
                value = "artik_led_control,38,0";
                Log.d("이준환", "light_off_button: on");

                LedTask ledTask = new LedTask();
                try {
                    ledTask.execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                lightOff.setVisibility(View.GONE);
                lightOn.setVisibility(View.VISIBLE);
                break;
            case R.id.light_on_button:
                Log.d("이준환", "light_off_button: off");
                value = "artik_led_control,38,1";
                ledTask = new LedTask();
                try {
                    ledTask.execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                lightOn.setVisibility(View.GONE);
                lightOff.setVisibility(View.VISIBLE);
                break;
        }
    }




    class LedTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            RestSender sender = new RestSender();


            JSONObject jsonParam = new JSONObject();
            try {
                jsonParam.put("ip", ip);
                jsonParam.put("data", value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String jsonStr = sender.httpPostPin("http://localhost:3082/control", jsonParam);

            Log.d("이준환", "LedTask: "+jsonStr);

            JSONObject resObject = null;
            try {
                resObject = new JSONObject(jsonStr);
                boolean res = (boolean) resObject.get("pin");

            }catch (Exception e){
                Log.d("이준환", "LedTask: "+e.getMessage());
            }
            return null;
        }
    }
}