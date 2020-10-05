package com.nexstacks.camera;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.FileInputStream;

public class ViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        ImageView mIvDisplay = findViewById(R.id.imageView2);



        String imgFile = getIntent().getExtras().getString("image");
        try{
            FileInputStream inputStream = this.openFileInput(imgFile);
            Bitmap image = BitmapFactory.decodeStream(inputStream);
            mIvDisplay.setImageBitmap(image);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}