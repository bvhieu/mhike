package com.example.myapplication.adapter;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.EditObservationActivity;
import com.example.myapplication.ObservationActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.ConnectDatabase;
import com.example.myapplication.objects.Observation;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ObservationAdapter extends RecyclerView.Adapter<ObservationAdapter.MyViewHolder> {
    Context context;
    private ArrayList<Observation> adapterData;

    public ObservationAdapter(Context context, ArrayList<Observation> adapterData) {
        this.context = context;
        this.adapterData = adapterData;
    }

    public static String createNewDirectory(String directoryName) {
        File newDirectory = new File(Environment.getExternalStorageDirectory(), directoryName);
        if (!newDirectory.exists()) {
            if (newDirectory.mkdirs()) {
                return newDirectory.getAbsolutePath();
            } else {
                return null; // Directory creation failed
            }
        } else {
            return newDirectory.getAbsolutePath(); // Directory already exists
        }
    }

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.observation_recycle_view_item, parent, false);
        return new ObservationAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.UK);
        Bitmap imageSrc = adapterData.get(position).getObservation_image();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageSrc.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageByteArray = stream.toByteArray();

        if (imageSrc == null) {
            holder.btn_share_facebook.setVisibility(View.INVISIBLE);
            holder.img_observation.setVisibility(View.INVISIBLE);
        } else
            holder.img_observation.setImageBitmap(adapterData.get(position).getObservation_image());

        holder.txt_observation_id.setText(String.valueOf(adapterData.get(position).getObservation_Id()));
        holder.txt_observation_name.setText(String.valueOf(adapterData.get(position).getObservation_name()));
        holder.txt_time_of_observation.setText(dateFormat.format(adapterData.get(position).getTime_of_observation()));
        holder.txt_observation_comment.setText(String.valueOf(adapterData.get(position).getObservation_comment()));


        holder.observationItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditObservationActivity.class);
                intent.putExtra("function", "UPDATE");
                intent.putExtra("observationId", String.valueOf(adapterData.get(position).getObservation_Id()));
                intent.putExtra("observationName", adapterData.get(position).getObservation_name());
                intent.putExtra("observationImage", imageByteArray);
                intent.putExtra("timeOfObservation", dateFormat.format(adapterData.get(position).getTime_of_observation()));
                intent.putExtra("observationComment", adapterData.get(position).getObservation_comment());
                context.startActivity(intent);
            }
        });
        holder.observationItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String observationName = adapterData.get(position).getObservation_name();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Remove " + observationName + "?");
                builder.setMessage("Are you sure to remove " + observationName + "?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ConnectDatabase connectDatabase = new ConnectDatabase(context);
                        long result = connectDatabase.deleteOneObservation(adapterData.get(position).getObservation_Id() + "");
                        if (result == -1) {
                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(context, ObservationActivity.class);
                            Toast.makeText(context, "Remove successfully", Toast.LENGTH_SHORT).show();
                            context.startActivity(intent);
                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create().show();
                return true;
            }
        });

        holder.btn_share_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionListener permissionlistener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (isFacebookLiteInstalled()) {
                            Uri imageUri = saveBitmap(imageSrc);
                            Log.i("Data", imageUri.toString());
                            if (imageUri != null) {
                                shareImageToFacebook(imageUri);
                            }
                        } else {
                            Toast.makeText(context, "Please install Facebook and login before sharing", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Toast.makeText(context, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                    }
                };
                TedPermission.create()
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();
            }
        });

    }

    @Override
    public int getItemCount() {
        return adapterData.size();
    }

    private void shareImageToFacebook(Uri imageUri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        intent.putExtra(Intent.EXTRA_TEXT, "Something interesting during my hike");
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        boolean facebookInstalled = false;
        for (ResolveInfo resolveInfo : resolveInfos) {
            if (resolveInfo.activityInfo.packageName.contains("com.facebook.katana")) {
                facebookInstalled = true;
                break;
            }
        }

        if (facebookInstalled) {
            intent.setPackage("com.facebook.katana");
        }
        Intent chooser = Intent.createChooser(intent, "Share Image");
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(chooser);
        } else {
            Toast.makeText(context, "No app to handle this action", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareImageViaBluetooth(Uri imageUri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);

        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        boolean bluetoothSupported = false;
        for (ResolveInfo resolveInfo : resolveInfos) {
            if (resolveInfo.activityInfo.packageName.contains("com.android.bluetooth")) {
                bluetoothSupported = true;
                break;
            }
        }

        if (bluetoothSupported) {
            intent.setPackage("com.android.bluetooth");
        } else {
            Toast.makeText(context, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent chooser = Intent.createChooser(intent, "Share Image");
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(chooser);
        } else {
            Toast.makeText(context, "No app to handle this action", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri saveBitmap(Bitmap bitmap) {
        String directoryName = "MyObservation";
        String newDirectoryPath = createNewDirectory(directoryName);
        File file = null;
        try (FileOutputStream stream = new FileOutputStream(new File(newDirectoryPath, "image.png"))) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            file = new File(newDirectoryPath, "image.png");
            return FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        } catch (IOException e) {
            e.printStackTrace();
            if (file != null) {
                file.delete();
            }
            return null;
        }
    }

    private boolean isFacebookLiteInstalled() {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo("com.facebook.katana", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_observation_id, txt_observation_name, txt_time_of_observation, txt_observation_comment;
        ImageView img_observation;
        Button btn_share_facebook;
        LinearLayout observationItemLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_observation_id = itemView.findViewById(R.id.textview_observation_id);
            txt_observation_name = itemView.findViewById(R.id.textview_observation_name);
            txt_time_of_observation = itemView.findViewById(R.id.textview_time_of_observation);
            txt_observation_comment = itemView.findViewById(R.id.textview_observation_comment);
            img_observation = itemView.findViewById(R.id.image_observation);
            btn_share_facebook = itemView.findViewById(R.id.btn_share_facebook);
            observationItemLayout = itemView.findViewById(R.id.observationItemLayout);
        }
    }
}
