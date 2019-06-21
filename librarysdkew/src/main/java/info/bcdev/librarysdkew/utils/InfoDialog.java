package info.bcdev.librarysdkew.utils;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

public class InfoDialog {

    private static final String TAG = "Wallet Test InfoDialog";
    private Context mContext;

    private AlertDialog infodialog;

    public InfoDialog(Context context){
        Log.e(TAG, "InfoDialog: 불러옴");
        mContext = context;
    }

    public void Get(String title, String message){
        Log.e(TAG, "Get: 실행함");
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(message)
                .setTitle(title);
        infodialog = builder.create();
        infodialog.setCanceledOnTouchOutside(false);
        infodialog.show();
    }

    public void Dismiss(){
        Log.e(TAG, "Dismiss: 실행됨");
        infodialog.dismiss();
    }

}
