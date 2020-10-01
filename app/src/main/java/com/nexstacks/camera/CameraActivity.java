package com.nexstacks.camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraActivity extends AppCompatActivity {

    private FrameLayout cameraFrame;
    private Camera camera;
    private boolean isCameraFacingBack;
    private ImageView mIvDisplayImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        cameraFrame = findViewById(R.id.camera_frame);

        ImageView mIvFlipCamera = findViewById(R.id.iv_flip_camera);
        mIvFlipCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.stopPreview();
                initiateCamera(!isCameraFacingBack);
            }
        });

        ImageView mIvCaptureImage = findViewById(R.id.iv_capture_image);
        mIvDisplayImage = findViewById(R.id.iv_display_img);

        mIvCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
                        mIvDisplayImage.setImageBitmap(image);
                        camera.startPreview();
                    }
                });
            }
        });

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 904);
        }else{
            initiateCamera(true);
        }

    }

    private void initiateCamera(boolean isBackCamera){
        int cameraID = isBackCamera ? Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT;

        isCameraFacingBack = isBackCamera;

        camera = Camera.open(cameraID);
        CameraSurfaceView surfaceView = new CameraSurfaceView(CameraActivity.this, camera);
        cameraFrame.addView(surfaceView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 904){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED){
                initiateCamera(true);
            }else{
                Toast.makeText(CameraActivity.this, "User Denied Permission", Toast.LENGTH_LONG).show();
            }
        }
    }
}