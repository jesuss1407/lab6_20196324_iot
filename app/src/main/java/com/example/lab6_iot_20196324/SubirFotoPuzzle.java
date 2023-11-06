package com.example.lab6_iot_20196324;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SubirFotoPuzzle extends AppCompatActivity {


    private static final int SELECT_IMAGE_REQUEST_CODE = 1001;
    private static final int IMAGE_CHOOSE = 1001;

    ImageView ivSelectedImage;
    Button btnSelectImage, btnJugar;


    Uri selectedImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_foto);

        btnJugar = findViewById(R.id.btnGuardarPublicacion);
        ivSelectedImage = findViewById(R.id.ivSelectedImage);
        btnSelectImage = findViewById(R.id.btnSelectImage);

        // Configurar botón para seleccionar imagen
        btnSelectImage.setOnClickListener(v -> {
            chooseImage();
        });



        // Ir a jugar
        btnJugar.setOnClickListener(v -> {
            Intent intent = new Intent(SubirFotoPuzzle.this, Puzzle.class);
            String uriString = selectedImageUri.toString();
            intent.putExtra("fotito", uriString);
            startActivity(intent);
        });



    }

    private void chooseImage() {
        Intent chooseImageIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(chooseImageIntent, IMAGE_CHOOSE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_CHOOSE && resultCode == RESULT_OK) {
            // La imagen se seleccionó exitosamente
            if (data != null) {
                selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    ivSelectedImage.setImageURI(selectedImageUri);
                    // También puedes ajustar la escala y el tamaño de la imagen si es necesario
                }
            }
        }
    }

}