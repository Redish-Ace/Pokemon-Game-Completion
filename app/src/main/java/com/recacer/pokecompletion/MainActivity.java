package com.recacer.pokecompletion;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button[] btns;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
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

        btns = new Button[]{findViewById(R.id.btn_red), findViewById(R.id.btn_blue), findViewById(R.id.btn_yellow),
                findViewById(R.id.btn_gold), findViewById(R.id.btn_silver), findViewById(R.id.btn_crystal),
                findViewById(R.id.btn_ruby), findViewById(R.id.btn_sapphire), findViewById(R.id.btn_emerald), findViewById(R.id.btn_fire), findViewById(R.id.btn_leaf),
                findViewById(R.id.btn_diamond), findViewById(R.id.btn_pearl), findViewById(R.id.btn_platinum), findViewById(R.id.btn_heart), findViewById(R.id.btn_soul),
                findViewById(R.id.btn_black), findViewById(R.id.btn_black2), findViewById(R.id.btn_white), findViewById(R.id.btn_white2),
                findViewById(R.id.btn_x), findViewById(R.id.btn_y), findViewById(R.id.btn_omega), findViewById(R.id.btn_alpha),
                findViewById(R.id.btn_sun), findViewById(R.id.btn_moon), findViewById(R.id.btn_ultras), findViewById(R.id.btn_ultram), findViewById(R.id.btn_pikachu), findViewById(R.id.btn_eevee),
                findViewById(R.id.btn_sword), findViewById(R.id.btn_shield), findViewById(R.id.btn_brilliant), findViewById(R.id.btn_shinning), findViewById(R.id.btn_la),
                findViewById(R.id.btn_scarlet), findViewById(R.id.btn_violet), findViewById(R.id.btn_za)};

        CompletedJSON.updateJSON(this.getBaseContext());

        for(final Button btn: btns){
            String game = btn.getText().toString();
            if(game.contains("\n")) {
                game = game.split("\n")[1];
            }

            setTotalCompletedTasks(btn, game);

            String finalGame = game;
            btn.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("GAME_NAME", finalGame);
                startActivity(intent);
            });
        }

        Button btn = findViewById(R.id.csvButton);
        btn.setOnClickListener(v -> CompletedJSON.writeCSV(this.getBaseContext()));
    }

    @Override
    protected void onResume() {
        super.onResume();

        for(final Button btn: btns){
            String game = btn.getText().toString();
            if(game.contains("\n")) {
                game = game.split("\n")[1];
            }

            setTotalCompletedTasks(btn, game);
        }
    }

    @SuppressLint("DefaultLocale")
    public void setTotalCompletedTasks(Button btn, String game){
        int total = 0;

        ArrayList<TaskData> tasks = CompletedJSON.readTaskJSON(this.getBaseContext(), game);
        for(TaskData task : tasks){
            if(task.completed) total++;
        }

        if(total == tasks.size()){
            btn.setText(String.format("\n%s\n%d ✅", game, total));
        } else if(total > 0){
            btn.setText(String.format("\n%s\n%d", game, total));
        }


    }
}