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
import android.widget.Button;
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


public class BTCPriceFragment extends Fragment implements View.OnClickListener {

    Logger log = Logger.getAnonymousLogger();
    LineChart lineChart;
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    Button button5;
    Button button6;
    Button button7;
    String times[] = {"1days", "7days", "1months", "6months", "1years", "2years", "all"};   //"1d", "7d", "1m", "6m", "1y", "2y", "all";

    public BTCPriceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_btcprice, container, false);
        lineChart = (LineChart) (v.findViewById(R.id.chart1));
        button1 = (Button) v.findViewById(R.id.day1);
        button2 = (Button) v.findViewById(R.id.week1);
        button3 = (Button) v.findViewById(R.id.month1);
        button4 = (Button) v.findViewById(R.id.month6);
        button5 = (Button) v.findViewById(R.id.year1);
        button6 = (Button) v.findViewById(R.id.year2);
        button7 = (Button) v.findViewById(R.id.all);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        button7.setOnClickListener(this);

        retrieveBTCPriceData();
        retrieveGraphData("all");
        return v;
    }

    @Override
    public void onClick(View v) {
        int num = 6;
        switch(v.getId()){
            case R.id.day1:
                log.info("button day 1 clicked");
                num = 0;
                break;
            case R.id.week1:
                log.info("button week1 clicked");
                num = 1;
                break;
            case R.id.month1:
                log.info("button month1 clicked");
                num = 2;
                break;
            case R.id.month6:
                log.info("button month6 clicked");
                num = 3;
                break;
            case R.id.year1:
                log.info("button year1 clicked");
                num = 4;
                break;
            case R.id.year2:
                log.info("button year2 clicked");
                num = 5;
                break;
            case R.id.all:
                log.info("button all clicked");
                num = 6;
                break;
            default:
                break;
        }
        retrieveGraphData(times[num]);
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

    public void retrieveGraphData(String timespan){
        final String time = timespan;
        Thread t = new Thread(){
            @Override
            public void run() {
                String urlString = "https://api.blockchain.info/charts/market-price?format=json&timespan=" + time;
                try {
                    URL url = new URL(urlString);
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
