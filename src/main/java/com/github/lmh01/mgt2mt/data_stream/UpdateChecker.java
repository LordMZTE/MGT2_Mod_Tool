package com.github.lmh01.mgt2mt.data_stream;

import com.github.lmh01.mgt2mt.MadGamesTycoon2ModTool;
import com.github.lmh01.mgt2mt.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class UpdateChecker {
    public static String newestVersion = "";
    private static final String UPDATE_URL = "https://www.dropbox.com/s/7dut2h3tqdc92xz/mgt2mtNewestVerion.txt?dl=1";
    private static final Logger logger = LoggerFactory.getLogger(MadGamesTycoon2ModTool.class);
    public static void checkForUpdates(boolean showNoUpdateAvailableDialog){
        new Thread("UpdateChecker"){
            public void run(){
                try {
                    logger.info("Checking for updates...");
                    java.net.URL url = new URL(UPDATE_URL);
                    Scanner scanner = new Scanner(url.openStream());
                    newestVersion = scanner.nextLine();
                    if(!newestVersion.equals(MadGamesTycoon2ModTool.VERSION)){
                        logger.info("New version found: " + newestVersion);
                        if(JOptionPane.showConfirmDialog(null, "A new version is available: " + newestVersion + "\nIt is recommended to always use the newest version to keep this tool compatible with MGT2.\n\nDo you wan't to open the github page to download the newest version?", "New version available", JOptionPane.YES_NO_OPTION) == 0){
                            try {
                                Utils.openGithubPage();
                            } catch (Exception e) {
                                JOptionPane.showConfirmDialog(null, "Unable to open Github page:\n\nException:\n" + e.getMessage(), "Can't open page", JOptionPane.ERROR_MESSAGE);
                                e.printStackTrace();
                            }
                        }
                    }else{
                        logger.info("You are using the newest version");
                        if(showNoUpdateAvailableDialog){
                            JOptionPane.showMessageDialog(null, "You are using the newest version.\nNo update available.", "No update available", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }
}