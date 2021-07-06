package com.common.ui.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.common.ui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用dialog
 */
public class CommonSelectDialog extends PopupWindow implements View.OnClickListener {

    private ViewGroup mContainer;
    private SelectCallBack mCallBack;
    private DialogModel mDialogModel;


    public CommonSelectDialog(Context context) {
        super(context);
        initView(context);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(0));//new ColorDrawable(0)即为透明背景
    }


    private void initView(Context context) {
        mContainer = (ViewGroup) View.inflate(context, R.layout.common_dialog_select_layout, null);
        setContentView(mContainer);
    }

    public void notifyDataChange() {
        bindData(mDialogModel, mCallBack);
    }

    public void bindData(DialogModel dialogModel, SelectCallBack callBack) {
        mDialogModel = dialogModel;
        mCallBack = callBack;
        for (int i = 0; i < dialogModel.dataList.size(); i++) {
            String content = dialogModel.dataList.get(i);
            View inflate = View.inflate(mContainer.getContext(), R.layout.common_dialog_select_layout_item, null);
            inflate.setTag(i);
            TextView textView = inflate.findViewById(R.id.common_dialog_item_content);
            View selectImg = inflate.findViewById(R.id.common_dialog_item_select);

            textView.setText(content);
            if (i == dialogModel.selectIndex) {
                textView.setSelected(true);
                selectImg.setVisibility(View.VISIBLE);
            } else {
                textView.setSelected(false);
                selectImg.setVisibility(View.GONE);
            }
            inflate.setOnClickListener(this);
            mContainer.addView(inflate);
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (mCallBack == null) {
            return;
        }
        mCallBack.selectByIndex((int) v.getTag());
    }

    public interface SelectCallBack {
        void selectByIndex(int index);
    }

    static public class DialogModel {
        List<String> dataList = new ArrayList<>();
        int selectIndex = -1;

        public void setSelectIndex(int index) {
            selectIndex = index;
        }

        public void setDataList(List<String> list) {
            dataList = list;
        }

    }
}
