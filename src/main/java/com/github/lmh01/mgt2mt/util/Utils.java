package com.github.lmh01.mgt2mt.util;

import com.github.lmh01.mgt2mt.MadGamesTycoon2ModTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;

public class Utils {

    //These are the files inside the mgt2 file structure that are used inside this tool.
    public static final File FILE_GENRES = new File(Utils.getMGT2DataPath() + "\\Genres.txt");
    public static final File FILE_NPC_GAMES = new File(Utils.getMGT2DataPath() + "\\NpcGames.txt");
    public static final String GITHUB_URL = "https://github.com/LMH01/MGT2_Mod_Tool";
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    /**
     * @return returns the current date time in format: YYYY-MM-DD-HH-MM
     */
    public static String getCurrentDateTime(){
        return LocalDateTime.now().getYear() + "-" +
                LocalDateTime.now().getMonth() + "-"+
                LocalDateTime.now().getDayOfMonth() + "-" +
                LocalDateTime.now().getHour() + "-" +
                LocalDateTime.now().getMinute();
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

    /**
     * Opens a message dialog with a specified error message.
     * @param errorMessageKey the error message key. see this functions for meanings
     * @param e The exception
     */
    public static void showErrorMessage(int errorMessageKey, Exception e){
        switch(errorMessageKey){
            // 1 = Used when AnalyzeExistingGenres.analyzeGenreFile() throws an exception.
            case 1: JOptionPane.showMessageDialog(null, "The Genres.txt file could not be analyzed.\nFile not found: Please check if your mgt2 folder is set correctly.\n\nException:\n" + e.getMessage(), "Unable to continue", JOptionPane.ERROR_MESSAGE);
            // 2 = When it is unsuccessful to open the github repository.
            case 2: JOptionPane.showMessageDialog(null, "Unable to open Github repository:\n\nException:\n" + e.getMessage(), "Can't open page", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Opens a message dialog with a specified error message.
     * @param confirmMessageKey The confirm message key. See this functions for meanings.
     * @param e The exception
     * @return Returns true when the user clicks yes. Returns false when the user clicks no.
     */
    public static boolean showConfirmDialog(int confirmMessageKey, Exception e){
        if(confirmMessageKey == 1){
            return JOptionPane.showConfirmDialog(null, "The backup could not be created.\n\nException:\n" + e.getMessage() + "\nDo you want to continue anyway?", "Unable to backup file", JOptionPane.YES_NO_OPTION) == 0;
        }
        return true;
    }

    /**
     * Opens a confirm dialog with a specified message.
     * @param confirmMessageKey The confirm message key. See this function for meanings.
     * @return Returns true when the user clicks yes. Returns false when the user clicks no.
     */
    public static boolean showConfirmDialog(int confirmMessageKey){
        if(confirmMessageKey == 1){
            return JOptionPane.showConfirmDialog(null, "Are you sure?\nYour progress will be saved until you close the program.", "Cancel add new genre", JOptionPane.YES_NO_OPTION) == 0;
        }
        return true;
    }
    /**
     * Opens the Github page for MGT2MT in the default browser.
     */
    public static void openGithubPage() throws Exception {
        Desktop.getDesktop().browse(new URL(GITHUB_URL).toURI());
    }

    /**
     * @param folder The folder that should be tested if contains the file.
     * @param content The content that should be found.
     * @return Returns true when the input file is the MGT2 folder.
     */
    public static boolean doesFoldercontainFile(String folder, String content){
        File file = new File(folder);
        if(file.exists()){
            File[] filesInFolder = file.listFiles();
            for (int i = 0; i < Objects.requireNonNull(filesInFolder).length; i++) {
                if(filesInFolder[i].getName().equals(content)){
                    return true;
                }
                LOGGER.info(filesInFolder[i].getName());
            }
        }else{
            LOGGER.info("File does not exist.");
        }
        return false;
    }
}
