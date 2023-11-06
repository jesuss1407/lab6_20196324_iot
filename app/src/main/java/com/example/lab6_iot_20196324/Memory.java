package com.example.lab6_iot_20196324;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class Memory extends AppCompatActivity {

    private static final int IMAGE_CHOOSE = 10001;
    private ArrayList<Uri> imageUris = new ArrayList<>();
    private LinearLayout imageContainer;
    private TextView imageCountView;
    private Button addImagesButton, btnBack, btnHelp, btnRandom;

    private HorizontalScrollView scrollView;


    private void chooseImage() {
        Intent chooseImageIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(chooseImageIntent, IMAGE_CHOOSE);
    }


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);


        imageContainer = findViewById(R.id.imageContainer);
        imageCountView = findViewById(R.id.tvImageCount);
        btnHelp = findViewById(R.id.btnHelp);
        addImagesButton = findViewById(R.id.btnSelectImage);
        scrollView = findViewById(R.id.scrollView);


        addImagesButton.setOnClickListener(v -> {
            if (imageUris.size() < 15) {
                chooseImage();
            } else {

            }
        });

        btnBack = findViewById(R.id.btnVolver);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(Memory.this, MainActivity.class);
            startActivity(intent);
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CHOOSE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            if (selectedImage != null && !imageUris.contains(selectedImage)) {
                imageUris.add(selectedImage);
                addImageView(selectedImage);
            }
        }
    }

    private void addImageView(Uri imageUri) {
        ImageView imageView = new ImageView(this);
        imageView.setImageURI(imageUri);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setPadding(10, 10, 10, 10);

        imageContainer.addView(imageView);
        updateImageCount();
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_RIGHT));
    }

    private void updateImageCount() {
        imageCountView.setText(String.format("Total de imagenes seleccionadas: %d", imageUris.size()));
    }


}