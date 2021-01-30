package com.github.lmh01.mgt2mt.util;

import java.io.File;
import java.time.LocalDateTime;

public class Utils {

    //These are the files inside the mgt2 file structure that are used inside this tool.
    public static File fileGenres = new File(Utils.getMGT2DataPath() + "\\Genres.txt");
    public static File fileNpcGames = new File(Utils.getMGT2DataPath() + "\\NpcGames.txt");

    /**
     * @return returns the current date time in format: YYYY-MM-DD-HH-MM
     */
    public static String getCurrentDateTime(){
        String currentDateTime = LocalDateTime.now().getYear() + "-" +
                LocalDateTime.now().getMonth() + "-"+
                LocalDateTime.now().getDayOfMonth() + "-" +
                LocalDateTime.now().getHour() + "-" +
                LocalDateTime.now().getMinute();
        return currentDateTime;
    }
    /**
     * @return Returns the path to \Mad Games Tycoon 2_Data\Extern\Text\DATA\
     */
    public static String getMGT2DataPath(){
        return Settings.mgt2FilePath + "\\Mad Games Tycoon 2_Data\\Extern\\Text\\DATA\\";
    }

    /**
     * @param s The input String
     * @return Returns the input String without UTF8BOM
     */
    public static String removeUTF8BOM(String s) {
        if (s.startsWith("\uFEFF")) {
            s = s.substring(1);
        }
        return s;
    }
}
