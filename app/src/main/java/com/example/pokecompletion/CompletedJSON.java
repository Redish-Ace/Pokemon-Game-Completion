package com.example.pokecompletion;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class CompletedJSON {
    static String filename = "completed_tasks.json";
    static JSONObject jsonObject;
    public static StringBuilder readTaskJSON(Context context) throws JSONException {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream is = context.openFileInput(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line = br.readLine()) != null){
                stringBuilder.append(line).append("\n");
            }
            br.close();
            is.close();
        } catch (Exception e) {
            Log.d("Reading JSON Error", Objects.requireNonNull(e.getMessage()));
            createJSON(context);
            readTaskJSON(context);
        }
        return stringBuilder;
    }

    @NonNull
    public static ArrayList<CompletedTask> getCompletedTasks(Context context) {
        ArrayList<CompletedTask> completedTaskList = new ArrayList<>();
        try {
            StringBuilder stringBuilder = readTaskJSON(context);
            jsonObject = new JSONObject(stringBuilder.toString());
            Iterator<String> gamesIterator = jsonObject.keys();
            while (gamesIterator.hasNext()) {
                String game = gamesIterator.next();
                String task = jsonObject.getString(game);
                CompletedTask completedTask = new CompletedTask(game, task);
                completedTaskList.add(completedTask);
            }
        } catch (JSONException e){
            Log.d("Creating Task List Error", Objects.requireNonNull(e.getMessage()));
        }
        return completedTaskList;
    }

    public static void writeTaskJSON(Context context, String game, String task){
        try {
            jsonObject.put(game, task);
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(jsonObject.toString().getBytes());
            fos.close();
            Log.d("Update JSON", "Successful");
        } catch (Exception e){
            Log.d("Update JSON Error", Objects.requireNonNull(e.getMessage()));
        }
    }

    public static void createJSON(Context context){
        InputStream is = context.getResources().openRawResource(R.raw.tasks_completed);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(stringBuilder.toString().getBytes());
            fos.close();
        }
        catch (Exception e){
            Log.d("Create JSON Error", Objects.requireNonNull(e.getMessage()));
        }
    }
}
