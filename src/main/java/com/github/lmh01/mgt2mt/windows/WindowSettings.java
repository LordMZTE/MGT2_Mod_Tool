package com.github.lmh01.mgt2mt.windows;

import com.github.lmh01.mgt2mt.data_stream.DataStreamHelper;
import com.github.lmh01.mgt2mt.util.Backup;
import com.github.lmh01.mgt2mt.util.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class WindowSettings extends JFrame {

    static final WindowSettings FRAME = new WindowSettings();
    private static final Logger LOGGER = LoggerFactory.getLogger(WindowSettings.class);
    private static boolean customFolderSetAndValid = false;
    private static boolean unsavedChanges = false;
    private static String customFolderPath = "";
    JComboBox comboBoxMGT2FolderOperation = new JComboBox();
    JCheckBox checkBoxDisableSafety = new JCheckBox("Disable safety features");
    JCheckBox checkBoxDebugMode = new JCheckBox("Enable debug logging");

    public static void createFrame(){
        EventQueue.invokeLater(() -> {
            try {
                FRAME.setVisible(true);
                FRAME.setLocationRelativeTo(null);
                FRAME.loadCurrentSelections();
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    public WindowSettings(){
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setBounds(100, 100, 343, 200);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel SettingsText = new JLabel("Settings");
        SettingsText.setFont(new Font("Tahoma", Font.PLAIN, 15));
        SettingsText.setBounds(137, 11, 57, 19);
        contentPane.add(SettingsText);

        checkBoxDebugMode.setBounds(20, 40, 200, 23);
        checkBoxDebugMode.setToolTipText("Check this box to enable debug logging when opening this jar with the console.");
        checkBoxDebugMode.addActionListener(e -> {
            LOGGER.info("checkBoxDebugMode action: " + e.getActionCommand());
            unsavedChanges = checkBoxDebugMode.isSelected() != Settings.enableDebugLogging;
        });
        contentPane.add(checkBoxDebugMode);

        checkBoxDisableSafety.setBounds(20, 70, 200, 23);
        checkBoxDisableSafety.setToolTipText("<html>Check this box to disable the automatic genre id allocation.<br>If checked most spinners won't have a maximum value.<br>Do only enable when you use your own genre id system and you need the spinners to be unlocked.");
        checkBoxDisableSafety.addActionListener(e -> {
            LOGGER.info("checkBoxDisableSafety action: " + e.getActionCommand());
            if(checkBoxDisableSafety.isSelected()){
                checkBoxDisableSafety.setSelected(JOptionPane.showConfirmDialog(null, "Are you sure that you wan't to disable the safety features?\nThis could lead to problems.\n\nUSE THIS FEATURE AT YOUR OWN RISK!\nI WILL NOT TAKE ANY RESPONSIBILITY IF YOU BREAK SOMETHING!\n\nDisable safety features?", "Disable safety features?", JOptionPane.YES_NO_OPTION) == 0);
            }
            unsavedChanges = checkBoxDisableSafety.isSelected() != Settings.disableSafetyFeatures;
        });
        contentPane.add(checkBoxDisableSafety);

        JLabel lblMinecraftLocation = new JLabel("MGT2 Folder:");
        lblMinecraftLocation.setBounds(20, 103, 127, 14);
        contentPane.add(lblMinecraftLocation);

        AtomicBoolean automaticWasLastSelectedOption = new AtomicBoolean(!Settings.enableCustomFolder);
        AtomicBoolean manualWasLastSelectedOption = new AtomicBoolean(Settings.enableCustomFolder);
        comboBoxMGT2FolderOperation.setBounds(117, 100, 100, 23);
        comboBoxMGT2FolderOperation.setToolTipText("<html>[Automatic]: The folder will be selected automatically<br>[Manual]: Use a custom path.");
        comboBoxMGT2FolderOperation.addActionListener(e -> {
            LOGGER.info("comboBoxMGT2FolderOperation action: " + e.getActionCommand());
            if(Objects.equals(comboBoxMGT2FolderOperation.getSelectedItem(), "Manual") && !customFolderSetAndValid && !manualWasLastSelectedOption.get()){
                try {
                    automaticWasLastSelectedOption.set(false);
                    manualWasLastSelectedOption.set(true);
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); //set Look and Feel to Windows
                    JFileChooser fileChooser = new JFileChooser(); //Create a new GUI that will use the current(windows) Look and Feel
                    fileChooser.setDialogTitle("Choose 'Mad Games Tycoon 2' main folder:");
                    fileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY);
                    int return_value = fileChooser.showOpenDialog(null);
                    if(return_value == JFileChooser.APPROVE_OPTION){
                        String mgt2Folder = fileChooser.getSelectedFile().getPath();
                        if(DataStreamHelper.doesFolderContainFile(mgt2Folder, "Mad Games Tycoon 2.exe")){
                            JOptionPane.showMessageDialog(new Frame(), "Folder set.");
                            customFolderPath = mgt2Folder;
                            customFolderSetAndValid = true;
                            automaticWasLastSelectedOption.set(false);
                            manualWasLastSelectedOption.set(true);
                            unsavedChanges = true;
                        }else{
                            JOptionPane.showMessageDialog(new Frame(), "Folder is invalid:\nFolder does not contain Mad Games Tycoon 2.exe\nUsing automatic folder.", "Folder not set", JOptionPane.ERROR_MESSAGE);
                            comboBoxMGT2FolderOperation.setSelectedItem("Automatic");
                            automaticWasLastSelectedOption.set(true);
                            manualWasLastSelectedOption.set(false);
                            customFolderSetAndValid = false;
                            customFolderPath = "";
                            unsavedChanges = false;
                        }
                    }else{
                        comboBoxMGT2FolderOperation.setSelectedItem("Automatic");
                        automaticWasLastSelectedOption.set(true);
                        manualWasLastSelectedOption.set(false);
                        unsavedChanges = false;
                    }
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException classNotFoundException) {
                    classNotFoundException.printStackTrace();
                }
            }else if (comboBoxMGT2FolderOperation.getSelectedItem().equals("Automatic")){
                Settings.setMgt2Folder(true);
                automaticWasLastSelectedOption.set(true);
                manualWasLastSelectedOption.set(false);
                unsavedChanges = true;
            }
        });
        contentPane.add(comboBoxMGT2FolderOperation);

        JButton buttonResetCustomFolder = new JButton("Reset");
        buttonResetCustomFolder.setBounds(230, 99, 89, 23);
        buttonResetCustomFolder.setToolTipText("<html>Click to reset the custom folder.<br>This will restore the default folder.");
        buttonResetCustomFolder.addActionListener(actionEvent -> {
            customFolderSetAndValid = false;
            comboBoxMGT2FolderOperation.setSelectedItem("Automatic");
        });
        contentPane.add(buttonResetCustomFolder);

        JButton btnBack = new JButton("Back");
        btnBack.setBounds(10, 132, 69, 23);
        btnBack.setToolTipText("Click to get to the main page.");
        btnBack.addActionListener(actionEvent -> {
            if(unsavedChanges){
                String unsavedChanges = getChangesInSettings(checkBoxDebugMode, checkBoxDisableSafety);
                if(JOptionPane.showConfirmDialog(null, "You have made changes that have not been saved:\n\n" + unsavedChanges + "\nDo you want to save them?", "Unsaved changes", JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION){
                    setCurrentSettings(checkBoxDebugMode, checkBoxDisableSafety);WindowSettings.FRAME.dispose();
                    Backup.createInitialBackup();
                }
            }
            unsavedChanges = false;
            WindowMain.checkActionAvailability();
            WindowSettings.FRAME.dispose();
        });
        contentPane.add(btnBack);

        JButton btnResetSettings = new JButton("Reset Settings");
        btnResetSettings.setBounds(90, 132, 127, 23);
        btnResetSettings.setToolTipText("Click to reset the settings to default values.");
        btnResetSettings.addActionListener(actionEvent -> {
            if (JOptionPane.showConfirmDialog(null, "Are you sure?", "Reset Settings", JOptionPane.YES_NO_OPTION) == 0) {
                Settings.resetSettings();
                checkBoxDebugMode.setSelected(false);
                checkBoxDisableSafety.setSelected(false);
                customFolderSetAndValid = false;
                comboBoxMGT2FolderOperation.setSelectedItem("Automatic");
                unsavedChanges = false;
                JOptionPane.showMessageDialog(new Frame(), "Settings have been restored to default.");
            }

        });
        contentPane.add(btnResetSettings);

        JButton btnSave = new JButton("Save");
        btnSave.setBounds(230, 132, 89, 23);
        btnSave.setToolTipText("Click to save the current settings.");
        btnSave.addActionListener(actionEvent -> {
            unsavedChanges = false;
            if(checkBoxDisableSafety.isSelected()){
                String unsavedChanges = getChangesInSettings(checkBoxDebugMode, checkBoxDisableSafety);
                if(JOptionPane.showConfirmDialog(null, "Save the following settings?\n\n" + unsavedChanges, "Unsaved changes", JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION){
                    setCurrentSettings(checkBoxDebugMode, checkBoxDisableSafety);
                    WindowMain.checkActionAvailability();
                    Backup.createInitialBackup();
                }
            }else{
                setCurrentSettings(checkBoxDebugMode, checkBoxDisableSafety);
                Backup.createInitialBackup();
            }
        });
        contentPane.add(btnSave);
    }

    private void loadCurrentSelections(){
        if(Settings.enableCustomFolder){
            comboBoxMGT2FolderOperation.setModel(new DefaultComboBoxModel<>(new String[]{"Manual", "Automatic"}));
        }else{
            comboBoxMGT2FolderOperation.setModel(new DefaultComboBoxModel<>(new String[]{"Automatic", "Manual"}));
        }
        checkBoxDebugMode.setSelected(Settings.enableDebugLogging);
        checkBoxDisableSafety.setSelected(Settings.disableSafetyFeatures);
    }

    /**
     * Applies the local changes in the settings to the global settings by calling Settings.setSettings(...)
     * @param checkBoxDebugMode The debug mode checkbox
     * @param checkBoxDisableSafety The disable safety features checkbox
     */
    private static void setCurrentSettings(JCheckBox checkBoxDebugMode,JCheckBox checkBoxDisableSafety){
        Settings.setSettings(true, checkBoxDebugMode.isSelected(),checkBoxDisableSafety.isSelected(), customFolderSetAndValid, customFolderPath, Settings.enableAddGenreWarning, Settings.enableGenreNameTranslationInfo, Settings.enableGenreDescriptionTranslationInfo);
    }

    /**
     * @param checkBoxDebugMode The debug mode checkbox
     * @param checkBoxDisableSafety The disable safety features checkbox
     * @return Returns the changes that have been made to the settings
     */
    private static String getChangesInSettings(JCheckBox checkBoxDebugMode,JCheckBox checkBoxDisableSafety){
        String unsavedChanges = "";
        if(Settings.enableDebugLogging != checkBoxDebugMode.isSelected()){
            unsavedChanges = unsavedChanges + "Enable debug logging: " + Settings.enableDebugLogging + " -> " + checkBoxDebugMode.isSelected() + "\n";
        }
        if(Settings.disableSafetyFeatures != checkBoxDisableSafety.isSelected()){
            unsavedChanges = unsavedChanges + "Disable safety features: " + Settings.disableSafetyFeatures + " -> " + checkBoxDisableSafety.isSelected() + "\n";
        }
        if(!Settings.mgt2FilePath.equals(customFolderPath) && !customFolderPath.isEmpty() && !Settings.mgt2FilePath.isEmpty()){
            unsavedChanges = unsavedChanges + "Mad Games Tycoon folder: " + Settings.mgt2FilePath + " -> " + customFolderPath + "\n";
        }
        return unsavedChanges;
    }
}
