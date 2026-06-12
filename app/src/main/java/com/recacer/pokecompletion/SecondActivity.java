package com.recacer.pokecompletion;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Map;

public class SecondActivity extends AppCompatActivity {
    String gameName;
    ArrayList<TaskData> tasks;
    Map<String, Integer> colors = Map.ofEntries(
            Map.entry("Red", R.color.red), Map.entry("Blue", R.color.blue), Map.entry("Yellow", R.color.yellow),
            Map.entry("Gold", R.color.gold), Map.entry("Silver", R.color.silver), Map.entry("Crystal", R.color.crystal),
            Map.entry("Ruby", R.color.ruby), Map.entry("Sapphire", R.color.sapphire), Map.entry("Emerald", R.color.emerald),
            Map.entry("Fire Red", R.color.fire), Map.entry("Leaf Green", R.color.leaf),
            Map.entry("Diamond", R.color.diamond), Map.entry("Pearl", R.color.pearl), Map.entry("Platinum", R.color.platinum),
            Map.entry("Heart Gold", R.color.heart), Map.entry("Soul Silver", R.color.soul),
            Map.entry("Black", R.color.b), Map.entry("White", R.color.w), Map.entry("Black 2", R.color.b2), Map.entry("White 2", R.color.w2),
            Map.entry("X", R.color.x), Map.entry("Y", R.color.y), Map.entry("Omega Ruby", R.color.omega), Map.entry("Alpha Sapphire", R.color.alpha),
            Map.entry("Sun", R.color.sun), Map.entry("Moon", R.color.moon), Map.entry("Ultra Sun", R.color.ultras), Map.entry("Ultra Moon", R.color.ultram),
            Map.entry("Let's Go Pikachu", R.color.pikachu), Map.entry("Let's Go Eevee", R.color.eevee),
            Map.entry("Sword", R.color.sword), Map.entry("Shield", R.color.shield),
            Map.entry("Brilliant Diamond", R.color.brilliant), Map.entry("Shining Pearl", R.color.shining), Map.entry("Legends Arceus", R.color.arceus),
            Map.entry("Scarlet", R.color.scarlet), Map.entry("Violet", R.color.violet), Map.entry("Legends ZA", R.color.za)
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.second), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        gameName = getIntent().getStringExtra("GAME_NAME");
        TextView text = findViewById(R.id.game_name);
        text.setText(gameName);

        String[] blackText = {getString(R.string.white), getString(R.string.w2), getString(R.string.w2), getString(R.string.la)};
        text.setTextColor(getColor(R.color.white));
        if(Arrays.asList(blackText).contains(gameName)){
            text.setTextColor(getColor(R.color.black));
        }

        findViewById(R.id.second).setBackgroundColor(getColor(colors.get(gameName)));

        tasks = CompletedJSON.readTaskJSON(this.getBaseContext(), gameName);
        TaskAdapter adapter = new TaskAdapter(this.getBaseContext(), gameName, tasks);
        ListView view = findViewById(R.id.task_view);
        view.setAdapter(adapter);
    }
}
