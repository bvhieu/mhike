package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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

import com.example.myapplication.adapter.HikeAdapter;
import com.example.myapplication.database.ConnectDatabase;
import com.example.myapplication.objects.Hike;
import com.example.myapplication.util.Memory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HikeActivity extends AppCompatActivity {

    FloatingActionButton btn_add_hike;
    RecyclerView hike_recycle_view;
    HikeAdapter hike_recycle_view_adapter;
    ConnectDatabase connectDatabase;
    ArrayList<Hike> adapterData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike);
        connectDatabase = new ConnectDatabase(this);
        adapterData = new ArrayList<>();
        hike_recycle_view = findViewById(R.id.recycle_view_hike);
        btn_add_hike = findViewById(R.id.btn_add_hike);
        btn_add_hike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HikeActivity.this, EditHikeActivity.class);
                if (getIntent().hasExtra("hikeId")) {
                    Memory.setHikeId(getIntent().getIntExtra("hikeId", -1));
                }
                intent.putExtra("function", "ADD");
                startActivity(intent);
            }
        });

        getAllHike();
        hike_recycle_view_adapter = new HikeAdapter(HikeActivity.this, adapterData);
        hike_recycle_view.setAdapter(hike_recycle_view_adapter);
        hike_recycle_view.setLayoutManager(new LinearLayoutManager(HikeActivity.this));


    }

    private void getAllHike() {
        Cursor cursor = connectDatabase.getAllHike();
        if (cursor.getCount() != 0) {
            setAdapterData(cursor);
        }
    }

    private void sortHike(int order) {
        Cursor cursor = connectDatabase.sortHike(order);
        if (cursor.getCount() != 0) {
            setAdapterData(cursor);
        }
    }

    private void searchHike(String keyWord) {
        Cursor cursor = connectDatabase.searchHike(keyWord);
        if (cursor.getCount() != 0) {
            setAdapterData(cursor);
        }
    }

    @SuppressLint("Range")
    private void setAdapterData(Cursor cursor) {
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.UK);
        Date dateOfHike = null;
        while (cursor.moveToNext()) {
            try {
                dateOfHike = dateFormat.parse(cursor.getString(cursor.getColumnIndex(Hike.COLUMN_HIKE_DATE)));
            } catch (Exception ex) {
                Toast.makeText(this, "Get data error", Toast.LENGTH_SHORT).show();
            }
            Hike newHike = new Hike(
                    cursor.getInt(cursor.getColumnIndex(Hike.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(Hike.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(Hike.COLUMN_LOCATION)),
                    dateOfHike,
                    cursor.getString(cursor.getColumnIndex(Hike.COLUMN_PARKING_AVAILABLE)).equals("True") ? true : false,
                    cursor.getFloat(cursor.getColumnIndex(Hike.COLUMN_HIKE_LENGTH)),
                    cursor.getString(cursor.getColumnIndex(Hike.COLUMN_DIFFICULT_LEVEL)),
                    cursor.getInt(cursor.getColumnIndex(Hike.COLUMN_HIKER_NUMBER)),
                    cursor.getString(cursor.getColumnIndex(Hike.COLUMN_HIKE_WEATHER)),
                    cursor.getString(cursor.getColumnIndex(Hike.COLUMN_HIKE_DESCRIPTION))
            );
            adapterData.add(newHike);
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
                searchHike(s);
                hike_recycle_view_adapter.notifyDataSetChanged();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.removeAll) {
            AlertDialog.Builder builder = new AlertDialog.Builder(HikeActivity.this);
            builder.setTitle("Remove all hike?");
            builder.setMessage("Are you sure to remove all hike?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    connectDatabase.deleteAllHike();
                    adapterData.clear();
                    hike_recycle_view_adapter.notifyDataSetChanged();
                    Intent intent = new Intent(HikeActivity.this, HikeActivity.class);
                    Toast.makeText(HikeActivity.this, "Remove successfully", Toast.LENGTH_SHORT).show();
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
            sortHike(0);
            hike_recycle_view_adapter.notifyDataSetChanged();
        }
        if (item.getItemId() == R.id.sortZToA) {
            adapterData.clear();
            sortHike(1);
            hike_recycle_view_adapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }
}