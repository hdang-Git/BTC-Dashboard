package com.example.hai.bitcoindashboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Created by Hai on 12/4/2016.
 */

public class CustomSpinnerAdapter extends ArrayAdapter{

    public CustomSpinnerAdapter(Context context, int resource, Object[] objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        return v;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v =  super.getDropDownView(position, convertView, parent);
        return v;
    }
}
