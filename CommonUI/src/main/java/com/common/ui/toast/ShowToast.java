package com.common.ui.toast;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.common.ui.R;
import com.common.util.StringUtil;

public class ShowToast {
    private static Toast toast = null;


    //    android9以上，不能复用toast，推荐使用Snackbar
    public static void showToast(Context context, CharSequence text) {
        showCenterToast(context, text, R.layout.common_toast);
    }

    //    android9以上，不能复用toast，推荐使用Snackbar
    public static void showPhoneCenterToast(Context context, CharSequence text) {
        showCenterToast(context, text, R.layout.common_toast);
    }

    public static void showPadCenterToast(Context context, CharSequence text) {
        showCenterToast(context, text, R.layout.common_toast_big);
    }

    public static void showCenterToast(Context context, CharSequence text, int layoutId) {
        if (text == null || StringUtil.isEmpty(text.toString())) {
            return;
        }
        new Handler(context.getMainLooper()).post(() -> {
            if (toast == null || Build.VERSION.SDK_INT >= 28) {
                View ll = LayoutInflater.from(context).inflate(layoutId, null);
                toast = new Toast(context);
                toast.setView(ll);
                toast.setDuration(Toast.LENGTH_SHORT);
            }
            ((TextView) toast.getView().findViewById(R.id.message)).setText(text);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        });
    }
}
