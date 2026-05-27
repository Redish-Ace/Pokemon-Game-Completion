package com.example.pokecompletion;

public class CompletedTask {
    String game, task;

    public CompletedTask(String game, String task) {
        this.game = game;
        this.task = task;
    }

    public void setTask(String task) {
        this.task = task;
    }
}
