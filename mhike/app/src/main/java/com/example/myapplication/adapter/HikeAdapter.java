package com.example.myapplication.adapter;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.EditHikeActivity;
import com.example.myapplication.HikeActivity;
import com.example.myapplication.ObservationActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.ConnectDatabase;
import com.example.myapplication.objects.Hike;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class HikeAdapter extends RecyclerView.Adapter<HikeAdapter.MyViewHolder> {

    Context context;
    private ArrayList<Hike> adapterData;

    public HikeAdapter(Context context, ArrayList<Hike> adapterData) {
        this.context = context;
        this.adapterData = adapterData;
    }

    @NonNull
    @Override
    public HikeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.hike_recycle_view_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HikeAdapter.MyViewHolder holder, int position) {
        Hike currentHike = adapterData.get(position);

        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.UK);
        holder.txt_hike_id.setText(String.valueOf(currentHike.getHike_id()));
        holder.txt_hike_name.setText(currentHike.getHike_name());
        holder.txt_location.setText(currentHike.getHike_location());
        holder.txt_hiker_number.setText(String.valueOf(currentHike.getHiker_number()));
        holder.txt_date_of_hike.setText(dateFormat.format(currentHike.getDate_of_hike()));

        setDifficultyLevelColor(holder.txt_level_of_difficulty, currentHike.getHike_level());
        holder.txt_level_of_difficulty.setText(currentHike.getHike_level());

        holder.txt_length_of_hiker.setText(currentHike.getLength_of_hike() + "Km");

        setObservationDirectionOnClickListener(holder.img_observation_direction, currentHike.getHike_id(), currentHike.getHike_name());

        setHikeDetailOnClickListener(holder.img_hike_detail, currentHike);

        setHikeItemLongClickListener(holder.hikeItemLayout, currentHike.getHike_name(), currentHike.getHike_id());
    }

    private void setDifficultyLevelColor(TextView textView, String level) {
        int colorResId;
        switch (level) {
            case "Very easy":
                colorResId = R.color.levelVeryEasy;
                break;
            case "Easy":
                colorResId = R.color.levelEasy;
                break;
            case "Medium":
                colorResId = R.color.levelMedium;
                break;
            case "Hard":
                colorResId = R.color.levelHard;
                break;
            case "Very hard":
                colorResId = R.color.levelVeryHard;
                break;
            default:
                colorResId = android.R.color.black; // Default color
        }
        textView.setTextColor(ContextCompat.getColor(context, colorResId));
    }

    private void setObservationDirectionOnClickListener(ImageView imageView, int hikeId, String hikeName) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ObservationActivity.class);
                intent.putExtra("hikeId", hikeId);
                intent.putExtra("hikeName", hikeName);
                context.startActivity(intent);
            }
        });
    }

    private void setHikeDetailOnClickListener(ImageView imageView, Hike hike) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditHikeActivity.class);
                intent.putExtra("function", "UPDATE");
                intent.putExtra("hikeId", hike.getHike_id());
                intent.putExtra("hikeName", hike.getHike_name());
                intent.putExtra("hikeLocation", hike.getHike_location());
                String myFormat = "MM/dd/yyyy";
                SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.UK);
                intent.putExtra("dateOfHike", dateFormat.format(hike.getDate_of_hike()));
                intent.putExtra("parkingAvailable", String.valueOf(hike.isParking_available()));
                intent.putExtra("hikeLength", String.valueOf(hike.getLength_of_hike()));
                intent.putExtra("levelOfDifficulty", hike.getHike_level());
                intent.putExtra("hikeDescription", hike.getHike_description());
                intent.putExtra("hikerNumber", String.valueOf(hike.getHiker_number()));
                intent.putExtra("hikeWeather", hike.getWeather_of_hike());
                context.startActivity(intent);
            }
        });
    }

    private void setHikeItemLongClickListener(View view, String hikeName, int hikeId) {
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Remove " + hikeName + "?");
                builder.setMessage("Are you sure to remove " + hikeName + "?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ConnectDatabase connectDatabase = new ConnectDatabase(context);
                        long result = connectDatabase.deleteOneHike(String.valueOf(hikeId));
                        if (result == -1) {
                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Remove successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, HikeActivity.class);
                            context.startActivity(intent);
                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                });
                builder.create().show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return adapterData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_hike_id, txt_hike_name, txt_location, txt_date_of_hike, txt_hiker_number, txt_level_of_difficulty, txt_length_of_hiker;
        ImageView img_observation_direction, img_hike_detail;
        LinearLayout hikeItemLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_hike_id = itemView.findViewById(R.id.textview_hike_id);
            txt_hike_name = itemView.findViewById(R.id.textview_hike_name);
            txt_location = itemView.findViewById(R.id.textview_location);
            txt_date_of_hike = itemView.findViewById(R.id.textview_date_of_hike);
            txt_hiker_number = itemView.findViewById(R.id.textview_hiker_number);
            txt_level_of_difficulty = itemView.findViewById(R.id.textview_level_of_difficulty);
            txt_length_of_hiker = itemView.findViewById(R.id.textview_length_of_hiker);
            img_observation_direction = itemView.findViewById(R.id.image_observation_direction);
            img_hike_detail = itemView.findViewById(R.id.image_hike_detail);
            hikeItemLayout = itemView.findViewById(R.id.hikeItemLayout);
        }
    }


}
