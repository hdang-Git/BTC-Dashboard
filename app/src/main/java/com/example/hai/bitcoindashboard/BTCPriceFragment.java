package com.example.hai.bitcoindashboard;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;


public class BTCPriceFragment extends Fragment {

    Logger log = Logger.getAnonymousLogger();
    LineChart lineChart;
    public BTCPriceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_btcprice, container, false);
        lineChart = (LineChart) (v.findViewById(R.id.chart1));
        retrieveBTCPriceData();
        retrieveGraphData();
        return v;
    }

    public void retrieveBTCPriceData(){
        log.info("retrieveBTCPriceData");
        final String urlString = "http://btc.blockr.io/api/v1/coin/info";
        Thread t = new Thread(){
            @Override
            public void run(){
                log.info("run() is called");
                try {
                    URL url = new URL(urlString);
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(
                                    url.openStream()));
                    String tmpString = "";
                    String response = "";
                    while (tmpString != null) {
                        //response.concat(tmpString);
                        response = response + tmpString;
                        tmpString = reader.readLine();
                    }
                    Message msg = Message.obtain();
                    msg.obj = response;

                    Log.d("downloaded data", response);
                    responseHandler.sendMessage(msg);
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    Handler responseHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg){
            try {
                JSONObject blockObject = new JSONObject((String) msg.obj);
                JSONObject data = blockObject.getJSONObject("data");
                JSONObject markets = data.getJSONObject("markets");
                JSONObject btce = markets.getJSONObject("btce");
                ((TextView) getView().findViewById(R.id.bitcoinCurrentPrice)).setText(btce.getString("value"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
    });

    public void retrieveGraphData(){

        Thread t = new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL("https://api.blockchain.info/charts/market-price?format=json&timespan=all");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                    StringBuilder response = new StringBuilder();
                    String tmpString = "";
                    while(tmpString != null){
                        //Append to response the data
                        response.append(tmpString);
                        //read in input data
                        tmpString = reader.readLine();
                    }
                    Message msg = Message.obtain();
                    msg.obj = response.toString();
                    graphResponseHandler.sendMessage(msg);
                }catch(IOException e){
                    e.printStackTrace();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    Handler graphResponseHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try{
                JSONObject marketPrice = new JSONObject((String) msg.obj);
                JSONArray valArr = marketPrice.getJSONArray("values");
                JSONObject values;
                List<Entry> entries =   new ArrayList<>();
                long unixDT;
                double x;
                double y;
                for(int i = 0; i < valArr.length(); i++){
                    values = valArr.getJSONObject(i);
                    x = values.getDouble("x");
                    y = values.getDouble("y");
                    unixDT = (long) x * 1000;
                    Date time = new Date(unixDT);
                    String date = new SimpleDateFormat("MM.dd.yy").format(time);

                    log.info("x: " + x + " " + date + "\ty: " + y);
                    entries.add(new Entry((float) x, (float) y));
                }
                LineDataSet dataSet = new LineDataSet(entries, "Datetime");
                dataSet.setDrawCircles(false);
                dataSet.setColor(Color.RED);
                LineData lineData = new LineData(dataSet);
                //lineData.setValueFormatter(new LargeValueFormatter());
                XAxis xAxis = lineChart.getXAxis();
                //xAxis.setValueFormatter(new LargeValueFormatter());
                xAxis.setValueFormatter(new IAxisValueFormatter() {
                    private SimpleDateFormat date = new SimpleDateFormat("MM.dd.yy");
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        Date dt = new Date((long) value * 1000);
                        String val = date.format(dt);
                        log.info("original value: " + value + " formatted - " + val);
                        return val;
                    }
                });
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                lineChart.setData(lineData);
                lineChart.invalidate();
            } catch(Exception e){
                e.printStackTrace();
            }
            return true;
        }
    });


}
