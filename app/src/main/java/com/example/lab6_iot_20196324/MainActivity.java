package com.example.lab6_iot_20196324;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //buton memory
        Button ingresar = findViewById(R.id.buttonMemory);
        ingresar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Memory.class);
            startActivity(intent);
        });


        //buton puzzle
        Button ingresar2 = findViewById(R.id.buttonPuzzle);
        ingresar2.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Puzzle.class);
            startActivity(intent);
        });




    }
}