package com.example.rhythmtranslator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;

public class MainActivity extends AppCompatActivity {

    // UI components
    public EditText userTextInput;
    public TextView notesView;
    public ImageView imageView;
    public Button convertButton;
    public ImageButton playButton;
    public ImageButton pauseButton;
    public ImageButton stopButton;
    public ImageButton loopButton;
    public LinkedHashMap<Character, String> noteDictionary = new LinkedHashMap<>();
    public LinkedHashMap<Character, Integer> noteImages = new LinkedHashMap<>();

    public MediaPlayer notePlayer = new MediaPlayer();
    public MediaPlayer metronome = new MediaPlayer(); // Use audiotrack

    public String notes;
    public boolean isPlaying = false;


    @SuppressLint({"MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Init UI components on creation
        userTextInput = findViewById(R.id.user_text_input);
        notesView = findViewById(R.id.notes_view);
        convertButton = findViewById(R.id.convert_button);
        playButton = findViewById(R.id.play_button);
        pauseButton = findViewById(R.id.pause_button);
        stopButton = findViewById(R.id.stop_button);
        loopButton = findViewById(R.id.loop_button);

        //read dictionary.txt and put into linked hashmap
        getNoteToChar(noteDictionary);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioPlayer(notes);
                setMetronome();
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notePlayer.pause();
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notePlayer.stop();
            }
        });

        loopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notePlayer.setLooping(true);
            }
        });

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user text
                String userText = userTextInput.getText().toString();
                notes = translateToNotes(userText); // Returns string, e. g. 0100
                // Display the translated letters for now
                notesView.setText(notes);
                displayNoteImages(userText);
                audioPlayer(notes);
                setMetronome();
            }
        });
    }


    @NonNull
    public String translateToNotes(String userText) {
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

    // Not necessary at all
    // Wanted to separate the data from the logic
    public void getImageToNote() {
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("letterToNoteImages.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Separate using "-"
                String[] parts = line.split("-");

                char letter = parts[0].charAt(0);
                String resourceString = parts[1];
                // Android Studio doesn't like that
                int resourceIdNoteImages = getResources().getIdentifier(resourceString, "drawable", getPackageName());
                noteImages.put(letter, resourceIdNoteImages);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getNoteToChar(LinkedHashMap<Character, String> map) {
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("dictionary.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Separate using "-"
                String[] parts = line.split("-");

                char letter = parts[0].charAt(0);
                String note = parts[1];
                // Add to hashmap
                map.put(letter, note);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void displayNoteImages(String userText) {
        LinearLayout noteImagesContainer = findViewById(R.id.note_images_container);
        noteImagesContainer.removeAllViews();

        userText = userText.toLowerCase();
        for(int i = 0; i < userText.length(); i++){
            char letter = userText.charAt(i);
            if(noteImages.containsKey(letter)){
                int noteImage = noteImages.get(letter);
                imageView = new ImageView(this);
                imageView.setImageResource(noteImage);
                noteImagesContainer.addView(imageView);
            }
        }
    }



    public void audioPlayer(String notes) {  // Create class
        //notePlayer = MediaPlayer.create(this, R.raw.snare);
        try {
            AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.snare);
            notePlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            notePlayer.prepare();

            final long delay = 250; // Delay for the 16th rest (250ms)
            final long noteDuration = 250; // Duration for the notes (250ms)

            final Handler handler = new Handler();
            final Runnable playNoteRunnable = new Runnable() {
                @Override
                public void run() {
                    notePlayer.start();
                    isPlaying = true;
                }
            };

            for (int i = 0; i < notes.length(); i++) {
                char c = notes.charAt(i);
                String singleNote = String.valueOf(c);
                if (singleNote.equals("1")) {
                    handler.postDelayed(playNoteRunnable, i * (delay + noteDuration));
                }
                //notePlayer.reset();
                //notePlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to set note source", e);
        }
    }


    public void setMetronome() {
        try {
            AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.click);
            metronome.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            metronome.prepare();
            // If note player starts playing or if playButton is pressed start playing metronome
            if (isPlaying) {
                metronome.start();
            }
            //metronome.reset();
            //metronome.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}