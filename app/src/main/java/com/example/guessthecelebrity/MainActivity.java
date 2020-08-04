package com.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebURLs, celebNames;
    ImageView imageView;
    int randomCeleb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DownloadWebContent dl = new DownloadWebContent();
        imageView = findViewById(R.id.imageView);
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
        celebNames = new ArrayList<String>();
        int i=0;
        while(m.find()){
            celebNames.add(m.group(1));
            Log.i("Names",celebNames.get(i));
            i++;
        }
        Pattern pat = Pattern.compile("img src=\"(.*?)\"");
        Matcher mat = pat.matcher(split[1]);
        celebURLs = new ArrayList<String>();
        i=0;
        while(mat.find()){
            celebURLs.add(mat.group(1));
            Log.i("URL",celebURLs.get(i));
            i++;
        }
        generateQuiz();
    }

    public class DownloadImage extends  AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {
            try{
                URL url = new URL(strings[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream in = httpURLConnection.getInputStream();
                return BitmapFactory.decodeStream(in);
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
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

    public void generateQuiz(){
        Random r = new Random();
        randomCeleb = r.nextInt(celebURLs.size());
        String celebUrl = celebURLs.get(randomCeleb);

        DownloadImage dl = new DownloadImage();
        try {
            Bitmap image = dl.execute(celebUrl).get();
            imageView.setImageBitmap(image);
        }catch(Exception e){
            e.printStackTrace();
        }
        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);
        Button button4 = findViewById(R.id.button4);
        int randomAnswer = r.nextInt(4);
        String[] answers = new String[4];
        for(int i=0; i<4; i++){
            answers[i] = celebNames.get(r.nextInt(celebNames.size()));
        }
        answers[randomAnswer] = celebNames.get(randomCeleb);
        button1.setText(answers[0]);
        button2.setText(answers[1]);
        button3.setText(answers[2]);
        button4.setText(answers[3]);
    }

    public void checkAnswer(View view){
        Button button = (Button) view;
        String answer = button.getText().toString();
        Log.i("selected answer", answer);
        if(answer.equalsIgnoreCase(celebNames.get(randomCeleb))){
            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
            Log.i("Right", answer);
        }else{
            Toast.makeText(getApplicationContext(), "Wrong!", Toast.LENGTH_SHORT).show();
            Log.i("Wrong", answer);
        }
        generateQuiz();
    }
}