package com.github.lmh01.mgt2mt.data_stream;

import com.github.lmh01.mgt2mt.util.Settings;
import com.github.lmh01.mgt2mt.util.TranslationManager;
import com.github.lmh01.mgt2mt.util.Utils;
import com.github.lmh01.mgt2mt.windows.WindowMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditPublishersFile {
    private static final Logger LOGGER = LoggerFactory.getLogger(EditPublishersFile.class);
    public static void addPublisher(HashMap<String, String> hashMap, String publisherImageFilePath) throws IOException {
        LOGGER.info("Adding new publisher");
        File publisherFile = Utils.getPublisherFile();
        if(Settings.disableSafetyFeatures){
            LOGGER.info("Deleting old publisher file.");
        }
        if(publisherFile.exists()){
            publisherFile.delete();
            publisherFile.createNewFile();
        }
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(publisherFile), StandardCharsets.UTF_8));
        bw.write("\ufeff");
        LOGGER.info("Writing contents of list to file: " + publisherFile.getPath());
        List<Map<String, String>> list = AnalyzeExistingPublishers.getListMap();
        for (Map<String, String> map : list) {
            bw.write("[ID]" + map.get("ID"));bw.write(System.getProperty("line.separator"));
            TranslationManager.printLanguages(bw, map);
            bw.write("[DATE]" + map.get("DATE"));bw.write(System.getProperty("line.separator"));
            bw.write("[PIC]" + map.get("PIC"));bw.write(System.getProperty("line.separator"));
            bw.write("[DEVELOPER]" + map.get("DEVELOPER"));bw.write(System.getProperty("line.separator"));
            bw.write("[PUBLISHER]" + map.get("PUBLISHER"));bw.write(System.getProperty("line.separator"));
            bw.write("[MARKET]" + map.get("MARKET"));bw.write(System.getProperty("line.separator"));
            bw.write("[SHARE]" + map.get("SHARE"));bw.write(System.getProperty("line.separator"));
            bw.write("[GENRE]" + map.get("GENRE"));bw.write(System.getProperty("line.separator"));
            bw.write(System.getProperty("line.separator"));
        }
        bw.write("[ID]" + hashMap.get("ID"));bw.write(System.getProperty("line.separator"));
        TranslationManager.printLanguages(bw, hashMap);
        bw.write("[DATE]" + hashMap.get("DATE"));bw.write(System.getProperty("line.separator"));
        bw.write("[PIC]" + hashMap.get("PIC"));bw.write(System.getProperty("line.separator"));
        bw.write("[DEVELOPER]" + hashMap.get("DEVELOPER"));bw.write(System.getProperty("line.separator"));
        bw.write("[PUBLISHER]" + hashMap.get("PUBLISHER"));bw.write(System.getProperty("line.separator"));
        bw.write("[MARKET]" + hashMap.get("MARKET"));bw.write(System.getProperty("line.separator"));
        bw.write("[SHARE]" + hashMap.get("SHARE"));bw.write(System.getProperty("line.separator"));
        bw.write("[GENRE]" + hashMap.get("GENRE"));bw.write(System.getProperty("line.separator"));
        bw.write(System.getProperty("line.separator"));
        bw.write("[EOF]");
        bw.close();
        if(!publisherImageFilePath.equals(Settings.mgt2FilePath + "\\Mad Games Tycoon 2_Data\\Extern\\CompanyLogos\\87.png")){
            File publisherImageFileToCopy = new File(publisherImageFilePath);
            File publisherImageFileTarget = new File(Utils.getCompanyLogosPath() + "//" + hashMap.get("PIC") + ".png");
            Files.copy(Paths.get(publisherImageFileToCopy.getPath()), Paths.get(publisherImageFileTarget.getPath()));
        }
        LOGGER.info("The publisher has been added: " + hashMap.get("NAME EN"));
    }

    /**
     * Removes the given publisher from the publisher list.
     * @param publisherNameEN The english publisher name.
     */
    public static boolean removePublisher(String publisherNameEN) throws IOException {
        AnalyzeExistingPublishers.analyzePublisherFile();
        LOGGER.info("Removing publisher: " + publisherNameEN);
        File publisherFile = Utils.getPublisherFile();
        if(Settings.disableSafetyFeatures){
            LOGGER.info("Deleting old publisher file.");
        }
        if(publisherFile.exists()){
            publisherFile.delete();
            publisherFile.createNewFile();
        }

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(publisherFile), StandardCharsets.UTF_8));
        bw.write("\ufeff");
        LOGGER.info("Writing contents of list to file: " + publisherFile.getPath());
        int publisherToSkip = getPublisherPositionInList(publisherNameEN);
        List<Map<String, String>> list = AnalyzeExistingPublishers.getListMap();
        for(int i=0; i<list.size(); i++){
            if(i != publisherToSkip){
                Map<String,String> map = list.get(i);
                bw.write("[ID]" + map.get("ID"));bw.write(System.getProperty("line.separator"));
                TranslationManager.printLanguages(bw, map);
                bw.write("[DATE]" + map.get("DATE"));bw.write(System.getProperty("line.separator"));
                bw.write("[PIC]" + map.get("PIC"));bw.write(System.getProperty("line.separator"));
                bw.write("[DEVELOPER]" + map.get("DEVELOPER"));bw.write(System.getProperty("line.separator"));
                bw.write("[PUBLISHER]" + map.get("PUBLISHER"));bw.write(System.getProperty("line.separator"));
                bw.write("[MARKET]" + map.get("MARKET"));bw.write(System.getProperty("line.separator"));
                bw.write("[SHARE]" + map.get("SHARE"));bw.write(System.getProperty("line.separator"));
                bw.write("[GENRE]" + map.get("GENRE"));bw.write(System.getProperty("line.separator"));
                bw.write(System.getProperty("line.separator"));
            }
        }
        bw.write("[EOF]");
        bw.close();
        int iconId = getPublisherIconIdByName(publisherNameEN);
        if(iconId>146){
            File publisherIcon = new File(Utils.getMGT2CompanyLogosPath() + iconId + ".png");
            LOGGER.info("publisherIcon: " + publisherIcon.getPath());
            if(publisherIcon.exists()){
                publisherIcon.delete();
                LOGGER.info("Image file for publisher " + publisherNameEN + " has been removed.");
            }
        }
        WindowMain.checkActionAvailability();
        return true;
    }

    /**
     * @param publisherNameEN The publisher name that should be searched
     * @return returns the number from the list position where the input genre is found.
     */
    public static int getPublisherPositionInList(String publisherNameEN){
        int returnValue = 0;
        List<Map<String, String>> list = AnalyzeExistingPublishers.getListMap();
        try{
            for(int i=0; i<list.size(); i++){
                Map<String, String> map = list.get(i);
                if(map.get("NAME EN").equals(publisherNameEN)){
                    returnValue = i;
                }
            }
        }catch(NullPointerException ignored){

        }
        return returnValue;
    }

    private static int getPublisherIconIdByName(String publisherNameEN){
        int returnValue = 0;
        List<Map<String, String>> list = AnalyzeExistingPublishers.getListMap();
        try{
            for (Map<String, String> map : list) {
                if (map.get("NAME EN").equals(publisherNameEN)) {
                    returnValue = Integer.parseInt(map.get("PIC"));
                }
            }
        }catch(NullPointerException ignored){

        }
        return returnValue;
    }
}
