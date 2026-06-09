package com.recacer.pokecompletion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GameAdapter extends ArrayAdapter<GameData> {
    ArrayList<CompletedTask> completedTaskList;
    int[][] themes = { {R.color.red_card, R.color.white, R.color.red_spinner}, {R.color.blue_card, R.color.white, R.color.blue_spinner}, {R.color.yellow_card, R.color.white, R.color.yellow_spinner},
            {R.color.gold_card, R.color.white, R.color.gold_spinner}, {R.color.silver_card, R.color.white, R.color.silver_spinner}, {R.color.crystal_card, R.color.white, R.color.crystal_spinner},
            {R.color.ruby_card, R.color.white, R.color.ruby_spinner}, {R.color.sapphire_card, R.color.white, R.color.sapphire_spinner}, {R.color.emerald_card, R.color.white, R.color.emerald_spinner},
            {R.color.firered_card, R.color.white, R.color.firered_spinner}, {R.color.leafgreen_card, R.color.white, R.color.leafgreen_spinner},
            {R.color.diamond_card, R.color.white, R.color.diamond_spinner}, {R.color.pearl_card, R.color.white, R.color.pearl_spinner}, {R.color.platinum_card, R.color.white, R.color.platinum_spinner},
            {R.color.heartgold_card, R.color.white, R.color.heartgold_spinner}, {R.color.soulsilver_card, R.color.white, R.color.soulsilver_spinner},
            {R.color.black_card, R.color.white, R.color.black_spinner}, {R.color.white_card, R.color.black, R.color.white_spinner},
            {R.color.black2_card, R.color.white, R.color.black2_spinner}, {R.color.white2_card, R.color.black, R.color.white2_spinner},
            {R.color.x_card, R.color.white, R.color.x_spinner}, {R.color.y_card, R.color.white, R.color.y_spinner},
            {R.color.omegaruby_card, R.color.white, R.color.omegaruby_spinner}, {R.color.alphasapphire_card, R.color.white, R.color.alphasapphire_spinner},
            {R.color.sun_card, R.color.white, R.color.sun_spinner}, {R.color.moon_card, R.color.white, R.color.moon_spinner},
            {R.color.ultrasun_card, R.color.white, R.color.ultrasun_spinner}, {R.color.ultramoon_card, R.color.white, R.color.ultramoon_spinner},
            {R.color.pikachu_card, R.color.white, R.color.pikachu_spinner}, {R.color.eevee_card, R.color.white, R.color.eevee_spinner},
            {R.color.sword_card, R.color.white, R.color.sword_spinner}, {R.color.shield_card, R.color.white, R.color.shield_spinner},
            {R.color.brilliantdiamond_card, R.color.white, R.color.brilliantdiamond_spinner}, {R.color.shinningpearl_card, R.color.white, R.color.shinningpearl_spinner}, {R.color.arceus_card, R.color.black, R.color.arceus_spinner},
            {R.color.scarlet_card, R.color.white, R.color.scarlet_spinner}, {R.color.violet_card, R.color.white, R.color.violet_spinner}, {R.color.za_card, R.color.white, R.color.za_spinner},
            {R.color.winds_card, R.color.white, R.color.winds_spinner}, {R.color.waves_card, R.color.white, R.color.waves_spinner} };

    public GameAdapter(@NonNull Context context, @NonNull List<GameData> objects) {
        super(context, R.layout.game_item, R.id.gameName, objects);
        completedTaskList = CompletedJSON.getCompletedTasks(this.getContext());
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
                CompletedJSON.writeTaskJSON(gameTasks.getContext(), gameData.name, gameTasks.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return view;
    }
}
