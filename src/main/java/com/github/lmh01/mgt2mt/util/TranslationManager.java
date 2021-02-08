package com.github.lmh01.mgt2mt.util;

import javax.swing.*;
import java.util.ArrayList;

public class TranslationManager {
    /**
     * @return Returns a array list with the user input that should be used as translation. See cases for translation position in array list.
     */
    public static ArrayList<String> getTranslationsArrayList(){
        ArrayList<String> arrayListTranslations = new ArrayList<>();
        JTextField textFieldDescriptionTranslation = new JTextField();
        JLabel labelExplanation = new JLabel();
        Object[] params = {labelExplanation,textFieldDescriptionTranslation};
        boolean breakLoop = false;
        for(int i = 0; i<14; i++){
            if(!breakLoop){
                String language = "";
                switch(i){
                    case 0: language = "Arabic"; break;
                    case 1: language = "Chinese simplified"; break;
                    case 2: language = "Chinese traditional"; break;
                    case 3: language = "Czech"; break;
                    case 4: language = "English"; break;
                    case 5: language = "Spanish"; break;
                    case 6: language = "French"; break;
                    case 7: language = "German"; break;
                    case 8: language = "Hungarian"; break;
                    case 9: language = "Korean"; break;
                    case 10: language = "Portuguese"; break;
                    case 11: language = "Polish"; break;
                    case 12: language = "Russian"; break;
                    case 13: language = "Turkish"; break;
                }
                if(i != 3){
                    labelExplanation.setText("Enter the translation for " + language + ":");
                    if(JOptionPane.showConfirmDialog(null, params, "Add translation", JOptionPane.YES_NO_OPTION) == 0){
                        arrayListTranslations.add(textFieldDescriptionTranslation.getText());
                        textFieldDescriptionTranslation.setText("");
                    }else{
                        JOptionPane.showMessageDialog(null, "The translation process has been canceled.");
                        breakLoop = true;
                    }
                }else if(i == 3){
                    arrayListTranslations.add("English");
                }
            }
        }
        if(!breakLoop){
            StringBuilder translations = new StringBuilder();
            for(int i = 0; i<arrayListTranslations.size(); i++){
                switch(i){
                    case 0: translations.append("\nArabic: ").append(arrayListTranslations.get(i)); break;
                    case 1: translations.append("\nChinese simplified: ").append(arrayListTranslations.get(i)); break;
                    case 2: translations.append("\nChinese traditional: ").append(arrayListTranslations.get(i)); break;
                    case 3: translations.append("\nCzech: ").append(arrayListTranslations.get(i)); break;
                    //case 4: translations.append("\nnEnglish: ").append(arrayListTranslations.get(i)); break;
                    case 5: translations.append("\nSpanish: ").append(arrayListTranslations.get(i)); break;
                    case 6: translations.append("\nFrench: ").append(arrayListTranslations.get(i)); break;
                    case 7: translations.append("\nGerman: ").append(arrayListTranslations.get(i)); break;
                    case 8: translations.append("\nHungarian: ").append(arrayListTranslations.get(i)); break;
                    case 9: translations.append("\nKorean: ").append(arrayListTranslations.get(i)); break;
                    case 10: translations.append("\nPortuguese: ").append(arrayListTranslations.get(i)); break;
                    case 11: translations.append("\nPolish: " ).append(arrayListTranslations.get(i)); break;
                    case 12: translations.append("\nRussian: ").append(arrayListTranslations.get(i)); break;
                    case 13: translations.append("\nTurkish: ").append(arrayListTranslations.get(i)); break;
                }
            }
            JOptionPane.showMessageDialog(null, "The following translations have been added:\n" + translations + "\n", "Translations added", JOptionPane.INFORMATION_MESSAGE);
        }
        return arrayListTranslations;
    }
}
