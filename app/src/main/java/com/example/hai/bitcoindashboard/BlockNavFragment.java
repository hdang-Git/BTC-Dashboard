package com.example.hai.bitcoindashboard;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.logging.Logger;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlockNavFragment extends Fragment {
    RelativeLayout view;
    ArrayList<BlockFragment> fragments = new ArrayList<>();
    int currentIndex = 0;
    int sizeIndex = 0;
    int fragNum = 1;
    Logger log = Logger.getAnonymousLogger();
    ViewPager pager;
    PagerAdapter pa;
    Button enterButton;
    FloatingActionButton addButton;
    EditText textField;
    int num;
    BlockFragment receiver;

    public BlockNavFragment() {
    }        // Required empty public constructor


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_blocknav, container, false);
        view = (RelativeLayout) v.findViewById(R.id.blocknavfrag);

        log.info("onCreateView() called");
        enterButton = (Button) v.findViewById(R.id.enterButton);
        addButton = (FloatingActionButton) v.findViewById(R.id.addFab);
        textField = (EditText) v.findViewById(R.id.inputField);


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log.info("addButton called");
                BlockFragment fragment = new BlockFragment();
                fragments.add(sizeIndex, fragment);
                fragNum++;
                //link to adapter and notify of the change
                pa = pager.getAdapter();
                pa.notifyDataSetChanged();
                pager.setCurrentItem(sizeIndex);
            }
        });


        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = textField.getText().toString();
                try{
                    num = Integer.parseInt(input);
                    log.info("Success, input is number.");
                    //Pass info to child fragment BlockFragment
                    currentIndex = pager.getCurrentItem();
                    receiver = fragments.get(currentIndex);
                    receiver.retrieveBlockData(num);

                    log.info("Success with retriever");
                }catch(NumberFormatException e){
                    Log.i("", "Number Format Error: "+ e);
                } catch(NullPointerException f){
                    Log.i("", "Null Pointer: " + f);
                }
            }
        });

        //Set up view pager
        pager = (ViewPager) v.findViewById(R.id.pager);
        pager.setOffscreenPageLimit(3); // the number of "off screen" pages to keep loaded each side of the current page
        pager.setAdapter(new CustomPagerAdapter(getChildFragmentManager()));
        return v;
    }

    private class CustomPagerAdapter extends FragmentPagerAdapter {

        public CustomPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {
                default:
                    BlockFragment frag = new BlockFragment();
                    fragments.add(sizeIndex, frag);
                    sizeIndex++;
                    return frag;
            }
        }

        @Override
        public int getCount() {
            return fragNum;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        BlockFragment block = new BlockFragment();
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.blockNav, block)
                .commit();

        */
    }


}


