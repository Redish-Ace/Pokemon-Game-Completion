package com.example.pokecompletion;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class GameAdapter extends ArrayAdapter<GameData> {
    JSONArray jsonArray;
    ArrayList<CompletedTask> completedTaskList = new ArrayList<>();
    JSONObject jsonObject;
    String filename = "completed_tasks.json";

    public GameAdapter(@NonNull Context context, @NonNull List<GameData> objects) {
        super(context, R.layout.game_item, R.id.gameName, objects);
        readTaskJSON();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NotNull ViewGroup parent){
        GameData gameData = getItem(position);

        View view = convertView;
        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.game_item, parent, false);
        }

        TextView gameName = view.findViewById(R.id.gameName);
        Spinner gameTasks = view.findViewById(R.id.taskSpinner);

        assert gameData != null;
        gameName.setText(gameData.name);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, gameData.tasks);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gameTasks.setAdapter(adapter);

        CompletedTask completedTask = completedTaskList.get(position);
        int taskIndex = gameData.tasks.indexOf(completedTask.task);
        gameTasks.setSelection(taskIndex);

        gameTasks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //TextView gameName = view.findViewById(R.id.gameName);
                //writeTaskJSON(gameName.getText().toString(), gameTasks.);
                writeTaskJSON(gameData.name, gameTasks.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return view;
    }

    private void readTaskJSON(){
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream is = this.getContext().openFileInput(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line = br.readLine()) != null){
                stringBuilder.append(line).append("\n");
            }
            br.close();
            is.close();
        } catch (Exception e) {
            Log.d("Reading JSON Error", Objects.requireNonNull(e.getMessage()));
            createJSON();
            readTaskJSON();
        }

        try{
            jsonObject = new JSONObject(stringBuilder.toString());
            Iterator<String> gamesIterator = jsonObject.keys();
            while(gamesIterator.hasNext()){
                String game = gamesIterator.next();
                String task = jsonObject.getString(game);
                CompletedTask completedTask = new CompletedTask(game, task);
                completedTaskList.add(completedTask);
            }
            Log.d("Task JSON", completedTaskList.toString());
        } catch (JSONException e){
            Log.d("Getting Array Error", Objects.requireNonNull(e.getMessage()));
        }
    }

    private void writeTaskJSON(String game, String task){
        try {
            jsonObject.put(game, task);
            FileOutputStream fos = this.getContext().openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(jsonObject.toString().getBytes());
            fos.close();
            Log.d("Update JSON", "Successful");
        } catch (Exception e){
            Log.d("Update JSON Error", Objects.requireNonNull(e.getMessage()));
        }
    }

    private void createJSON(){
        InputStream is = this.getContext().getResources().openRawResource(R.raw.tasks_completed);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            FileOutputStream fos = this.getContext().openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(stringBuilder.toString().getBytes());
            fos.close();
        }
        catch (Exception e){
            Log.d("Create JSON Error", Objects.requireNonNull(e.getMessage()));
        }
    }
}
