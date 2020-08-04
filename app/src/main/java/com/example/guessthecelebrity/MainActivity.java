package com.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DownloadWebContent dl = new DownloadWebContent();
        String result="";
        try {
            result = dl.execute("https://web.archive.org/web/20200118162958/http://www.posh24.se/kandisar").get();
            //Log.i("Result", result);
        }catch (Exception e){
            e.printStackTrace();
            Log.i("Get Failed","Something wrong with url");
        }
        String[] split = result.split("<div class=\"sidebarContainer\">");
        split = split[0].split("<h1 class=\"header\">Topp 100 kändisar</h1>");

        Pattern p = Pattern.compile(" alt=\"(.*?)\"");
        Matcher m = p.matcher(split[1]);
        ArrayList<String> celebNames = new ArrayList<String>();
        int i=0;
        while(m.find()){
            celebNames.add(m.group(1));
            Log.i("Names",celebNames.get(i));
            i++;
        }
        Pattern pat = Pattern.compile("img src=\"(.*?)\"");
        Matcher mat = pat.matcher(split[1]);
        ArrayList<String> celebURLs = new ArrayList<String>();
        i=0;
        while(mat.find()){
            celebURLs.add(mat.group(1));
            Log.i("URL",celebURLs.get(i));
            i++;
        }
    }

    public class DownloadWebContent extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            try{
                URL url = new URL(strings[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data;
                char c;
                String s="";
                do{
                    data = reader.read();
                    c = (char) data;
                    s += c;
                }while (data!=-1);
                return s;
            }catch (Exception e){
                e.printStackTrace();
                return "failed";
            }
        }
    }
}