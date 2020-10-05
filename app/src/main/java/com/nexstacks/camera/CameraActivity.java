package com.nexstacks.camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class CameraActivity extends AppCompatActivity {

    private FrameLayout cameraFrame;
    private Camera camera;
    private boolean isCameraFacingBack;
    private ImageView mIvDisplayImage;

    private Bitmap cameraImage;

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
//                        cameraImage = image;
                        storeImageToDevice(image);
//                        mIvDisplayImage.setImageBitmap(image);
//                        camera.startPreview();
                    }
                });
            }
        });

        mIvDisplayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = "temp_img.png";

                try {
                    FileOutputStream stream = CameraActivity.this.openFileOutput(fileName, MODE_PRIVATE);
                    cameraImage.compress(Bitmap.CompressFormat.PNG, 90, stream);

                    stream.close();

                    Intent viewIntent = new Intent(CameraActivity.this, ViewActivity.class);
                    viewIntent.putExtra("image", fileName);
                    startActivity(viewIntent);
                }catch(Exception e){
                    e.printStackTrace();
                }
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

    private void storeImageToDevice(Bitmap capturedImage){
        Matrix imageMatrix = new Matrix();
        imageMatrix.postRotate(90);

        Bitmap resizedImage = Bitmap.createBitmap(capturedImage, 0, 0, capturedImage.getWidth(), capturedImage.getHeight(), imageMatrix, false);


        cameraImage = resizedImage;
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath(), "MyCameraApp");

        if(!directory.exists()){
            directory.mkdir();
        }

        File imageName = new File(directory, "IMG_"+System.currentTimeMillis()+".png");
        try {
            FileOutputStream fileOutputStream =new FileOutputStream(imageName);
            resizedImage.compress(Bitmap.CompressFormat.PNG, 80, fileOutputStream);
            fileOutputStream.close();


            mIvDisplayImage.setImageBitmap(resizedImage);
            camera.startPreview();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void readImageFromDevice(){


        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] proj = new String[]{MediaStore.Images.Media.DATA};
        ArrayList<String> imagePaths = new ArrayList<>();

        Cursor cursor = getApplicationContext().getContentResolver().query(imageUri, proj, null, null, null);;
        if(cursor != null){
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                String image = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                imagePaths.add(image);
            }
        }

    }
}