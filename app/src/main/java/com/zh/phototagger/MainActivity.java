package com.zh.phototagger;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    static final int REQUEST_IMAGE_CAPTURE = 1;

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);
        //dispatchTakePictureIntent();
    }

    public void save(View v) {
        EditText mTagView = (EditText) findViewById(R.id.tags);
        ImageView mImageView = (ImageView) findViewById(R.id.imageView);
        //make sure it has changed
        if (mTagView.getText().length() > 0) {

            String tags = mTagView.getText().toString();
            for (String s : tags.split(";")) {

            }
            //save picture
            BitmapDrawable d = (BitmapDrawable) mImageView.getDrawable();
            Bitmap img = d.getBitmap();
            savePictureToSD(img);


        } else {
            Toast.makeText(this, "Please input tags separated by ;", Toast.LENGTH_LONG).show();
        }
    }

    public void load(View v) {
        EditText mTagView = (EditText) findViewById(R.id.tags);
        TextView mSizeView = (TextView) findViewById(R.id.size);
    }

    private void savePictureToSD(Bitmap bmp) {
        File sdcard = Environment.getExternalStorageDirectory();
        File f = new File (sdcard, "comp433_" + new Date().toString() + ".png");
        FileOutputStream out;
        try {
            out = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dispatchTakePictureIntent(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
        ImageView mImageView = (ImageView) findViewById(R.id.imageView);
        TextView mSizeView = (TextView) findViewById(R.id.size);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            int size = imageBitmap.getByteCount();
            mImageView.setImageBitmap(imageBitmap);
            mSizeView.setText(Integer.toString(size));
        }
    }

}
