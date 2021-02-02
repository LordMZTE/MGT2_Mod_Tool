package com.github.lmh01.mgt2mt.data_stream;

import com.github.lmh01.mgt2mt.util.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class ImportSettings{
    private static final Logger LOGGER = LoggerFactory.getLogger(ImportSettings.class);

    public static boolean Import(String fileLocation, boolean importCustomSettings) {
        LOGGER.info("Starting settings import process...");

        try {
            LOGGER.info("Scanning for File '" + fileLocation + "'...");
            File file = new File(fileLocation);
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            LOGGER.info("Beginning to import settings from file: " + file);
            int setting = 1;
            String currentLine;
            while((currentLine = reader.readLine()) != null) {
                switch(setting) {
                    case 1:
                        Settings.mgt2FilePath = currentLine; break;
                    /*case 2:
                        Settings.languageToAdd = currentLine; break;*/
                    case 3:
                        if(currentLine.equals("true")){
                            Settings.enableDebugLogging = true;
                        }else if(currentLine.equals("false")){
                            Settings.enableDebugLogging = false;
                        } break;
                    case 4:
                        if(currentLine.equals("true")){
                            Settings.disableSafetyFeatures = true;
                        }else if(currentLine.equals("false")){
                            Settings.disableSafetyFeatures = false;
                        } break;
                    case 5:
                        Settings.steamLibraryFolder = currentLine; break;
                    case 6:
                        if(currentLine.equals("true")){
                            Settings.enableCustomFolder = true;
                        }else if(currentLine.equals("false")){
                            Settings.enableCustomFolder = false;
                        } break;
                }
                if(Settings.enableDebugLogging){
                    LOGGER.info("Imported Setting (" + setting + "): " + currentLine);
                }
                ++setting;
            }
            if(Settings.enableDebugLogging){
                LOGGER.info("Import Complete!");
            }
            reader.close();
            return true;
        } catch (FileNotFoundException | UnsupportedEncodingException var6) {
            var6.printStackTrace();
            LOGGER.info("Unable to import settings: File not found! Using default settings!");
            Settings.resetSettings();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}