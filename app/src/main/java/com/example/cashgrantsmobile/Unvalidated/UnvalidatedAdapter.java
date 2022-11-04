package com.example.cashgrantsmobile.Unvalidated;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.cashgrantsmobile.R;

import java.util.ArrayList;

public class UnvalidatedAdapter extends BaseAdapter {
    private final Context context;
    private final int layout;
    private final ArrayList<UnvalidatedItem> unvalidatedList;

    public UnvalidatedAdapter(Context context, int layout, ArrayList<UnvalidatedItem> unvalidatedList) {
        this.context = context;
        this.layout = layout;
        this.unvalidatedList = unvalidatedList;
    }

    @Override
    public int getCount() {
        return unvalidatedList.size();
    }

    @Override
    public Object getItem(int i) {
        return unvalidatedList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private static class ViewHolder {
        TextView textHHID, textAddress, textName;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder = new ViewHolder();
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, null);
            holder.textHHID = view.findViewById(R.id.textHHID);
            holder.textName = view.findViewById(R.id.textName);
            holder.textAddress = view.findViewById(R.id.textAddress);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        UnvalidatedItem unvalidatedItem = unvalidatedList.get(i);
        holder.textHHID.setText(unvalidatedItem.getHh_id());
        holder.textName.setText(unvalidatedItem.getFullname());
        holder.textAddress.setText(unvalidatedItem.getAddress());

        return view;
    }
}
