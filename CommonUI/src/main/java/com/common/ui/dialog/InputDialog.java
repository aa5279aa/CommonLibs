package com.common.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.ui.R;
import com.common.util.UIShowUtil;
import com.common.util.ViewUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 通用dialog
 */
public class InputDialog extends Dialog implements View.OnClickListener {

    private LinearLayout mRoot;
    private ClickCallBack mCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    public InputDialog(Context context) {
        super(context, R.style.NativeInsertDialog);
        initView();
        getWindow().setGravity(Gravity.CENTER);
    }


    private void initView() {
        mRoot = (LinearLayout) View.inflate(getContext(), R.layout.common_dialog_input_layout, null);
        setContentView(mRoot, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void clearInputText() {
        clearInputText("");
    }

    public void clearInputText(String text) {
        TextView edit = ViewUtil.requestView(mRoot, R.id.edit_text);
        edit.setHint(text);
        edit.setText("");
    }

    public void setButtonClickBack(ClickCallBack callBack) {
        mCallBack = callBack;
    }


    public void bindData(DialogModel dialogModel) {
        TextView title = ViewUtil.requestView(mRoot, R.id.title_text);
        TextView button = ViewUtil.requestView(mRoot, R.id.button_text);
        TextView edit = ViewUtil.requestView(mRoot, R.id.edit_text);

        button.setOnClickListener(this);
        UIShowUtil.showText(title, dialogModel.title);
        UIShowUtil.showText(button, dialogModel.button);
        edit.setHint(dialogModel.hint);
    }

    @Override
    public void show() {
        super.show();
        ViewUtil.requestView(mRoot, R.id.edit_text).setFocusable(true);
        ViewUtil.requestView(mRoot, R.id.edit_text).setFocusableInTouchMode(true);
        ViewUtil.requestView(mRoot, R.id.edit_text).requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           public void run() {
                               InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               imm.showSoftInput(ViewUtil.requestView(mRoot, R.id.edit_text), InputMethodManager.SHOW_IMPLICIT);
                           }
                       },
                500);
    }

    @Override
    public void onClick(View v) {
        if (mCallBack == null) {
            return;
        }
        dismiss();
        TextView edit = ViewUtil.requestView(mRoot, R.id.edit_text);
        String s = edit.getText().toString();
        mCallBack.clickButton(s);
    }

    public interface ClickCallBack {
        void clickButton(String str);
    }

    static public class DialogModel {
        CharSequence title;
        CharSequence hint;
        CharSequence button;

        public void setButton(CharSequence button1) {
            button = button1;
        }

        public void setHint(CharSequence desc1) {
            hint = desc1;
        }

        public void setTitle(CharSequence title1) {
            title = title1;
        }

    }
}
