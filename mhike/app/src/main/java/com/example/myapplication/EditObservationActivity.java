package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.database.ConnectDatabase;
import com.example.myapplication.util.Memory;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditObservationActivity extends AppCompatActivity {
    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.UK);
    TextInputEditText txt_observation_name, txt_time_of_observation, txt_observation_comment;
    Button btn_upload_observation_image, btn_save_observation;
    ImageView img_observation;
    ConnectDatabase connectDatabase;
    String function;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation_edit);

        connectDatabase = new ConnectDatabase(this);
        img_observation = findViewById(R.id.img_observation);
        txt_observation_name = findViewById(R.id.txt_observation_name);
        txt_time_of_observation = findViewById(R.id.txt_time_of_observation);
        txt_observation_comment = findViewById(R.id.txt_observation_comment);
        btn_upload_observation_image = findViewById(R.id.btn_upload_observation_image);
        btn_save_observation = findViewById(R.id.btn_save_observation);
        txt_time_of_observation.setText(dateFormat.format(calendar.getTime()));

        if (getIntent().hasExtra("function")) {
            function = getIntent().getStringExtra("function");
        }

        if (function.equals("UPDATE")) {
            if (getIntent().hasExtra("observationId") && getIntent().hasExtra("observationName") &&
                    getIntent().hasExtra("observationImage") && getIntent().hasExtra("timeOfObservation") && getIntent().hasExtra("observationComment")) {
                btn_save_observation.setText("Update");
                setTitle(getIntent().getStringExtra("observationName"));
                txt_observation_name.setText(getIntent().getStringExtra("observationName"));
                txt_time_of_observation.setText(getIntent().getStringExtra("timeOfObservation"));
                txt_observation_comment.setText(getIntent().getStringExtra("observationComment"));
                byte[] imageBytes = getIntent().getByteArrayExtra("observationImage");
                img_observation.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
            }
        }


        btn_upload_observation_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionListener permissionlistener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        ImagePicker.with(EditObservationActivity.this)
                                .crop()
                                .compress(1024)
                                .maxResultSize(1080, 1080)
                                .start();
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Toast.makeText(EditObservationActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                    }
                };
                TedPermission.create()
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .check();
            }
        });

        txt_time_of_observation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateTimePicker(txt_time_of_observation);
            }
        });

        btn_save_observation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //validation
                if (!isTextInputEditTextEmpty(txt_observation_name)) {
                    img_observation.setDrawingCacheEnabled(true);
                    img_observation.buildDrawingCache();
                    Bitmap bitmap = img_observation.getDrawingCache();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] imageByteArray = stream.toByteArray();

                    String observationName = txt_observation_name.getText().toString().trim();
                    String timeOfObservation = txt_time_of_observation.getText().toString().trim();
                    String observationComment = txt_observation_comment.getText().toString().trim().length() == 0 ? "" : txt_observation_comment.getText().toString().trim();

                    long result = function.equals("ADD") ?
                            connectDatabase.addNewObservation(observationName, Memory.hikeId, imageByteArray, timeOfObservation, observationComment) :
                            connectDatabase.updateObservation(getIntent().getStringExtra("observationId"), observationName, Memory.hikeId, imageByteArray, timeOfObservation, observationComment);
                    if (result == -1) {
                        Toast.makeText(EditObservationActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    } else {
                        if (function.equals("ADD")) refreshInputForm();
                        Toast.makeText(EditObservationActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void dateTimePicker(final EditText editText) {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        editText.setText(dateFormat.format(calendar.getTime()));
                    }
                };
                new TimePickerDialog(EditObservationActivity.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        };
        new DatePickerDialog(EditObservationActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            img_observation.setImageURI(uri);
        } else {
            Toast.makeText(EditObservationActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    //validation
    private boolean isTextInputEditTextEmpty(TextInputEditText txt) {

        if (txt.getText().toString().trim().length() == 0) {
            txt.setError("This field is required");
            return true;
        }
        return false;
    }


    //Refresh input form
    private void refreshInputForm() {
        img_observation.setImageResource(R.drawable.no_image_icon);
        txt_observation_name.setText("");
        txt_time_of_observation.setText(dateFormat.format(calendar.getTime()));
        txt_observation_comment.setText("");
    }
}