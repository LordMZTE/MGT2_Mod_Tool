package com.github.lmh01.mgt2mt.dataStream;

import com.github.lmh01.mgt2mt.util.Backup;
import com.github.lmh01.mgt2mt.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

public class NPCGameListChanger {
    private static final Logger LOGGER = LoggerFactory.getLogger(NPCGameListChanger.class);

    /**
     * Modifies the NpcGames.txt file to include/remove a specified genre id
     * @param genreID The genre id that should be added/removed.
     * @param addGenreID If true the genre will be added to the list. If false the genre will be removed from the list.
     * @param chance The chance with which the genre should be added to the npc game list 100 = 100%
     * @return Returns true when process was successful. Returns false if an exception occurred.
     */
    public static boolean editNPCGames(int genreID, boolean addGenreID, int chance){
        try {
            File fileNpcGamesTemp = new File(Utils.getMGT2DataPath() + "\\NpcGames.txt.temp");
            fileNpcGamesTemp.createNewFile();
            Backup.createBackup(Utils.fileNpcGames);
            LOGGER.info("NpcGames.txt.temp has been created");
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(Utils.fileNpcGames), StandardCharsets.UTF_16LE));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileNpcGamesTemp), StandardCharsets.UTF_16LE));
            String currentLine;
            while((currentLine = br.readLine()) != null){
                if(addGenreID){
                    int randomNum = ThreadLocalRandom.current().nextInt(1, 100);
                    if(randomNum>(100-chance)){
                        bw.write(currentLine + "<" + genreID + ">" + System.getProperty("line.separator"));
                    }else{
                        bw.write(currentLine + System.getProperty("line.separator"));
                    }
                }else{
                    bw.write(currentLine.replace("<" + genreID + ">", "") + System.getProperty("line.separator"));
                }
            }
            br.close();
            bw.close();
            Utils.fileNpcGames.delete();
            fileNpcGamesTemp.renameTo(Utils.fileNpcGames);
            if(addGenreID){
                ChangeLog.addLogEntry(2, Integer.toString(genreID));
            }else{
                ChangeLog.addLogEntry(3, Integer.toString(genreID));
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
