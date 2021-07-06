package com.common.util;

import android.view.View;
import android.widget.TextView;

public class ViewUtil {

    public static <T extends View> T requestView(View convertView, int id) {
        View view = null;
        if (convertView != null) {
            view = convertView.findViewById(id);
        }
        return (T) view;
    }

    public static void showTextEmplyGone(TextView convertView, String content) {
        convertView.setText(content);
        convertView.setVisibility(StringUtil.emptyOrNull(content) ? View.GONE : View.VISIBLE);
    }


}
