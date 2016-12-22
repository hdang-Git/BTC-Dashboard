package com.example.hai.bitcoindashboard;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Logger;

import static java.util.logging.Logger.getAnonymousLogger;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlockFragment extends Fragment {

    Logger log = getAnonymousLogger();
    public BlockFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_block, container, false);
        return v;
    }


    public String concatNumWithUrl(int num){
        String baseUrl;
        //Error handling for url string which should start at 1
        if(num > 0){
            baseUrl = "http://btc.blockr.io/api/v1/block/info/" + num;
        } else {
            baseUrl = null;
        }
        return baseUrl;
    }

    public void retrieveBlockData(int num){
        log.info("retrieveBlockData called with num: " + num);
        final String urlString = concatNumWithUrl(num);
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
                        response.concat(tmpString);
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
                ((TextView) getView().findViewById(R.id.value3)).setText(String.valueOf(data.getString("nb")));
                ((TextView) getView().findViewById(R.id.value4)).setText(data.getString("hash"));
                ((TextView) getView().findViewById(R.id.value5)).setText(data.getString("version"));
                ((TextView) getView().findViewById(R.id.value6)).setText(data.getString("confirmations"));
                ((TextView) getView().findViewById(R.id.value7)).setText(data.getString("time_utc"));
                ((TextView) getView().findViewById(R.id.value8)).setText(data.getString("nb_txs"));
                ((TextView) getView().findViewById(R.id.value9)).setText(data.getString("merkleroot"));
                ((TextView) getView().findViewById(R.id.value10)).setText(data.getString("next_block_nb"));
                ((TextView) getView().findViewById(R.id.value11)).setText(data.getString("prev_block_nb"));
                ((TextView) getView().findViewById(R.id.value12)).setText(data.getString("next_block_hash"));
                ((TextView) getView().findViewById(R.id.value13)).setText(data.getString("prev_block_hash"));
                ((TextView) getView().findViewById(R.id.value14)).setText(data.getString("fee"));
                ((TextView) getView().findViewById(R.id.value15)).setText(data.getString("vout_sum"));
                ((TextView) getView().findViewById(R.id.value16)).setText(data.getString("size"));
                ((TextView) getView().findViewById(R.id.value17)).setText(data.getString("difficulty"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
    });

}
