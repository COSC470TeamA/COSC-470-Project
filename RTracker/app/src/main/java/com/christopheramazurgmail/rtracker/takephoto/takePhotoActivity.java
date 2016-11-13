package com.christopheramazurgmail.rtracker.takephoto;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.christopheramazurgmail.rtracker.R;
import com.christopheramazurgmail.rtracker.tesseract.OCRActivity;


public class TakePhotoActivity extends Activity {

    Button takePicture;
    ImageButton cropPicture;
    int CAMERA_PIC_REQUEST = 1;
    int NEW_PICTURE_TRUE = 1;
    Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        takePicture = (Button) findViewById(R.id.picture);
        cropPicture = (ImageButton) findViewById(R.id.crop_image);

        final Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
        }

        if (NEW_PICTURE_TRUE == -1){
            Intent intent = new Intent(getApplicationContext(), OCRActivity.class);
            startActivity(intent);
        }

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
                }
            }
        });
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == CAMERA_PIC_REQUEST) {
                imageUri = data.getData();
                Intent OCRIntent = new Intent(this, OCRActivity.class);
                OCRIntent.putExtra("ImageURI", imageUri);
                startActivity(OCRIntent);
                }
            else {
                NEW_PICTURE_TRUE = 0;

            }

        }
    }

}
