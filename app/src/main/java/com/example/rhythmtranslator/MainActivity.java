package com.example.rhythmtranslator;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.lang.System;

public class MainActivity extends AppCompatActivity {

    // UI components
    private EditText userTextInput;
    private TextView notesView;
    private Button convertButton;
    private ImageButton playButton;
    private LinkedHashMap<Character, String> noteDictionary;

    private MediaPlayer notePlayer = new MediaPlayer();



    @SuppressLint({"MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Init UI components on creation
        noteDictionary = new LinkedHashMap<>();
        userTextInput = findViewById(R.id.user_text_input);
        notesView = findViewById(R.id.notes_view);
        convertButton = findViewById(R.id.convert_button);
        playButton = findViewById(R.id.play_button);

        //read dictionary.txt and put into linked hashmap
        getNoteToChar(noteDictionary);

        /* When convert button is clicked:
        * 1. get user text
        * 2. convert characters to notes
        * 3. build note string
        * 4. show notes in text view*/

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user text
                String userText = userTextInput.getText().toString();
                String notes = translateToNotes(userText); // Returns string, e. g. 0100
                // Display the translated letters for now
                notesView.setText(notes);
                audioPlayer(notes);
            }
        });
      /*  convertButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userText = userTextInput.getText().toString();
                String notes = translateToNotes(userText); // Returns String
                convertButton.setOnClickListener((new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        convertButton.setVisibility(View.INVISIBLE);
                        notesView.setText(notes);
                        // Add metronome with tempo 60
                        audioPlayer(notes);
                    }
                }));
                convertButton.setVisibility(View.INVISIBLE);
                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            audioPlayer(notes);

                    }
                });
                userTextInput.setOnClickListener((new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        convertButton.setVisibility(View.VISIBLE);
                    }
                }));
            }

        }));

        */

    }



    private String translateToNotes(String userText){
        StringBuilder noteText = new StringBuilder();
        userText = userText.toLowerCase();
        for (int i = 0; i < userText.length(); i++) {
            char c = userText.charAt(i);
            //Potentially change to function parameter
            if (Character.isLetter(c) || Character.isWhitespace(c)) {
                if (noteDictionary.containsKey(c)) {
                    String note = noteDictionary.get(c);
                        noteText.append(note);
                }
            } else {
                noteText.append(c); // Append non-letter and non-whitespace characters as-is
            }
        }
        String notes = noteText.toString().trim();
        // Clean string builder at the end
        noteText.setLength(0);
        return notes;
    }

    private void getNoteToChar(LinkedHashMap<Character, String> map){
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("dictionary.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";
            while((line = reader.readLine())!=null){
                // Separate using "-"
                String [] parts = line.split("-");

                char letter = parts[0].charAt(0);
                String note = parts[1];
                // Add to hashmap
                map.put(letter, note);

            }
            reader.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void audioPlayer(String notes) {
        try {
            AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.click);
            notePlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            notePlayer.prepare();
            for (int i = 0; i < notes.length(); i++) {
                char c = notes.charAt(i);
                String singleNote = String.valueOf(c);
                if (singleNote.equals("1")) {
                    notePlayer.start();
                } else {
                    long millis = System.currentTimeMillis();

                    // Or set the time in a TextView for debugging purposes
                    // textView.setText("Current Time: " + millis);
                    Thread.sleep(250);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to set data source", e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }





}