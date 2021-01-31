package com.github.lmh01.mgt2mt.util;

import com.github.lmh01.mgt2mt.data_stream.ChangeLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Backup {
    private static final Logger LOGGER = LoggerFactory.getLogger(Backup.class);
    private static String latestBackupFolderName = "";
    public static final String BACKUP_FOLDER_PATH = System.getenv("APPDATA") + "//LMH01//MGT2_Mod_Manager//Backup//";

    /**
     * Creates a backup of a given file.
     * @param fileToBackup This is the file from which a backup should be created.
     * @throws IOException Throws IOException when backup was not successful.
     */
    public static void createBackup(File fileToBackup) throws IOException {
        createBackup(fileToBackup, false);
    }
    /**
     * Creates a backup of a given file. And sets it was initial backup
     * @param fileToBackup This is the file from which a backup should be created.
     * @param initialBackup Set true when this is the initial backup.
     * @throws IOException Throws IOException when backup was not successful.
     */
    public static void createBackup(File fileToBackup, boolean initialBackup) throws IOException {
        String currentTimeAndDay = Utils.getCurrentDateTime();
        latestBackupFolderName = currentTimeAndDay;
        ChangeLog.addLogEntry(5, fileToBackup.getName());
        File backupFileFolder = new File(BACKUP_FOLDER_PATH + currentTimeAndDay + "//");
        File directoryBackup = new File(BACKUP_FOLDER_PATH);
        if(!directoryBackup.exists()){
            directoryBackup.mkdirs();
        }
        File fileLatestBackupOfInputFile;
        if(initialBackup){
            fileLatestBackupOfInputFile = new File(System.getenv("APPDATA") + "//LMH01//MGT2_Mod_Manager//Backup//" + fileToBackup.getName()+ ".initialBackup");
            if(fileLatestBackupOfInputFile.exists()){
                if(JOptionPane.showConfirmDialog(null, "The initial backup of " + fileToBackup.getName() + " already exists.\nDo you want to replace it?", "Initial backup already exists", JOptionPane.YES_NO_OPTION) == 0){
                    fileLatestBackupOfInputFile.delete();
                }
            }
        }else{
            fileLatestBackupOfInputFile = new File(System.getenv("APPDATA") + "//LMH01//MGT2_Mod_Manager//Backup//" + fileToBackup.getName()+ ".latestBackup");
        }
        if(fileLatestBackupOfInputFile.exists()){
            if(!backupFileFolder.exists()){//Creates directory if backup directory for specified time does not exist
                backupFileFolder.mkdirs();
            }
            LOGGER.info(fileToBackup.getName() +  " backup already exists. Moving old backup into storage folder");
            File fileBackupSaved = new File(backupFileFolder.getPath() + "\\" + fileToBackup.getName());
            if(fileBackupSaved.exists()){//If the backup file already exists it will be deleted. (The backup file in the backup folder with timestamp) Maybe change the formatting of currentTimeAndDay to a format where seconds are also used to prevent this deletion.
                LOGGER.info("The file inside the storage folder does already exist. deleting...");
                fileBackupSaved.delete();
            }
            fileLatestBackupOfInputFile.renameTo(fileBackupSaved);
        }
        File fileBackupFile = new File(Utils.getMGT2DataPath() + fileToBackup.getName());
        Files.copy(Paths.get(fileBackupFile.getPath()), Paths.get(fileLatestBackupOfInputFile.getPath()));
    }

    /**
     * Restores a backup
     * @param initialBackup If true the initial backup will be restored. If false the latest backup will be restored.
     */
    public static void restoreBackup(boolean initialBackup){
        try {
            LOGGER.info("Restoring initial backup.");
            File fileGenresBackup;
            File fileNpcGamesBackup;
            if(initialBackup){
                fileGenresBackup = new File(System.getenv("APPDATA") + "//LMH01//MGT2_Mod_Manager//Backup//Genres.txt.initialBackup");
                fileNpcGamesBackup = new File(System.getenv("APPDATA") + "//LMH01//MGT2_Mod_Manager//Backup//NpcGames.txt.initialBackup");
            }else{
                fileGenresBackup = new File(System.getenv("APPDATA") + "//LMH01//MGT2_Mod_Manager//Backup//" + latestBackupFolderName + "//Genres.txt");
                fileNpcGamesBackup = new File(System.getenv("APPDATA") + "//LMH01//MGT2_Mod_Manager//Backup//" + latestBackupFolderName + "//NpcGames.txt");
            }
            Utils.FILE_GENRES.delete();
            Utils.FILE_NPC_GAMES.delete();
            Files.copy(Paths.get(fileGenresBackup.getPath()), Paths.get(Utils.FILE_GENRES.getPath()));
            Files.copy(Paths.get(fileNpcGamesBackup.getPath()), Paths.get(Utils.FILE_NPC_GAMES.getPath()));
            if(initialBackup){
                ChangeLog.addLogEntry(8, "");
                JOptionPane.showMessageDialog(null, "The initial backup has been restored.", "Backup restored", JOptionPane.INFORMATION_MESSAGE);
            }else{
                ChangeLog.addLogEntry(9, "");
                JOptionPane.showMessageDialog(null, "The latest backup has been restored.", "Backup restored", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            if(initialBackup){
                ChangeLog.addLogEntry(10, exception.getMessage());
                JOptionPane.showMessageDialog(null, "The initial backup could not be restored.\nThe initial backup file(s) are missing.\n\nException:\n" + exception.getMessage(), "Restoring failed", JOptionPane.ERROR_MESSAGE);
            }else{
                ChangeLog.addLogEntry(11, exception.getMessage());
                JOptionPane.showMessageDialog(null, "The latest backup could not be restored.\nThe latest backup file(s) are missing.\n\nException:\n" + exception.getMessage(), "Restoring failed", JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    public static void deleteAllBackups() throws IOException {
        File backupFolder = new File(BACKUP_FOLDER_PATH);
        if(backupFolder.exists()){
            Files.walk(Paths.get(backupFolder.getPath()))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    /**
     * Creates a backup of each file that can be edited with this tool.
     * @param initialBackup Set true when this is the initial backup.
     * @throws IOException Throws IOException when backup was not successful.
     */
    public static void createFullBackup(boolean initialBackup) throws IOException {
        if(initialBackup){
            Backup.createBackup(Utils.FILE_GENRES, true);
            Backup.createBackup(Utils.FILE_NPC_GAMES, true);
            ChangeLog.addLogEntry(6, "");
        }else{
            Backup.createBackup(Utils.FILE_GENRES);
            Backup.createBackup(Utils.FILE_NPC_GAMES);
        }
    }
}
