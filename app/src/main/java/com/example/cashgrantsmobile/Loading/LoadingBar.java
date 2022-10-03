package com.example.cashgrantsmobile.Loading;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;

import com.example.cashgrantsmobile.R;

public class LoadingBar {
    private AlertDialog.Builder builder;
    private View view;
    private AlertDialog dialog;
    private ImageView loading_bar_image;
    private AnimationDrawable animationDrawable;
    public LoadingBar(Context context){
        builder = new AlertDialog.Builder(context);
        view = View.inflate(context, R.layout.loading_bar_layout,null);
        loading_bar_image = view.findViewById(R.id.loading_bar_image);
        builder.setView(view);
        dialog=builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        animationDrawable = (AnimationDrawable) loading_bar_image.getBackground();
    }
    public void Show_loading_bar(){
        dialog.show();
        animationDrawable.start();
    }
    public void Hide_loading_bar(){
        dialog.dismiss();
    }

}
