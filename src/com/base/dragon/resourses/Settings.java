package com.base.dragon.resourses;

import java.io.*;

public class Settings {

    private static String fileName = ".settings";

    public static boolean isSoundEnabled = true;

    public static void save(FileIO files){
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(files.writeFile(fileName)));
            out.write(Boolean.toString(isSoundEnabled));

        } catch (IOException e) {}
        finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
            }
        }
    }

    public static void load(FileIO files){
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    files.readFile(fileName)));
            isSoundEnabled = Boolean.parseBoolean(in.readLine());

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

    /**
     * Change Sound State
     */
    public static void changeSound(FileIO files){
        isSoundEnabled = !isSoundEnabled;
        save(files);
    }
}
