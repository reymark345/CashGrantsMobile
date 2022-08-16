package com.example.cashgrantsmobile.Internet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;

import com.example.cashgrantsmobile.R;


public class NetworkChangeListener extends BroadcastReceiver {
    public boolean connection = false;
    @Override
    public void onReceive(Context context, Intent intent){
        if(!Common.isConnectedToInternet(context)){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View layout_dialog = LayoutInflater.from(context).inflate(R.layout.internet_connection,null);
            builder.setView(layout_dialog);
            AppCompatButton btnProceed = layout_dialog.findViewById(R.id.btnProceed);
            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.setCancelable(false);
            dialog.getWindow().setGravity(Gravity.CENTER);
            btnProceed.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    dialog.dismiss();
//                    onReceive(context,intent);
                }
            });
        }
        else{
            connection =true;
        }
    }

}
