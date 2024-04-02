package com.example.myapplication.objects;

import android.graphics.Bitmap;

import java.util.Date;

public class Observation {
    public static String TABLE_NAME = "observation";
    public static String COLUMN_ID = "observation_id";
    public static String COLUMN_HIKE_ID = "hike_id";
    public static String COLUMN_NAME = "observation_name";
    public static String COLUMN_TIME = "time_of_observation";
    public static String COLUMN_IMAGE = "observation_image";
    public static String COLUMN_COMMENT = "observation_comment";

    public static final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_HIKE_ID + " INTEGER NOT NULL, " +
            COLUMN_NAME + " TEXT NOT NULL, " +
            COLUMN_TIME + " TEXT NOT NULL, " +
            COLUMN_IMAGE + " BLOB , " +
            COLUMN_COMMENT + " TEXT)";
    private int observation_Id;
    private int hike_Id;
    private String observation_name;

    private Date time_of_observation;

    private Bitmap observation_image;

    private String observation_comment;

    public Observation(int observation_Id, int hike_Id, String observation_name, Date time_of_observation, Bitmap observation_image, String observation_comment) {
        this.observation_Id = observation_Id;
        this.hike_Id = hike_Id;
        this.observation_name = observation_name;
        this.time_of_observation = time_of_observation;
        this.observation_image = observation_image;
        this.observation_comment = observation_comment;
    }

    public int getObservation_Id() {
        return observation_Id;
    }

    public void setObservation_Id(int observation_Id) {
        this.observation_Id = observation_Id;
    }

    public int getHike_Id() {
        return hike_Id;
    }

    public void setHike_Id(int hike_Id) {
        this.hike_Id = hike_Id;
    }

    public String getObservation_name() {
        return observation_name;
    }

    public void setObservation_name(String observation_name) {
        this.observation_name = observation_name;
    }

    public Date getTime_of_observation() {
        return time_of_observation;
    }

    public void setTime_of_observation(Date time_of_observation) {
        this.time_of_observation = time_of_observation;
    }

    public Bitmap getObservation_image() {
        return observation_image;
    }

    public void setObservation_image(Bitmap observation_image) {
        this.observation_image = observation_image;
    }

    public String getObservation_comment() {
        return observation_comment;
    }

    public void setObservation_comment(String observation_comment) {
        this.observation_comment = observation_comment;
    }
}
