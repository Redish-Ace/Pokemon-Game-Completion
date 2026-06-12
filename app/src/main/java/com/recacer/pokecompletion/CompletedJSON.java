package com.recacer.pokecompletion;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Objects;

public class CompletedJSON {
    static String filename = "completed_tasks.json";
    static JSONObject jsonObject;

    public static ArrayList<TaskData> readTaskJSON(Context context, String game){
        ArrayList<TaskData> tasksList = new ArrayList<>();
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

            jsonObject = new JSONObject(stringBuilder.toString());
            JSONObject taskObj = jsonObject.getJSONObject(game);
            Iterator<String> tasks = taskObj.keys();
            while(tasks.hasNext()){
                String task = tasks.next();
                boolean completed = taskObj.getBoolean(task);
                tasksList.add(new TaskData(task, completed));
            }
        } catch (Exception e) {
            Log.d("Reading JSON Error", Objects.requireNonNull(e.getMessage()));
            createJSON(context);
            readTaskJSON(context, game);
        }

        return tasksList;
    }

    public static void writeTaskJSON(Context context, String game, String task, boolean completed){
        try {
            jsonObject.getJSONObject(game).put(task, completed);
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
            Log.d("Create JSON", "Successful");
        }
        catch (Exception e){
            Log.d("Create JSON Error", Objects.requireNonNull(e.getMessage()));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static void updateJSON(Context context){
        if (!context.getFileStreamPath(filename).exists()) {
            createJSON(context);
            return;
        }

        StringBuilder internal_sb = new StringBuilder();
        JSONObject internalObj = null;
        try {
            InputStream is = context.openFileInput(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line = br.readLine()) != null){
                internal_sb.append(line).append("\n");
            }
            br.close();
            internalObj = new JSONObject(internal_sb.toString());
        } catch (Exception e) {
            Log.d("Reading internal JSON Error", Objects.requireNonNull(e.getMessage()));
        }

        JSONObject rawObj = null;
        InputStream is = context.getResources().openRawResource(R.raw.tasks_completed);
        StringBuilder raw_sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                raw_sb.append(line).append("\n");
            }
            rawObj = new JSONObject(raw_sb.toString());
        }
        catch (Exception e){
            Log.d("Reading raw JSON Error", Objects.requireNonNull(e.getMessage()));
        }

        try{
            JSONObject updatedJSON = new JSONObject();
            Iterator<String> internalGames = internalObj.keys();

            while(internalGames.hasNext()){
                String game = internalGames.next();
                JSONObject intGameObj = internalObj.getJSONObject(game);
                JSONObject rawGameObj = rawObj.getJSONObject(game);

                JSONObject updatedObj = new JSONObject();
                Iterator<String> tasks = rawGameObj.keys();

                while(tasks.hasNext()){
                    String task = tasks.next();
                    if(intGameObj.has(task)){
                        updatedObj.put(task, intGameObj.getBoolean(task));
                    } else{
                        updatedObj.put(task, rawGameObj.getBoolean(task));
                    }
                }

                updatedJSON.put(game, updatedObj);
            }

            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(updatedJSON.toString().getBytes());
            fos.close();

            Log.d("Update JSON", "Successful");
        } catch (Exception e){
            Log.d("Update JSON Error", Objects.requireNonNull(e.getMessage()));
        }
    }

    public static void writeCSV(Context context) {
        ContentResolver resolver = context.getContentResolver();
        Uri collectionUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

        String selection = MediaStore.MediaColumns.DISPLAY_NAME + "=? AND " +
                MediaStore.MediaColumns.RELATIVE_PATH + "=?";
        String[] selectionArgs = new String[]{"completed_tasks.csv", Environment.DIRECTORY_DOCUMENTS + "/"};

        int deleted = resolver.delete(collectionUri, selection, selectionArgs);
        if (deleted > 0) {
            Log.d("CSV Write", "Deleted existing file: " + deleted + " record(s)");
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, "completed_tasks.csv");
        values.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);

        Uri uri = resolver.insert(collectionUri, values);

        if (uri != null) {
            try (OutputStream outputStream = resolver.openOutputStream(uri);
                 OutputStreamWriter writer = new OutputStreamWriter(outputStream)) {


                Toast.makeText(context, "Saved into completed_tasks.csv in Documents folder", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                Log.d("CSV Write Error", Objects.requireNonNull(e.getMessage()));
                Toast.makeText(context, "Failed to save CSV file", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
