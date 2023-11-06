package com.example.lab6_iot_20196324;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.content.Intent;

import android.widget.GridView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Puzzle extends AppCompatActivity {

    private static final int IMAGE_CHOOSE = 10001;
    Uri selectedImageUri;
    private GridView gridView;
    private Button btnStartGame, btnNewImage, btnBack;
    private int gridSize = 3;
    private int emptySpaceIndex=-1;
    private boolean gameStarted = false;
    private int pieceWidth, pieceHeight;
    private ArrayList<Bitmap> puzzlePieces = new ArrayList<>();
    private ArrayList<Bitmap> imagePieces = new ArrayList<>();


    private void chooseImage() {
        Intent chooseImageIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(chooseImageIntent, IMAGE_CHOOSE);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);


        // Activity de destino
        /*Intent intent = getIntent();
        if (intent.hasExtra("fotito")) {
            String uriString = intent.getStringExtra("fotito");
            selectedImageUri = Uri.parse(uriString);
        }
        if (selectedImageUri != null) {
            // Cargar la imagen y las piezas del rompecabezas
            try {
                puzzlePieces.clear();
                if (imagePieces != null) {
                    imagePieces.clear();
                }

                InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap selectedBitmap = BitmapFactory.decodeStream(imageStream);
                createPuzzlePieces(selectedBitmap);

                // Reiniciamos el estado del juego
                gameStarted = false;
                btnStartGame.setText("Start Game");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        */
        gridView = findViewById(R.id.gridViewPuzzle);
        btnStartGame = findViewById(R.id.btnReset);
        btnStartGame.setEnabled(false);
        btnNewImage = findViewById(R.id.btnSelectImage);
        btnNewImage.setOnClickListener(v -> {
            chooseImage();
        });

        btnBack = findViewById(R.id.btnVolver);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(Puzzle.this, MainActivity.class);
            startActivity(intent);
        });

        gridView.setNumColumns(gridSize);
        puzzlePieces = new ArrayList<>();
        imagePieces = new ArrayList<>();


        if (!imagePieces.isEmpty()) {
            loadSavedGame();
        }

        btnStartGame.setOnClickListener(v -> {
            if (!gameStarted) {
                gameStarted = true;
                shufflePuzzle();
                btnStartGame.setText("Reiniciar juego");
            } else {
                resetGame();
            }
        });

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            if (isValidSpace(position)) {
                Collections.swap(puzzlePieces, position, emptySpaceIndex);
                emptySpaceIndex = position;
                ((BaseAdapter) gridView.getAdapter()).notifyDataSetChanged();
                for (int i = 0; i < puzzlePieces.size() - 1; i++) {
                    if (puzzlePieces.get(i) != imagePieces.get(i)) {
                        return;
                    }
                }
                // Se armaron las piezas correctamente
                Toast.makeText(this, "Se culminó el juego", Toast.LENGTH_SHORT).show();
            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CHOOSE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            puzzlePieces.clear();
            imagePieces.clear();

            try {
                Bitmap selectedBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
                createPuzzlePieces(selectedBitmap);

                gameStarted = false;
                // Habilitar el botón después de seleccionar una imagen
                btnStartGame.setEnabled(true);
                btnStartGame.setText("Comenzar juego");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    private void createPuzzlePieces(Bitmap image) {
        int numPieces = gridSize * gridSize;
        int pieceWidth = image.getWidth() / gridSize;
        int pieceHeight = image.getHeight() / gridSize;

        puzzlePieces.clear();
        imagePieces.clear();

        int yCoordinate = 0;
        for (int row = 0; row < gridSize; row++) {
            int xCoordinate = 0;
            for (int col = 0; col < gridSize; col++) {
                Bitmap pieceBitmap = Bitmap.createBitmap(image, xCoordinate, yCoordinate, pieceWidth, pieceHeight);
                puzzlePieces.add(pieceBitmap);
                xCoordinate += pieceWidth;
            }
            yCoordinate += pieceHeight;
        }

        Bitmap lastPiece = puzzlePieces.remove(numPieces - 1);
        puzzlePieces.add(null);

        imagePieces.addAll(puzzlePieces);
        imagePieces.add(lastPiece);

        gridView.setAdapter(new AdapterPuzzle(this, puzzlePieces, pieceWidth, pieceHeight));
    }




    private void shufflePuzzle() {
        if (puzzlePieces.isEmpty()) return;

        Random random = new Random();
        emptySpaceIndex = puzzlePieces.size() - 1;

        int shuffleSteps = gridSize * gridSize * 2;

        for (int step = 0; step < shuffleSteps; step++) {
            List<Integer> validMoves = getValidMoves();

            int toSwapWithIndex = validMoves.get(random.nextInt(validMoves.size()));
            Collections.swap(puzzlePieces, emptySpaceIndex, toSwapWithIndex);
            emptySpaceIndex = toSwapWithIndex;
        }

        gridView.invalidateViews();
        saveGameState();
    }

    private List<Integer> getValidMoves() {
        List<Integer> validMoves = new ArrayList<>();

        if (emptySpaceIndex - gridSize >= 0) validMoves.add(emptySpaceIndex - gridSize);
        if (emptySpaceIndex + gridSize < puzzlePieces.size()) validMoves.add(emptySpaceIndex + gridSize);
        if (emptySpaceIndex % gridSize > 0) validMoves.add(emptySpaceIndex - 1);
        if (emptySpaceIndex % gridSize < gridSize - 1) validMoves.add(emptySpaceIndex + 1);

        return validMoves;
    }

    private void resetGame() {
        gridSize = 3;
        gridView.setNumColumns(gridSize);

        puzzlePieces.clear();
        puzzlePieces.addAll(imagePieces.subList(0, gridSize * gridSize));
        emptySpaceIndex = gridSize * gridSize - 1;
        puzzlePieces.set(emptySpaceIndex, null);

        gridView.invalidateViews(); // Actualiza el GridView para mostrar las piezas restablecidas

        gameStarted = false;
        btnStartGame.setText("Comenzar juego");
        saveGameState();
    }



    private boolean isValidSpace(int clickedPosition) {
        int emptyRow = emptySpaceIndex / gridSize;
        int emptyCol = emptySpaceIndex % gridSize;

        int clickedRow = clickedPosition / gridSize;
        int clickedCol = clickedPosition % gridSize;

        int rowDiff = Math.abs(emptyRow - clickedRow);
        int colDiff = Math.abs(emptyCol - clickedCol);

        return (rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1);
    }



    private void saveGameState() {
        if (puzzlePieces.isEmpty() || imagePieces.isEmpty()) return;

        // Guardar el estado actual del rompecabezas
        SharedPreferences prefs = getSharedPreferences("PuzzleState", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Crear una cadena con los índices de las piezas
        StringBuilder sb = new StringBuilder();
        for (Bitmap piece : puzzlePieces) {
            int originalIndex = imagePieces.indexOf(piece);
            sb.append(originalIndex).append(",");
        }

        // Guardar la cadena y el índice del espacio vacío
        editor.putString("puzzleState", sb.toString());
        editor.putInt("emptySpaceIndex", emptySpaceIndex);
        editor.apply();
    }

    private void loadSavedGame() {
        // Cargar el estado previamente guardado del rompecabezas
        SharedPreferences prefs = getSharedPreferences("PuzzleState", MODE_PRIVATE);
        String savedState = prefs.getString("puzzleState", null);
        emptySpaceIndex = prefs.getInt("emptySpaceIndex", -1);

        if (savedState != null && emptySpaceIndex != -1 && !imagePieces.isEmpty()) {
            // Recuperar los índices de las piezas y reconstruir el estado
            String[] pieceIndexes = savedState.split(",");
            puzzlePieces.clear();
            for (String index : pieceIndexes) {
                if (!index.isEmpty()) {
                    int originalIndex = Integer.parseInt(index);
                    puzzlePieces.add(imagePieces.get(originalIndex));
                }
            }
            puzzlePieces.add(null); // Agregar el espacio vacío

            // Actualizar la vista del GridView
            gridView.setAdapter(new AdapterPuzzle(this, puzzlePieces, pieceWidth, pieceHeight));
        }
    }

}