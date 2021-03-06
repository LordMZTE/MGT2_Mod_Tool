package com.github.lmh01.mgt2mt.data_stream;

import com.github.lmh01.mgt2mt.util.Settings;
import com.github.lmh01.mgt2mt.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DataStreamHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataStreamHelper.class);

    /**
     * Searches the input file for the "NAME EN" key and returns its value. If not found returns -1.
     * @param file The file that should be searched for the key
     * @param charsetType Defines what charset the source file uses. Possible UTF_8BOM UTF_16LE
     */
    public static String getNameFromFile(File file, String charsetType) throws IOException {
        Map<Integer, String> map = getContentFromFile(file, charsetType);
        for(Map.Entry entry : map.entrySet()){
            if(entry.getValue().toString().contains("NAME EN")){
                return entry.getValue().toString().replace("[NAME EN]", "");
            }
        }
        return "-1";
    }

    public static void downloadZip(String URL, String destination) throws IOException {
        File destinationFile = new File(destination);
        if(destinationFile.exists()){
            destinationFile.delete();
        }
        destinationFile.getParentFile().mkdirs();
        new FileOutputStream(destination).getChannel().transferFrom(Channels.newChannel(new URL(URL).openStream()), 0, Long.MAX_VALUE);
        LOGGER.info("The zip file from " + URL + " has been successfully downloaded to " + destination);
    }

    /**
     * @param file The input file
     * @return Returns a list containing map entries for every data package in the input text file.
     */
    public static List<Map<String,String>> parseDataFile(File file) throws IOException{
        List<Map<String, String>> fileParts = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        String currentLine;
        boolean firstLine = true;
        boolean firstList = true;
        Map<String, String> mapCurrent = new HashMap<>();
        while((currentLine = reader.readLine()) != null){
            if(firstLine){
                currentLine = Utils.removeUTF8BOM(currentLine);
                if(currentLine.contains("EOF")){
                    //This is being put into the list when the file is empty except for the [EOF]
                    //A dummy id and name are inserted
                    mapCurrent.put("ID", "-1");
                    mapCurrent.put("NAME EN", "Dummy");
                    mapCurrent.put("PIC", "0");
                    fileParts.add(mapCurrent);
                    reader.close();
                    return fileParts;
                }
                firstLine = false;
            }
            if(currentLine.isEmpty()){
                fileParts.add(mapCurrent);
                mapCurrent = new HashMap<>();
                firstList = false;
            }else{
                boolean keyComplete = false;
                StringBuilder mapKey = new StringBuilder();
                StringBuilder mapValue = new StringBuilder();
                for(int i=1; i<currentLine.length(); i++){
                    if(!keyComplete){
                        if(String.valueOf(currentLine.charAt(i)).equals("]")){
                            keyComplete = true;
                            continue;
                        }
                    }
                    if(keyComplete){
                        mapValue.append(currentLine.charAt(i));
                    }else{
                        mapKey.append(currentLine.charAt(i));
                    }
                }
                mapCurrent.put(mapKey.toString(), mapValue.toString());
            }
        }
        if(firstList){
            fileParts.add(mapCurrent);
        }
        reader.close();
        return fileParts;
    }

    /**
     * @param folder The folder that should be tested if contains the file.
     * @param content The content that should be found.
     * @return Returns true when the input file is the MGT2 folder.
     */
    public static boolean doesFolderContainFile(String folder, String content){
        File file = new File(folder);
        if(file.exists()){
            File[] filesInFolder = file.listFiles();
            for (int i = 0; i < Objects.requireNonNull(filesInFolder).length; i++) {
                if(filesInFolder[i].getName().equals(content)){
                    return true;
                }
                if(Settings.enableDebugLogging){
                    LOGGER.info(filesInFolder[i].getName());
                }
            }
        }else{
            LOGGER.info("File does not exist.");
        }
        return false;
    }

    /**
     * @param file The input file
     * @param charsetType Defines what charset the source file uses. Possible UTF_8BOM UTF_16LE
     * @return Returns a map. The key is the line number and the value is the content for that line number.
     */
    public static Map<Integer, String> getContentFromFile(File file, String charsetType) throws IOException{
        BufferedReader br;
        if(charsetType.equals("UTF_8BOM")){
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        }else if(charsetType.equals("UTF_16LE")){
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_16LE));
        }else{
            return null;
        }
        String currentLine;
        boolean firstLine = true;
        Map<Integer, String> mapCurrent = new HashMap<>();
        int currentLineNumber = 1;
        while((currentLine = br.readLine()) != null){
            if(firstLine){
                currentLine = Utils.removeUTF8BOM(currentLine);
                firstLine = false;
            }
            mapCurrent.put(currentLineNumber, currentLine);
            currentLineNumber++;
        }
        br.close();
        return mapCurrent;
    }

    /**
     * @param folder The folder that should be searched for files.
     * @return Returns an array list containing all files inside the input folder
     */
    public static ArrayList<File> getFilesInFolder(String folder){
        return getFilesInFolderBlackList(folder, "EMPTY");
    }

    /**
     * @param folder The folder that should be searched for files.
     * @param blackList When the string entered here is found in the filename the file wont be added to the arrayListFiles.
     * @return Returns an array list containing all files inside the input folder
     */
    public static ArrayList<File> getFilesInFolderBlackList(String folder, String blackList){
        File file = new File(folder);
        ArrayList<File> arrayListFiles = new ArrayList<>();
        if(file.exists()){
            File[] filesInFolder = file.listFiles();
            for (int i = 0; i < Objects.requireNonNull(filesInFolder).length; i++) {
                if(!filesInFolder[i].getName().contains(blackList) || blackList.equals("EMPTY")){
                    arrayListFiles.add(filesInFolder[i]);
                    if(Settings.enableDebugLogging){
                        LOGGER.info(filesInFolder[i].getName());
                    }
                }
            }
        }
        return arrayListFiles;
    }

    /**
     * @param folder The folder that should be searched for files.
     * @param whiteList When the string entered here is found/equals the filename the file will be added to the arrayListFiles. All other files wont be added
     * @return Returns an array list containing all files inside the input folder
     */
    public static ArrayList<File> getFilesInFolderWhiteList(String folder, String whiteList){
        File file = new File(folder);
        ArrayList<File> arrayListFiles = new ArrayList<>();
        if(file.exists()){
            File[] filesInFolder = file.listFiles();
            for (int i = 0; i < Objects.requireNonNull(filesInFolder).length; i++) {
                if(filesInFolder[i].getName().contains(whiteList)){
                    arrayListFiles.add(filesInFolder[i]);
                    if(Settings.enableDebugLogging){
                        LOGGER.info(filesInFolder[i].getName());
                    }
                }
            }
        }
        return arrayListFiles;
    }

    /**
     * Unzips the zipFile to the destination directory.
     * @param zipFile The input zip file
     * @param destination The destination where the file should be unzipped to.
     */
    public static void unzip(String zipFile, File destination) throws IOException {
        LOGGER.info("Unzipping folder [" + zipFile + "] to [" + destination + "]");
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = newFile(destination, zipEntry);
            if(Settings.enableDebugLogging){
                LOGGER.info("Unzipped file: " + newFile.getPath());
            }
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                // fix for Windows-created archives
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }

                // write file content
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
        LOGGER.info("Unzipping complete!");
    }

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    /**
     * @param rootDirectory The directory where the file search is started
     * @param fileToSearch The file name that should be searched
     * @return Returns a array list containing all files that match the file to search
     */
    public static ArrayList<File> getFiles(File rootDirectory, String fileToSearch) throws IOException {
        ArrayList<File> arrayList = new ArrayList<>();
        Path start = Paths.get(rootDirectory.getPath());
        try (Stream<Path> stream = Files.walk(start, Integer.MAX_VALUE)) {
            List<String> collect = stream
                    .map(String::valueOf)
                    .sorted()
                    .collect(Collectors.toList());

            collect.forEach((string) -> {
                if(string.contains(fileToSearch)){
                    LOGGER.info(fileToSearch + ": " + string);
                    arrayList.add(new File(string));
                }
                if(Settings.enableDebugLogging){
                    LOGGER.info("current file: " + string);
                }
            });
        }
        return arrayList;
    }

    /**
     * Copied from https://www.baeldung.com/java-copy-directory
     * @param sourceDirectoryLocation The source
     * @param destinationDirectoryLocation The destination
     */
    public static void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation)
            throws IOException {
        Files.walk(Paths.get(sourceDirectoryLocation))
                .forEach(source -> {
                    Path destination = Paths.get(destinationDirectoryLocation, source.toString()
                            .substring(sourceDirectoryLocation.length()));
                    try {
                        Files.copy(source, destination);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    /**
     * Copied from https://www.baeldung.com/java-delete-directory
     * Deletes a complete directory with its contents
     */
    public static void deleteDirectory(File directoryToBeDeleted ){
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
                if(Settings.enableDebugLogging){
                    LOGGER.info("Deleting file: " + file.getPath());
                }
            }
        }
        directoryToBeDeleted.delete();
    }

    /**
     * @param directoryName The directory that should be searched.
     * @return Returns a list containing all files in the input folder plus subfolders
     */
    public static List<File> getFilesInFolderAndSubfolder(String directoryName){
        File directory = new File(directoryName);

        // get all the files from a directory
        File[] fList = directory.listFiles();
        assert fList != null;
        List<File> resultList = new ArrayList<>(Arrays.asList(fList));
        for (File file : fList) {
            if (file.isFile()) {
                System.out.println(file.getAbsolutePath());
            } else if (file.isDirectory()) {
                resultList.addAll(getFilesInFolder(file.getAbsolutePath()));
            }
        }
        //System.out.println(fList);
        return resultList;
    }
}
