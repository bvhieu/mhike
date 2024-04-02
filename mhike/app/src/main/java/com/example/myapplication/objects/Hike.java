package com.example.myapplication.objects;

import java.util.Date;

public class Hike {
    public static String TABLE_NAME = "hike";
    public static String COLUMN_ID = "hike_id";
    public static String COLUMN_NAME = "hike_name";
    public static String COLUMN_LOCATION = "hike_location";
    public static String COLUMN_HIKE_DATE = "date_of_hike";
    public static String COLUMN_PARKING_AVAILABLE = "parking_available";
    public static String COLUMN_HIKE_LENGTH = "length_of_hike";
    public static String COLUMN_HIKER_NUMBER = "hiker_number";
    public static String COLUMN_HIKE_WEATHER = "weather_of_hike";
    public static String COLUMN_DIFFICULT_LEVEL = "level_of_difficult";
    public static String COLUMN_HIKE_DESCRIPTION = "hike_description";

    public static final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT NOT NULL, " +
            COLUMN_LOCATION + " TEXT NOT NULL, " +
            COLUMN_HIKE_DATE + " TEXT NOT NULL, " +
            COLUMN_PARKING_AVAILABLE + " BOOLEAN NOT NULL, " +
            COLUMN_HIKE_LENGTH + " REAL NOT NULL, " +
            COLUMN_DIFFICULT_LEVEL + " TEXT NOT NULL, " +
            COLUMN_HIKER_NUMBER + " INTEGER, " +
            COLUMN_HIKE_WEATHER + " TEXT, " +
            COLUMN_HIKE_DESCRIPTION + " TEXT)";

    private int hike_id;
    private String hike_name;
    private String hike_location;
    private Date date_of_hike;
    private boolean parking_available;
    private float length_of_hike;
    private String hike_level;
    private int hiker_number;
    private String weather_of_hike;
    private String hike_description;

    public Hike(int hike_id, String hike_name, String hike_location, Date date_of_hike, boolean parking_available, float length_of_hike, String hike_level, int hiker_number, String weather_of_hike, String hike_description) {
        this.hike_id = hike_id;
        this.hike_name = hike_name;
        this.hike_location = hike_location;
        this.date_of_hike = date_of_hike;
        this.parking_available = parking_available;
        this.length_of_hike = length_of_hike;
        this.hike_level = hike_level;
        this.hiker_number = hiker_number;
        this.weather_of_hike = weather_of_hike;
        this.hike_description = hike_description;
    }

    public int getHike_id() {
        return hike_id;
    }

    public void setHike_id(int hike_id) {
        this.hike_id = hike_id;
    }

    public String getHike_name() {
        return hike_name;
    }

    public void setHike_name(String hike_name) {
        this.hike_name = hike_name;
    }

    public String getHike_location() {
        return hike_location;
    }

    public void setHike_location(String hike_location) {
        this.hike_location = hike_location;
    }

    public Date getDate_of_hike() {
        return date_of_hike;
    }

    public void setDate_of_hike(Date date_of_hike) {
        this.date_of_hike = date_of_hike;
    }

    public boolean isParking_available() {
        return parking_available;
    }

    public void setParking_available(boolean parking_available) {
        this.parking_available = parking_available;
    }

    public float getLength_of_hike() {
        return length_of_hike;
    }

    public void setLength_of_hike(float length_of_hike) {
        this.length_of_hike = length_of_hike;
    }

    public String getHike_level() {
        return hike_level;
    }

    public void setHike_level(String hike_level) {
        this.hike_level = hike_level;
    }

    public int getHiker_number() {
        return hiker_number;
    }

    public void setHiker_number(int hiker_number) {
        this.hiker_number = hiker_number;
    }

    public String getWeather_of_hike() {
        return weather_of_hike;
    }

    public void setWeather_of_hike(String weather_of_hike) {
        this.weather_of_hike = weather_of_hike;
    }

    public String getHike_description() {
        return hike_description;
    }

    public void setHike_description(String hike_description) {
        this.hike_description = hike_description;
    }
}
