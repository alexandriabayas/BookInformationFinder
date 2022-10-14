package com.example.finalproject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class PreviousSearches extends AppCompatActivity {

    ListView listView;
    CustomAdapter customAdapter;
    Button close;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searches_previous);

        listView = findViewById(R.id.id_listView);
        close = findViewById(R.id.id_searchclose);

        ArrayList<String> arrayList = (ArrayList<String>)getIntent().getSerializableExtra("LIST");

        customAdapter = new CustomAdapter(this, R.layout.adapter_custom, arrayList);
        listView.setAdapter(customAdapter);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }

    public class CustomAdapter extends ArrayAdapter<String> {

        private List<String> list;
        private Context context;
        private int xmlresource;

        public CustomAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
            super(context, resource, objects);
            this.context = context;
            xmlresource = resource;
            list = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View adapterView = layoutInflater.inflate(xmlresource, null);

            TextView textView = adapterView.findViewById(R.id.id_customadapter_textView);

            textView.setText("" + list.get(position));

            return adapterView;
        }
    }
}
