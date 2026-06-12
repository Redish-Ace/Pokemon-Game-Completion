package com.recacer.pokecompletion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TaskAdapter extends ArrayAdapter<TaskData> {
    String game;
    Context context;
    public TaskAdapter(@NonNull Context context, String game, @NonNull List<TaskData> objects) {
        super(context, R.layout.task_item, objects);
        this.context = context;
        this.game = game;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NotNull ViewGroup parent){
        TaskData taskData = getItem(position);

        View view = convertView;
        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.task_item, parent, false);
        }

        CheckBox task = view.findViewById(R.id.task_check);
        assert taskData != null;
        task.setText(taskData.task);
        task.setTextColor(context.getColor(R.color.white));
        task.setChecked(taskData.completed);

        task.setOnClickListener(v -> {
            CompletedJSON.writeTaskJSON(this.getContext(), game, taskData.task, task.isChecked());

            taskData.completed = task.isChecked();
        });

        return view;
    }
}
