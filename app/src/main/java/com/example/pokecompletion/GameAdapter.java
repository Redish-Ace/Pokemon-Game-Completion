package com.example.pokecompletion;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class GameAdapter extends ArrayAdapter<GameData> {
    ArrayList<CompletedTask> completedTaskList = new ArrayList<>();
    JSONObject jsonObject;
    String filename = "completed_tasks.json";

    int[][] themes = { {R.color.red_card, R.color.red_text, R.color.red_spinner}, {R.color.blue_card, R.color.blue_text, R.color.blue_spinner}, {R.color.yellow_card, R.color.yellow_text, R.color.yellow_spinner},
            {R.color.gold_card, R.color.gold_text, R.color.gold_spinner}, {R.color.silver_card, R.color.silver_text, R.color.silver_spinner}, {R.color.crystal_card, R.color.crystal_text, R.color.crystal_spinner},
            {R.color.ruby_card, R.color.ruby_text, R.color.ruby_spinner}, {R.color.sapphire_card, R.color.sapphire_text, R.color.sapphire_spinner}, {R.color.emerald_card, R.color.emerald_text, R.color.emerald_spinner},
            {R.color.firered_card, R.color.firered_text, R.color.firered_spinner}, {R.color.leafgreen_card, R.color.leafgreen_text, R.color.leafgreen_spinner},
            {R.color.diamond_card, R.color.diamond_text, R.color.diamond_spinner}, {R.color.pearl_card, R.color.pearl_text, R.color.pearl_spinner}, {R.color.platinum_card, R.color.platinum_text, R.color.platinum_spinner},
            {R.color.heartgold_card, R.color.heartgold_text, R.color.heartgold_spinner}, {R.color.soulsilver_card, R.color.soulsilver_text, R.color.soulsilver_spinner},
            {R.color.black_card, R.color.black_text, R.color.black_spinner}, {R.color.white_card, R.color.white_text, R.color.white_spinner},
            {R.color.black2_card, R.color.black2_text, R.color.black2_spinner}, {R.color.white2_card, R.color.white2_text, R.color.white2_spinner},
            {R.color.x_card, R.color.x_text, R.color.x_spinner}, {R.color.y_card, R.color.y_text, R.color.y_spinner},
            {R.color.omegaruby_card, R.color.omegaruby_text, R.color.omegaruby_spinner}, {R.color.alphasapphire_card, R.color.alphasapphire_text, R.color.alphasapphire_spinner},
            {R.color.sun_card, R.color.sun_text, R.color.sun_spinner}, {R.color.moon_card, R.color.moon_text, R.color.moon_spinner},
            {R.color.ultrasun_card, R.color.ultrasun_text, R.color.ultrasun_spinner}, {R.color.ultramoon_card, R.color.ultramoon_text, R.color.ultramoon_spinner},
            {R.color.pikachu_card, R.color.pikachu_text, R.color.pikachu_spinner}, {R.color.eevee_card, R.color.eevee_text, R.color.eevee_spinner},
            {R.color.sword_card, R.color.sword_text, R.color.sword_spinner}, {R.color.shield_card, R.color.shield_text, R.color.shield_spinner},
            {R.color.brilliantdiamond_card, R.color.brilliantdiamond_text, R.color.brilliantdiamond_spinner}, {R.color.shinningpearl_card, R.color.shinningpearl_text, R.color.shinningpearl_spinner}, {R.color.arceus_card, R.color.arceus_text, R.color.arceus_spinner},
            {R.color.scarlet_card, R.color.scarlet_text, R.color.scarlet_spinner}, {R.color.violet_card, R.color.violet_text, R.color.violet_spinner}, {R.color.za_card, R.color.za_text, R.color.za_spinner},
            {R.color.winds_card, R.color.winds_text, R.color.winds_spinner}, {R.color.waves_card, R.color.waves_text, R.color.waves_spinner} };

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

        if(position < themes.length) {
            view.setBackgroundResource(themes[position][0]);
            gameName.setTextColor(ContextCompat.getColor(this.getContext(), themes[position][1]));
            gameTasks.setBackgroundResource(themes[position][2]);
        }
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
