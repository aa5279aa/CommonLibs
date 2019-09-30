package com.common.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.common.demo.create.UIWidgetCreate;
import com.common.ui.demoshow.CommonUIActivity;
import com.common.ui.widget.CommonButton;


public class MainActivity extends Activity implements View.OnClickListener {

    LinearLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContainer = findViewById(R.id.container);

        CommonButton commonButton = UIWidgetCreate.createCommonButton(this);
        commonButton.setText("跳转UI控件库");
        commonButton.setOnClickListener(this);
        mContainer.addView(commonButton);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setClass(this, CommonUIActivity.class);
        startActivity(intent);
    }
}
