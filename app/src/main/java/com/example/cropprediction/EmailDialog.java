package com.example.cropprediction;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class EmailDialog {
    Activity activity;
    AlertDialog alertDialog;

    EmailDialog(Activity activity){
        this.activity=activity;
    }
    void startLoadingDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        LayoutInflater inflater=activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_for_verification_mail,null));
        builder.setCancelable(true);
        alertDialog=builder.create();
        alertDialog.show();
    }
    void stopLoadingDialog(){
        alertDialog.dismiss();
    }
    void changeLayoutToEmailIntent(){
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        LayoutInflater inflater=activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.email_intent_dialog,null));
        builder.setCancelable(true);
        alertDialog=builder.create();
        alertDialog.show();

    }
}
