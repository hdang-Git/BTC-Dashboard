package com.example.hai.bitcoindashboard;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;


public class CustomMarkerView extends MarkerView {

    private TextView display;
    Logger log = Logger.getAnonymousLogger();

    //constructor
    public CustomMarkerView (Context context, int layoutResource) {
        super(context, layoutResource);
        // this markerview only displays a textview
        display = (TextView) findViewById(R.id.tvContent);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        float dtVal = e.getX();
        float price = e.getY();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yy");
        Date dt = new Date((long) dtVal * 1000);
        String date = dateFormat.format(dt);
        String val = date + " $ " + Float.toString(price);
        log.info("MarkerView class: value:" + val);
        display.setText(val); // set the entry-value as the display text
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
