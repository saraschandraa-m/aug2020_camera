package com.nexstacks.camera;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button cameraButton = findViewById(R.id.button);
        imageView = findViewById(R.id.imageView);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            if(data.getExtras() != null){
                Bitmap capturedImage = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(capturedImage);
            }else{
                try {
                    Bitmap capturedImage2 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    imageView.setImageBitmap(capturedImage2);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}