package com.example.cashgrantsmobile.Inventory;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.cashgrantsmobile.MainActivity;
import com.example.cashgrantsmobile.R;
import java.util.ArrayList;

public class InventoryListAdapter extends BaseAdapter {

    private Context context;
    private  int layout;
    private ArrayList<Inventory> inventoryList;
    String DarkModeStatus;

    public InventoryListAdapter(Context context, int layout, ArrayList<Inventory> foodsList) {
        this.context = context;
        this.layout = layout;
        this.inventoryList = foodsList;
    }
    @Override
    public int getCount() {
        return inventoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return inventoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        ImageView imageView, mPreviewCashCard;
        TextView txtName, txtPrice;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT id,status FROM DarkMode");
        while (cursor.moveToNext()) {
            DarkModeStatus = cursor.getString(1);
        }

        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.txtName = (TextView) row.findViewById(R.id.txtName);
            holder.txtPrice = (TextView) row.findViewById(R.id.txtPrice);
            holder.imageView = (ImageView) row.findViewById(R.id.imgFood);
            holder.mPreviewCashCard = (ImageView) row.findViewById(R.id.imgId);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }
        Inventory inventory = inventoryList.get(position);
        holder.txtName.setText(inventory.getName());
        holder.txtPrice.setText(inventory.gethhNumber());
//        holder.txtSeriesNo.setText(inventory.getSeriesNumber());
        int status = inventory.getStatus();
        if (status==0 && DarkModeStatus.matches("false")){
              //exclude and white
            row.setBackgroundColor(Color.parseColor("#FEF8DD")); //
        }
        else if(status==0 && DarkModeStatus.matches("true")){
            //exclude and dark
            row.setBackgroundColor(Color.parseColor("#282828"));
        }
        else if (status==1 && DarkModeStatus.matches("false")) {
            //include and white
            row.setBackgroundColor(Color.parseColor("#F7F7FA")); // white background color you are using
        }
        else if (status==1 && DarkModeStatus.matches("true")) {
            //include and white
            row.setBackgroundColor(Color.parseColor("#252C4B")); // white background color you are using
        }

        byte[] CashCardImage = inventory.getImage();

        byte[] idImage = inventory.getIdImage();
        if(CashCardImage.length > 1)
        {
            Bitmap bitmap = BitmapFactory.decodeByteArray(CashCardImage, 0, CashCardImage.length);
            holder.imageView.setImageBitmap(bitmap);
        }
        else{
            holder.imageView.setImageResource(R.drawable.ic_image);
        }
        if(idImage!=null)
        {
            Bitmap bitmap2 = BitmapFactory.decodeByteArray(idImage, 0, idImage.length);
            holder.mPreviewCashCard.setImageBitmap(bitmap2);
        }
        else{
            holder.mPreviewCashCard.setImageResource(R.drawable.ic_image);
        }
        return row;
    }
}