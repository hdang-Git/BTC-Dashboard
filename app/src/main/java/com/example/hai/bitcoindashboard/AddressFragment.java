package com.example.hai.bitcoindashboard;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddressFragment extends Fragment {

    EditText input;
    ImageButton imgButton;
    ListView list;
    FloatingActionButton fab;
    ArrayAdapter<String> adapter;
    Logger log = Logger.getAnonymousLogger();

    ArrayList<String> addrList; //arraylist of addresses to store
    String fileName = "addressStuff"; //filename of internal file
    File file;
    Context c;      //application context
    boolean validAddress = false;

    //default list of addresses
    String[] arr = {"198aMn6ZYAczwrE5NvNTUMyJ5qkfy4g3Hi", "1L8meqhMTRpxasdGt8DHSJfscxgHHzvPgk",
            "1kidsECR1aVTKACYQs5spxJASWAwLiX5y",  "17W3w9BTcsfKsoWAKX4bykRqdxpr6eKtCX",
            "3J5KeQSVBUEs3v2vEEkZDBtPLWqLTuZPuD", "3Nxwenay9Z8Lc9JBiywExpnEFiLp6Afp8v"};


    public AddressFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_address, container, false);

        input = (EditText) v.findViewById(R.id.input_addr);
        imgButton = (ImageButton) v.findViewById(R.id.imageButton);
        fab = (FloatingActionButton) v.findViewById(R.id.deleteFab);
        list = (ListView) v.findViewById(R.id.listView);

        //find file reference to implement internal file storage
        //file = getActivity().getFilesDir();
        //get application context
        c = getActivity().getApplicationContext();
        checkForExistingFile();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, addrList);
        list.setAdapter(adapter);

        //search for only one address that the user typed and add to beginning of arraylist
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("", "Image button is clicked");
                try{

                    String address = input.getText().toString();
                    retrieveAddressData(address);
                    if(validAddress){
                        addrList.add(0, address);       //add to beginning of arraylist, and shift all others
                        adapter.notifyDataSetChanged(); //update listview
                        writeFile(fileName);            //update file
                        validAddress = false;           //reset flag
                    }
                }catch(NullPointerException e){
                    Log.d("", "Null Pointer Error in AddressFragment - onCreateView() " + e);
                    e.printStackTrace();
                }
            }
        });

        //delete stored internal address file when this button is clicked
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    boolean deleted;
                    //File file = getActivity().getFileStreamPath(fileName);
                    File dir = getActivity().getFilesDir();
                    File file = new File(dir, fileName);
                    log.info("Delete internal address file called");
                    if(file.exists()){
                        log.info("file exists");
                        if(deleted = file.delete()){
                            log.info("file deleted ");
                            //TODO: fixme
                            checkForExistingFile();
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getContext(), "History cleared", Toast.LENGTH_SHORT).show();
                        } else {
                            log.info("No file to delete: " + deleted);
                        }
                    } else {
                        log.info("file DNE");
                        Toast.makeText(getContext(), "No previous history exists", Toast.LENGTH_SHORT).show();
                    }
                }catch(NullPointerException e){
                    e.printStackTrace();
                }
            }
        });

        //shows list of addresses to reference and calls retrieveAddressData() to populate balance field
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("", "onItemClick() for listview is called");
                String address = list.getItemAtPosition(position).toString(); //Get string of selected address in listview
                retrieveAddressData(address);
            }
        });

        return v;
    }

    //Checks for existing internal file. If doesn't exist, make one and load in arr into arrayList
    //If does exist, read and fill in arrayList
    public void checkForExistingFile(){
        //find file reference to implement internal file storage
        //file = getActivity().getFilesDir();
        file = getActivity().getFileStreamPath(fileName);
        log.info("checkForExistingFile() called");
        try{
            //if file doesn't exist create it else read from it
            if(!file.exists()){
                log.info("File DNE!");
                //Create a new file
                file = new File(fileName);
                //populate arraylist with array
                addrList = new ArrayList<>(Arrays.asList(arr));
                //write to file
                writeFile(fileName);
            } else {
                log.info("File Exists!");
                //read from file
                readFile(fileName);
            }
        } catch(NullPointerException e){
            e.printStackTrace();
        }
    }

    //Write to a file contents of arraylist
    public void writeFile(String file){
        log.info("writeFile() called");
        FileOutputStream output;
        ObjectOutputStream out;
        try{
            //In order to write arraylist, it must be serialized
            output = c.openFileOutput(file, Context.MODE_PRIVATE);
            out = new ObjectOutputStream(output);
            out.writeObject(addrList);
            out.flush();
            out.close();
            output.close();
            log.info("Writing completed successfully");
        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    //Read from file and populate arraylist
    public void readFile(String file){
        log.info("readFile() called");
        FileInputStream input;
        ObjectInputStream in;
        try{
            //In order to read arraylist, it must be deserialized
            input = c.openFileInput(file);
            in = new ObjectInputStream(input);
            addrList = (ArrayList) in.readObject();
            in.close();
            input.close();
            log.info("Reading completed successfully");
        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        } catch(ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public String appendToUrl(String address){
        return "http://btc.blockr.io/api/v1/address/balance/" + address;
        //return "https://blockchain.info/address/" + address + "?format=json";
    }

    public void retrieveAddressData(String url){
        log.info("retrieveAddressData() called");
        final String urlString = appendToUrl(url);
        Thread t = new Thread(){
            @Override
            public void run(){
                log.info("run() is called");
                try {
                    URL url = new URL(urlString);
                    //URL url = new URL("https://blockchain.info/address/198aMn6ZYAczwrE5NvNTUMyJ5qkfy4g3Hi?format=json");
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
                    validAddress = true;
                    log.info("It is a valid address");
                } catch(Exception e){
                    e.printStackTrace();
                    log.info("Not a valid address");
                }
            }
        };
        t.start();
    }

    Handler responseHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg){
            try {
                JSONObject addressObject = new JSONObject((String) msg.obj);
                JSONObject dataObject = addressObject.getJSONObject("data");
                ((TextView) getView().findViewById(R.id.addrBalance))
                        .setText("$ " + String.valueOf(dataObject.getString("balance")));
            } catch (JSONException e) {
                log.info("JSON Parsing error in AddressFragment - responseHandler()");
                e.printStackTrace();
            } catch (NullPointerException e){
                log.info("Nullpointer Exception in AddressFragment - responseHandler()");
                e.printStackTrace();
            }
            return true;
        }
    });
}
