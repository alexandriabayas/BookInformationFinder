package com.example.finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

public class InfoWidget extends AppCompatActivity {

    ImageView cover;
    TextView title;
    TextView author;
    TextView genre;
    TextView description;
    Button back;

    AsyncThread thread;
    JSONObject jsonObject;

    String t;
    String a;
    String g;
    String d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget_info);

        title = findViewById(R.id.id_title);
        author = findViewById(R.id.id_author);
        genre = findViewById(R.id.id_genre);
        description = findViewById(R.id.id_description);
        back = findViewById(R.id.id_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendInfoBack = new Intent();
                sendInfoBack.putExtra(MainActivity.INTENT_CODE, "yee all done :)");
                setResult(RESULT_OK, sendInfoBack);
                finish();
            }
        });

        thread = new AsyncThread();
        thread.execute(getIntent().getStringExtra("CALL"));

        try{

            jsonObject = thread.get();

            String title = getIntent().getStringExtra("TITLE");
            Log.d("oof", "title: " + title);
            int index = 1;


            for(int i = 0; i < 20; i++){
                String test = jsonObject.getJSONArray("items").getJSONObject(i).getJSONObject("volumeInfo").getString("title");
                if(title.equalsIgnoreCase(test)){
                    index = i;
                    break;
                }
            }



            t = jsonObject.getJSONArray("items").getJSONObject(index).getJSONObject("volumeInfo").getString("title");
            a = jsonObject.getJSONArray("items").getJSONObject(index).getJSONObject("volumeInfo").getJSONArray("authors").getString(0);
            g = jsonObject.getJSONArray("items").getJSONObject(index).getJSONObject("volumeInfo").getJSONArray("categories").getString(0);
            d = jsonObject.getJSONArray("items").getJSONObject(index).getJSONObject("volumeInfo").getString("description");


        }catch(ExecutionException e){
            Log.d("TAG", "e");
        }catch(InterruptedException i){
            Log.d("TAG", "i");
        }catch (JSONException j){
            Log.d("TAG", "j");
        }

        title.setText(t);
        author.setText(a);
        genre.setText(g);
        description.setText(d);

    }

    public class AsyncThread extends AsyncTask<String, Void, JSONObject>{
        @Override
        protected JSONObject doInBackground(String... strings) {
            try{
                URL url = new URL(strings[0]);
                URLConnection urlConnection = url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String str = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    str += (line + "\n");
                }


                JSONObject jsonObject = new JSONObject(str);
                return jsonObject;

            }catch(MalformedURLException m){
                Log.d("TAG", "m");
            } catch(IOException ioe){
                Log.d("TAG", "i");
            } catch(JSONException j){
                Log.d("TAG", "j");
            }

            return null;
        }
    }


}
