package com.christopheramazurgmail.rtracker.takephoto;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import com.christopheramazurgmail.rtracker.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Chris Mazur on 11/9/2016.
 */

public class TakePhotoActivity extends Activity {

    File imageFile;
    Button takePicture;
    ImageButton cropPicture;
    int CAMERA_PIC_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        takePicture = (Button) findViewById(R.id.picture);
        cropPicture = (ImageButton) findViewById(R.id.crop_image);

        takePicture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            if (requestCode == CAMERA_PIC_REQUEST) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                ByteBuffer buffer = ByteBuffer.allocate(image.getRowBytes() * image.getHeight());
                byte[] imageBytes = new byte[buffer.remaining()];
                imageFile = new File(this.getExternalFilesDir(null), "pic.jpg");
                image.copyPixelsToBuffer(buffer);
                try {
                    buffer.get(imageBytes, 0, imageBytes.length);
                    FileOutputStream output = new FileOutputStream(imageFile);
                    output.write(imageBytes);
                } catch (IOException ioe) {
                    ioe.getStackTrace();
                }
            }
    }

}
