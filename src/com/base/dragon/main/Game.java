package com.base.dragon.main;

import com.base.dragon.resourses.AndroidFileIO;
import com.base.dragon.resourses.FileIO;

import java.io.*;
import java.util.Map;

public class Game {

    private final String fileName = ".dragon";
    private FileIO fileIO;

    GameView view;

    public Game(GameView view){
        this.view = view;

        fileIO = new AndroidFileIO(view.context);
    }

    public void save(){
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(fileIO.writeFile(fileName)));
            Map<String, Integer> currentData = view.getCurrentData();
            out.write(Integer.toString(currentData.get("level")));

        } catch (IOException e) {}
        finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
            }
        }
    }

    public void load(){
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    fileIO.readFile(fileName)));
            view.setLevel(Integer.parseInt(in.readLine()));

        } catch (IOException e) {
            // :( It's ok we have defaults
        } catch (NumberFormatException e) {
            // :/ It's ok, defaults save our day
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {}
        }
    }

}
