package com.example.cashgrantsmobile.Logs;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.cashgrantsmobile.MainActivity;
import com.example.cashgrantsmobile.R;

import java.util.ArrayList;

public class LogsListAdapter extends BaseAdapter {

    private Context context;
    private  int layout;
    private ArrayList<Logs> logsList;
    String DarkModeStatus;

    public LogsListAdapter(Context context, int layout, ArrayList<Logs> logsList) {
        this.context = context;
        this.layout = layout;
        this.logsList = logsList;
    }

    @Override
    public int getCount() {
        return logsList.size();
    }

    @Override
    public Object getItem(int position) {
        return logsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        TextView textStatus, textHHID, textDesc, textUsername, textDate;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT id,status FROM DarkMode");
        while (cursor.moveToNext()) {
            DarkModeStatus = cursor.getString(1);
        }
        cursor.close();

        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.textStatus = (TextView) row.findViewById(R.id.textStatus);
            holder.textHHID = (TextView) row.findViewById(R.id.textHHID);
            holder.textDesc = (TextView) row.findViewById(R.id.textDesc);
            holder.textUsername = (TextView) row.findViewById(R.id.textUsername);
            holder.textDate = (TextView) row.findViewById(R.id.textDate);
            row.setTag(holder);

        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        Logs logs = logsList.get(position);
        holder.textStatus.setText(logs.getType());
        holder.textHHID.setText(logs.getHHID());
        holder.textDesc.setText(logs.getDescription());
        holder.textUsername.setText(logs.getUsername());
        holder.textDate.setText(logs.getCreated_at());

        if (logs.getType().matches("error")) {
            holder.textStatus.setTextColor(Color.rgb(200,0,0));
        } else {
            holder.textStatus.setTextColor(Color.rgb(0,200,0));
        }

        return row;
    }
}