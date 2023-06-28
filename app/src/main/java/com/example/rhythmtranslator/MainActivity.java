package com.example.rhythmtranslator;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //UI components
    private EditText userTextInput;
    private TextView notesView;
    private Button convertButton;
    private LinkedHashMap<Character, String> noteDictionary;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "Hello World!", Toast.LENGTH_SHORT).show();

        //init UI components on creation
        userTextInput = findViewById(R.id.user_text_input);
        notesView = findViewById(R.id.notes_view);
        convertButton = findViewById(R.id.convert_button);

        getNoteToChar(noteDictionary);

        /* When convert button is clicked:
        * 1. get user text
        * 2. convert characters to notes
        * 3. build note string
        * 4. show notes in text view*/
        convertButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userText = userTextInput.getText().toString();
                //translateToNotes(userText); -returns String

            }
        }));

    }



    private String translateToNotes(String userText){
        StringBuilder noteText = new StringBuilder();
        userText.toLowerCase();
        for (int i = 0; i < userText.length(); i++) {
            char c = userText.charAt(i);
            /*if map contains key condition
            * String note = map.get(c)  - get note value
            * noteText.append(note);*/
        }
        return noteText.toString().trim();
    }

    private void getNoteToChar(LinkedHashMap<Character, String> map){
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("dictionary.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";
            while((line = reader.readLine())!=null){
                //separate using "-"
                String [] parts = line.split("-");

                char letter = parts[0].charAt(0);
                String note = parts[1];
                //add to hashmap
                map.put(letter, note);

            }
            reader.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }





}