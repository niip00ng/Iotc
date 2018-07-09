package com.example.a1002732.iotc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.a1002732.iotc.dialog.CustomDialog;
import com.example.a1002732.iotc.dialog.SetDialog;
import com.example.a1002732.iotc.task.BlockChainTask;
import com.example.a1002732.iotc.task.InitChainTask;

import java.util.concurrent.ExecutionException;

/**
 * Created by 1002732 on 2018. 4. 17..
 */

public class SplashActivity extends Activity {

    final Context context = this;
    private ProgressBar progressBar;
    boolean popupIndex = true;


    Runnable runnable;
    Object lock = new Object();
    CustomDialog mCustomDialog = null;
    SetDialog mSetDalog = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //초기 블록체인이 존재 하는지 확인
        tryGetBlockChain();

    }

    private boolean defineGenesisBlock(String res){

        switch (res){
            case "101":

                break;
            case "102":

                break;
            case "OK":
                // Step 1 이 성공일때는 메인으로 진행
                try {
                    Thread.sleep(1300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case "FAIL":
                // Step 1 이 실패일때는 블록체인 초기화 설정 할지 여부확인
                mSetDalog = new SetDialog(context,
                        "Mobile Setting", // 제목
                        "블록체인 설정을 계속 진행하시려면 확인을 누르세요.", // 내용
                        leftListener, // 왼쪽 버튼 이벤트
                        rightListener); // 오른쪽 버튼 이벤트
                mSetDalog.show();
                break;
            default:
        }

        return true;
    }

    private String tryGetBlockChain(){
        BlockChainTask task = new BlockChainTask();
        String result = "";
        try {
            result = task.execute().get();

            Log.d("이준환", "====== Step 1 블록체인 존재 유무 확인 결과 : "+result);

            defineGenesisBlock(result);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }
    private void tryInitChain(){
        InitChainTask task = new InitChainTask();
        try {
            String result  = task.execute().get();

            Log.d("이준환", "====== Step 2 블록체인 초기화 결과 : "+result);

            defineInitChain(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private boolean defineInitChain(String res){

        switch (res){
            case "101":

                break;
            case "102":

                break;
            case "OK":
                try {
                    Thread.sleep(1300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case "FAIL":
                mSetDalog = new SetDialog(context,
                        "Genesis", // 제목
                        "블록체인 설정을 재시도 하시려면 확인을 누르세요.",
                        leftListener,
                        rightListener);
                mSetDalog.show();
                break;
            default:
        }

        return true;
    }

    private View.OnClickListener leftListener = new View.OnClickListener() {
        public void onClick(View v) {
            //확인 누를시 initChain 실행
            mSetDalog.dismiss();
            tryInitChain();
        }
    };

    private View.OnClickListener rightListener = new View.OnClickListener() {
        public void onClick(View v) {
            //취소 누를시 종료
            mSetDalog.dismiss();
            finish();
        }
    };
}
