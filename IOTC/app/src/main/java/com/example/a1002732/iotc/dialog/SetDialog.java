package com.example.a1002732.iotc.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.a1002732.iotc.R;

public class SetDialog extends Dialog {

    private ImageView mLeftButton;
    private ImageView mRightButton;
    private LinearLayout layout;
    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mRightClickListener;

    private String title= "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.3f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.set_dialog);


        layout = (LinearLayout) findViewById(R.id.set_dialog_layout);
        mLeftButton = (ImageView) findViewById(R.id.btn_left);
        mRightButton = (ImageView) findViewById(R.id.btn_right);

        if(title.equals("genesis")) layout.setBackgroundResource(R.drawable.genesis_set_dialog);

        // 클릭 이벤트 셋팅
        if (mLeftClickListener != null && mRightClickListener != null) {
            mLeftButton.setOnClickListener(mLeftClickListener);
            mRightButton.setOnClickListener(mRightClickListener);
        } else if (mLeftClickListener != null
                && mRightClickListener == null) {
            mLeftButton.setOnClickListener(mLeftClickListener);
        } else {

        }
    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public SetDialog(Context context, String title,
                     View.OnClickListener singleListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.title = title;
        this.mLeftClickListener = singleListener;
    }

    // 클릭버튼이 확인과 취소 두개일때 생성자 함수로 이벤트를 받는다
    public SetDialog(Context context, String title,
                     String content, View.OnClickListener leftListener,
                     View.OnClickListener rightListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.title = title;
        this.mLeftClickListener = leftListener;
        this.mRightClickListener = rightListener;
    }
}