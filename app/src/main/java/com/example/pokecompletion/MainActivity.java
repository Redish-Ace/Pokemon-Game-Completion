package com.example.pokecompletion;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_WRITE_STORAGE = 100;
    private static final int REQUEST_CODE = 100;
    GameAdapter gameAdapter;
    JSONArray jsonArray;
    List<GameData> gameDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        readJSON(this);

        AppCompatButton btn = findViewById(R.id.csvButton);
        btn.setOnClickListener(v -> {
            writeCSV();
        });
    }

    private void readJSON(Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.pokemon_game_tasks);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))){
            String line;
            while((line = br.readLine()) != null){
                stringBuilder.append(line).append("\n");
            }
        } catch (Exception e) {
            Log.d("Error", Objects.requireNonNull(e.getMessage()));
        }

        try{
            JSONObject obj = new JSONObject(stringBuilder.toString());
            jsonArray = obj.getJSONArray("metadata");
            createGameItems();
        } catch (JSONException e){
            Log.d("Error", Objects.requireNonNull(e.getMessage()));
        }
    }

    private void createGameItems(){
        if(jsonArray == null) return;

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                JSONArray games = item.getJSONArray("games");
                JSONArray tasks = item.getJSONArray("tasks");
                ArrayList<String> tasksList = getArrayListFromJSONArray(tasks);
                for (int j=0; j<games.length(); j++){
                    GameData game = new GameData(games.getString(j), tasksList);
                    gameDataList.add(game);
                }
            }
            Log.d("Game Data List", gameDataList.toString());

            gameAdapter = new GameAdapter(this, gameDataList);
            ListView gameView = findViewById(R.id.gameView);
            gameView.setAdapter(gameAdapter);
        } catch (JSONException e){
            Log.d("JSON Error", Objects.requireNonNull(e.getMessage()));
        }
    }

    private ArrayList<String> getArrayListFromJSONArray(JSONArray jsonArr){
        ArrayList<String> arrayList = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArr.length(); i++) {
                arrayList.add(jsonArr.getString(i));
            }
        } catch(Exception e){
            Log.d("JSONArray Error", Objects.requireNonNull(e.getMessage()));
        }

        return arrayList;
    }

    private void writeCSV() {
        ContentResolver resolver = getContentResolver();
        Uri collectionUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

        // Delete existing file first
        String selection = MediaStore.MediaColumns.DISPLAY_NAME + "=? AND " +
                MediaStore.MediaColumns.RELATIVE_PATH + "=?";
        String[] selectionArgs = new String[]{"completed_tasks.csv", Environment.DIRECTORY_DOCUMENTS + "/"};

        int deleted = resolver.delete(collectionUri, selection, selectionArgs);
        if (deleted > 0) {
            Log.d("CSV Write", "Deleted existing file: " + deleted + " record(s)");
        }

        // Now create new file
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, "completed_tasks.csv");
        values.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);

        Uri uri = resolver.insert(collectionUri, values);

        if (uri != null) {
            try (OutputStream outputStream = resolver.openOutputStream(uri);
                 OutputStreamWriter writer = new OutputStreamWriter(outputStream)) {

                ArrayList<CompletedTask> completedTaskList = CompletedJSON.getCompletedTasks(this.getBaseContext());

                writer.write("Game,Task\n");

                for (int i = 0; i < completedTaskList.size(); i++) {
                    CompletedTask task = completedTaskList.get(i);
                    String line = String.format("%s,%s\n", task.game, task.task);
                    writer.write(line);
                }

                Toast.makeText(this.getBaseContext(), "Saved into completed_tasks.csv in Documents folder", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                Log.d("CSV Write Error", Objects.requireNonNull(e.getMessage()));
                Toast.makeText(this.getBaseContext(), "Failed to save CSV file", Toast.LENGTH_SHORT).show();
            }
        }
    }
}