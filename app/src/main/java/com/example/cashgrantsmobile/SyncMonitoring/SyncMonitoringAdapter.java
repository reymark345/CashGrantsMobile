package com.example.cashgrantsmobile.SyncMonitoring;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.cashgrantsmobile.R;
import com.example.cashgrantsmobile.Unvalidated.UnvalidatedAdapter;
import com.example.cashgrantsmobile.Unvalidated.UnvalidatedItem;

import java.util.ArrayList;

public class SyncMonitoringAdapter extends BaseAdapter {
    private final Context context;
    private final int layout;
    private final ArrayList<SyncMonitoringItem> syncMonitoringList;

    public SyncMonitoringAdapter(Context context, int layout, ArrayList<SyncMonitoringItem> syncMonitoringList) {
        this.context = context;
        this.layout = layout;
        this.syncMonitoringList = syncMonitoringList;
    }

    @Override
    public int getCount() {
        return syncMonitoringList.size();
    }

    @Override
    public Object getItem(int i) {
        return syncMonitoringList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private static class ViewHolder {
        TextView textUsername, textDateTime, textSync, textUpdate, textRecord;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        SyncMonitoringAdapter.ViewHolder holder = new SyncMonitoringAdapter.ViewHolder();
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, null);
            holder.textUsername = view.findViewById(R.id.username_data);
            holder.textDateTime = view.findViewById(R.id.datetime_data);
            holder.textSync = view.findViewById(R.id.sync_data);
            holder.textUpdate = view.findViewById(R.id.update_data);
            holder.textRecord = view.findViewById(R.id.record_data);
            view.setTag(holder);
        } else {
            holder = (SyncMonitoringAdapter.ViewHolder) view.getTag();
        }
        SyncMonitoringItem syncMonitoringItem = syncMonitoringList.get(i);
        holder.textUsername.setText(syncMonitoringItem.getUsername());
        holder.textDateTime.setText(syncMonitoringItem.getDateTime());
        holder.textSync.setText(syncMonitoringItem.getSync());
        holder.textUpdate.setText(syncMonitoringItem.getUpdate());
        holder.textRecord.setText(syncMonitoringItem.getRecord());


        return view;
    }
}
