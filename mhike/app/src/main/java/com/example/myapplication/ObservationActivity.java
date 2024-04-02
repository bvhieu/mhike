package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.ObservationAdapter;
import com.example.myapplication.database.ConnectDatabase;
import com.example.myapplication.objects.Observation;
import com.example.myapplication.util.Memory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ObservationActivity extends AppCompatActivity {
    FloatingActionButton btn_add_observation;
    RecyclerView observation_recycle_view;
    ObservationAdapter observation_recycle_view_adapter;
    ConnectDatabase connectDatabase;
    ArrayList<Observation> adapterData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation);
        connectDatabase = new ConnectDatabase(this);
        if (getIntent().hasExtra("hikeId") && getIntent().hasExtra("hikeName")) {
            Memory.setHikeId(getIntent().getIntExtra("hikeId", -1));
            Memory.setHikename(getIntent().getStringExtra("hikeName"));
        }
        setTitle(Memory.hikeName);
        observation_recycle_view = findViewById(R.id.recycle_view_observation);
        btn_add_observation = findViewById(R.id.btn_add_observation);
        btn_add_observation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ObservationActivity.this, EditObservationActivity.class);

                intent.putExtra("function", "ADD");
                startActivity(intent);
            }
        });
        getAllObservation();
        observation_recycle_view_adapter = new ObservationAdapter(ObservationActivity.this, adapterData);
        observation_recycle_view.setAdapter(observation_recycle_view_adapter);
        observation_recycle_view.setLayoutManager(new LinearLayoutManager(ObservationActivity.this));
    }

    private void getAllObservation() {
        Cursor cursor = connectDatabase.getAllObservation(Memory.hikeId);
        if (cursor.getCount() != 0) {
            setAdapterData(cursor);
        }
    }

    private void sortObservation(int order) {
        Cursor cursor = connectDatabase.sortObservation(order, Memory.hikeId);
        if (cursor.getCount() != 0) {
            setAdapterData(cursor);
        }
    }

    private void searchObservation(String keyWord) {
        Cursor cursor = connectDatabase.searchObservation(keyWord, Memory.hikeId);
        if (cursor.getCount() != 0) {
            setAdapterData(cursor);
        }
    }

    @SuppressLint("Range")
    private void setAdapterData(Cursor cursor) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.UK);
        Date timeOfObservation = null;
        byte[] imageBytes;
        while (cursor.moveToNext()) {
            try {
                timeOfObservation = dateFormat.parse(cursor.getString(cursor.getColumnIndex(Observation.COLUMN_TIME)));
            } catch (Exception ex) {
                Toast.makeText(this, "Get data error", Toast.LENGTH_SHORT).show();
            }
            imageBytes = cursor.getBlob(cursor.getColumnIndex(Observation.COLUMN_IMAGE));
            Observation newObservation = new Observation(
                    cursor.getInt(cursor.getColumnIndex(Observation.COLUMN_ID)),
                    cursor.getInt(cursor.getColumnIndex(Observation.COLUMN_HIKE_ID)),
                    cursor.getString(cursor.getColumnIndex(Observation.COLUMN_NAME)),
                    timeOfObservation,
                    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length),
                    cursor.getString(cursor.getColumnIndex(Observation.COLUMN_COMMENT))
            );
            adapterData.add(newObservation);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapterData.clear();
                searchObservation(s);
                observation_recycle_view_adapter.notifyDataSetChanged();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.removeAll) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ObservationActivity.this);
            builder.setTitle("Remove all hike?");
            builder.setMessage("Are you sure to remove all hike?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    connectDatabase.deleteAllObservation();
                    adapterData.clear();
                    observation_recycle_view_adapter.notifyDataSetChanged();
                    Intent intent = new Intent(ObservationActivity.this, HikeActivity.class);
                    Toast.makeText(ObservationActivity.this, "Remove successfully", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.create().show();
        }
        if (item.getItemId() == R.id.sortAToZ) {
            adapterData.clear();
            sortObservation(0);
            observation_recycle_view_adapter.notifyDataSetChanged();
        }
        if (item.getItemId() == R.id.sortZToA) {
            adapterData.clear();
            sortObservation(1);
            observation_recycle_view_adapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }
}