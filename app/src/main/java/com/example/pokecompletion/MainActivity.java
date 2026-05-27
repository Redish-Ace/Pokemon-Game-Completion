package com.example.pokecompletion;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
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
}