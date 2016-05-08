package com.imooc.guessmusic.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.imooc.guessmusic.R;
import com.imooc.guessmusic.model.IAlertDialogButtonListener;

/**
 * Created by Administrator on 2016/5/4 0004.
 */
public class Util {


    private static AlertDialog mAlertDialog;

    public static View getView(Context context, int layoutid) {
        LayoutInflater inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(layoutid, null);

        return layout;
    }

    /**
     * 界面跳转
     */
    public static void startActivity(Context context, Class desti) {
        Intent intent = new Intent();
        intent.setClass(context, desti);
        context.startActivity(intent);

        // 关闭当前的ACTIVITY
        ((Activity)context).finish();
    }

    /**
     * 对话框
     */
    public static void showDialog(final Context context, String message,
                                  final IAlertDialogButtonListener listener) {

        View dialogView = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        dialogView = getView(context, R.layout.dialog_view);

        ImageButton btnOkView = (ImageButton) dialogView.findViewById(
                R.id.btn_dialog_ok);

        ImageButton btnCancelView = (ImageButton) dialogView.findViewById(
                R.id.btn_dialog_cancel);

        TextView txtMessageView = (TextView) dialogView.findViewById(
                R.id.text_dialog_message);

        txtMessageView.setText(message);

        btnOkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 点击“是”，关闭当前弹窗
                if (mAlertDialog != null) {
                    mAlertDialog.cancel();
                }

                // 回调onClick()
                if (listener != null) {
                    listener.onClick();
                }

                MyPlayer.playTone(context, MyPlayer.INDEX_STONE_ENTER);
            }
        });

        btnCancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 点击“否”，关闭当前弹窗
                if (mAlertDialog != null) {
                    mAlertDialog.cancel();
                }

                MyPlayer.playTone(context, MyPlayer.INDEX_STONE_CANCEL);

            }
        });

        // 为dialog设置view
        builder.setView(dialogView);
        mAlertDialog = builder.create();

        // 显示对话框
        mAlertDialog.show();

    }

}
