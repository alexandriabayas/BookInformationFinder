package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.AsynchronousChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText editTextBookSearch;
    Button buttonTextSearch;
    Button buttonImageSearch;
    Button buttonSeePrevious;

    Bitmap photo;

    String title = "Book Title";

    static final int CAMERA_REQUEST = 1888;
    static final int MY_CAMERA_PERMISSION_CODE = 100;

    static final int NUMBER_CODE = 1234;
    static final String INTENT_CODE = "abcd";


    ArrayList<String> titles = new ArrayList<>();

    SharedPreferences settings;
    SharedPreferences.Editor editor;

    String name = "a";

    //Google Books key: AIzaSyAP9pjKtgrwTpgYm1Pp8T5fnQpqx9u6rng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextBookSearch = findViewById(R.id.id_editTextBook);
        buttonTextSearch = findViewById(R.id.id_buttonSearch);
        buttonImageSearch = findViewById(R.id.id_buttonImageSearch);
        buttonSeePrevious = findViewById(R.id.id_buttonPrevious);


        editTextBookSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                title = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        buttonImageSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);

                }
            }
        });

        buttonTextSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.equals("Book Title")){
                    Toast.makeText(MainActivity.this, "Please enter valid book title", Toast.LENGTH_SHORT).show();
                } else{
                    intent(title);
                }
            }
        });

        buttonSeePrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent intent = new Intent(MainActivity.this, PreviousSearches.class);
                 intent.putExtra("LIST", titles);
                 startActivity(intent);

            }
        });
    }


    public void intent(String title){

        String call = "https://www.googleapis.com/books/v1/volumes?q=" + title.replaceAll(" ", "&") + "&key=AIzaSyAP9pjKtgrwTpgYm1Pp8T5fnQpqx9u6rng";

        settings = this.getSharedPreferences("com.example.finalproject", 0);
        editor = settings.edit();

        editor.putString(name, title);
        name += "a";


        editor.commit();
        editor.clear();

        Intent intentToLoad = new Intent(MainActivity.this, InfoWidget.class);

        intentToLoad.putExtra("CALL", call);
        intentToLoad.putExtra("TITLE", title);
        startActivityForResult(intentToLoad, NUMBER_CODE);

    }

    @Override
    protected void onResume() {
        titles = getPreferences();
        super.onResume();
    }

    public ArrayList<String> getPreferences(){
        settings = this.getSharedPreferences("com.example.finalproject", 0);
        ArrayList<String> arrayList = new ArrayList<>();
        String n = "a";

        while(!settings.getString(n, "").equals("")){
            arrayList.add(settings.getString(n, ""));
            n+="a";
        }


        Log.d("oof", arrayList.toString());
        return arrayList;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");

            Matrix matrix = new Matrix();
            matrix.postRotate(270);
            photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);


            TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

            if(!textRecognizer.isOperational()) {
                Log.d("oof","Not Operational");
            } else{
                Log.d("oof", "Operational");
            }


            Frame imageFrame = new Frame.Builder().setBitmap(photo).build();

            String imageText = "";

            SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);

            for (int i = 0; i < textBlocks.size(); i++) {
                TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));
                imageText = textBlock.getValue();
            }



            Log.d("oof", imageText);
            intent(imageText);
        }
        if(requestCode == NUMBER_CODE && resultCode == RESULT_OK){
            titles = getPreferences();
        }
    }






}
