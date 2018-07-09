package com.example.a1002732.iotc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a1002732.iotc.dialog.CustomDialog;
import com.example.a1002732.iotc.dialog.PincodeDialog;
import com.example.a1002732.iotc.rest.RestSender;
import com.example.a1002732.iotc.rest.Transaction;
import com.example.a1002732.iotc.task.BlockChainListTask;
import com.example.a1002732.iotc.task.RestTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    final Context context = this;

    private CustomDialog mCustomDialog;
    private PincodeDialog mPincodeDialog;
    List<String> myBlockCnList = null;
    ArrayList<String> newCnList = null;
    ArrayList<String> newCnIPList = null;
    Map<String, String> mapIpDevice = null;
    ArrayList<String> viewList = null;
    int myblockSize = 0;
    ArrayList<String> keyList = null;
    JSONObject fruitObject = null;
    Map<String, String> myDeviceIp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.notification_list);

        myDeviceIp = new HashMap<>();
        myDeviceIp.put("mobile", "IP_192.168.43.100");
        myDeviceIp.put("light", "IP_192.168.43.107");
        myDeviceIp.put("robot", "IP_192.168.43.106");
        //myBlockChainListSetting();


        //브로드 캐스트를 3초마다 가져와서, 기존 CN을 제외하여 추가 저장,
        TimerTask myTask = new TimerTask() {
            public void run() {
                myBlockChainListSetting();
                BloadCastTask task = new BloadCastTask();
                try {
                    task.execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(myTask, 0000, 6500); // 5초후 첫실행, 3초마다 계속실행


        //클릭 이벤트
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    mPincodeDialog = new PincodeDialog(context, "mobile", null);
                    mPincodeDialog.show();
                } else {
                    boolean dir = false;
                    for (String s : myBlockCnList) {
                        if (viewList.get(position).equals(s)) {
                            dir = true;
                            break;
                        }

                    }
                    if (dir) {
                        //식구
                        String ip = "192.168.43.118";
                        Log.d("이준환", "LED IP : " + ip);
                        ip = ip.replace("IP_", "");
                        //Toast.makeText(getApplicationContext(), viewList.get(position) , Toast.LENGTH_LONG).show();
                        mCustomDialog = new CustomDialog(context,
                                viewList.get(position),
                                ip);
                        mCustomDialog.show();
                    } else {
                        String ip = mapIpDevice.get(viewList.get(position));
                        Log.d("이준환", "LED IP : " + ip);
                        ip = ip.replace("IP_", "");
                        //Toast.makeText(getApplicationContext(), viewList.get(position)+" / "+ip , Toast.LENGTH_LONG).show();

                        mPincodeDialog = new PincodeDialog(context, viewList.get(position), ip);
                        mPincodeDialog.show();
                    }
                }


            }
        });
    }


    private void myBlockChainListSetting() {
        //블록체인 리스트를 가져와서 myBlockCnList 에 저장
        BlockChainListTask blockChainListTask = new BlockChainListTask();
        try {
            myBlockCnList = blockChainListTask.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    private View.OnClickListener leftListener = new View.OnClickListener() {
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "왼쪽버튼 클릭", Toast.LENGTH_SHORT).show();
            mCustomDialog.dismiss();
        }
    };

    private View.OnClickListener rightListener = new View.OnClickListener() {
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "오른쪽버튼 클릭", Toast.LENGTH_SHORT).show();
        }
    };


    class BloadCastTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {

            RestSender sender = new RestSender();
            String jsonStr = sender.sendGet("http://localhost:3082/broadcast");


            keyList = new ArrayList<>();


            try {
                fruitObject = new JSONObject(jsonStr);
                Iterator i = fruitObject.keys();
                while (i.hasNext()) {
                    String b = i.next().toString();
                    keyList.add(b);
                }
                newCnList = new ArrayList<>();
                mapIpDevice = new HashMap<>();


                for (String s : keyList) {

                    String ss = (String) fruitObject.get(s); // 기계이름

                    if (ss.equals("null")) continue;
                    if (ss.trim().equals("mobile")) continue;


                    boolean dir = false;
                    //내 디바이스에 있는지 확인
                    for (String d : myBlockCnList) {

                        //내 디바이스에 있고 아이피도 같으면 스킵
                        if (d.equals(ss) && myDeviceIp.get(ss).equals(s)) {
                            dir = true;
                            break;
                        }
                    }

                    if (dir) continue;

                    newCnList.add(ss.trim());
                    mapIpDevice.put(ss.trim(), s);

                    Log.d("이준환", "doInBackground: " + myDeviceIp.get(ss) + " / " + s);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            //Log.d("이준환", "BloadCastTask: "+jsonStr);
            //List<Transaction> transactions = sender.getTransactiom();
            return "myTask";
        }

        @Override
        protected void onPostExecute(String strings) {
            RestTask t = new RestTask();

            try {
                List<Transaction> transaction = t.execute().get();
                ArrayList<String> list = new ArrayList<>();
                viewList = new ArrayList<>();

                for (String s : myBlockCnList) {
                    if (s.equals("undefined")) viewList.add("mobile");
                    else if (s.equals("robot")) viewList.add("robot");
                    else if (s.equals("mobile")) viewList.add("mobile");
                    else if (s.equals("light")) viewList.add("light");
                    else list.add("unKnown");
                }

                CustomAdapter adapter = new CustomAdapter(getBaseContext(), 0, viewList, newCnList);
                listView.setAdapter(adapter);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            super.onPostExecute(strings);
        }
    }


    public void alertPopup(String arg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // 제목셋팅
        alertDialogBuilder.setTitle("새로운 기기추가");

        // AlertDialog 셋팅
        alertDialogBuilder
                .setMessage(arg)
                .setCancelable(false)
                .setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 프로그램을 종료한다
                                dialog.cancel();
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


    private class CustomAdapter extends ArrayAdapter<String> {
        private ArrayList<String> items;

        public CustomAdapter(Context context, int textViewResourceId, ArrayList<String> objects, ArrayList<String> newList) {
            super(context, textViewResourceId, objects);
            myblockSize = objects.size();

            for (String s : newList) {
                objects.add(s);
            }
            this.items = objects;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.notification_listview_row, null);
            }


            // ImageView 인스턴스
            ImageView deviceImg = (ImageView) v.findViewById(R.id.device_img);
            ImageView deviceConnStatus = (ImageView) v.findViewById(R.id.deviceConnStatus);

            ImageView deviceDisconn = (ImageView) v.findViewById(R.id.deviceDisconn);

            // 리스트뷰의 아이템에 이미지를 변경한다.
            TextView device_type = (TextView) v.findViewById(R.id.device_type);
            TextView device_cn = (TextView) v.findViewById(R.id.device_cn);

            Log.d("이준환", "TOTAL : " + myblockSize + " getView: " + position + " / " + items.get(position) + " / " + mapIpDevice.get(items.get(position)));

            if (position >= myblockSize) {
                if (items.get(position).equals("robot")) {
                    deviceImg.setImageResource(R.drawable.new_device);
                    deviceConnStatus.setImageResource(R.drawable.disconninfo);
                    device_type.setText("Robot Cleaner");
                    device_cn.setText("samsung robot cleaner 480N3");

                } else if (items.get(position).equals("light")) {
                    deviceImg.setImageResource(R.drawable.new_device);
                    deviceConnStatus.setImageResource(R.drawable.disconninfo);
                    device_type.setText("LP-50");
                    device_cn.setText("samsung smart Lamp");
                } else {
                    deviceImg.setImageResource(R.drawable.newdevice);
                    deviceConnStatus.setImageResource(R.drawable.disconninfo);
                    device_type.setText("Unknown");
                    device_cn.setText("알수없는 디바이스");
                }

            } else {

                switch (items.get(position)) {
                    case "robot":
                        deviceImg.setImageResource(R.drawable.robotcleaner);
                        try {
                            String ip = myDeviceIp.get("robot");
                            String ss = (String) fruitObject.get(ip);
                            if (ss.equals("null")) {
                                deviceDisconn.setVisibility(View.VISIBLE);
                                deviceConnStatus.setVisibility(View.GONE);
                            } else {
                                deviceDisconn.setVisibility(View.GONE);
                                deviceConnStatus.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        device_type.setText("Robot Cleaner");
                        device_cn.setText("samsung robot cleaner 480N3");
                        break;
                    case "light":
                        deviceImg.setImageResource(R.drawable.light_device);
                        String ip = myDeviceIp.get("light");

                        try {
                            String ss = (String) fruitObject.get(ip);
                            if (ss.equals("null")) {
                                deviceDisconn.setVisibility(View.VISIBLE);
                                deviceConnStatus.setVisibility(View.GONE);
                            } else {
                                deviceDisconn.setVisibility(View.GONE);
                                deviceConnStatus.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        device_type.setText("LP-50");
                        device_cn.setText("samsung smart Lamp");
                        break;
                    case "mobile":
                        deviceImg.setImageResource(R.drawable.mobile);
                        device_type.setText("Mobile");
                        deviceConnStatus.setImageResource(R.drawable.disconninfo);
                        device_cn.setText("samsung galaxy S8");
                        deviceDisconn.setVisibility(View.GONE);
                        deviceConnStatus.setVisibility(View.GONE);
                        break;
                    case "unKnown":
                        deviceImg.setImageResource(R.drawable.newdevice);
                        deviceConnStatus.setImageResource(R.drawable.disconninfo);
                        device_type.setText("Unknown");
                        device_cn.setText("알수없는 디바이스");
                        break;
                    default:
                }
            }


            return v;
        }
    }

}
