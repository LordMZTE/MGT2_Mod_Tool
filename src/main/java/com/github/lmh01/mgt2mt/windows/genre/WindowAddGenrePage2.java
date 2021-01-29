package com.github.lmh01.mgt2mt.windows.genre;

import com.github.lmh01.mgt2mt.util.NewGenreManager;
import com.github.lmh01.mgt2mt.windows.WindowAddNewGenre;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

public class WindowAddGenrePage2 extends JFrame{
    static WindowAddGenrePage2 frame = new WindowAddGenrePage2();
    private static final Logger LOGGER = LoggerFactory.getLogger(WindowAddGenrePage2.class);
    public static void createFrame(){
        EventQueue.invokeLater(() -> {
            try {
                frame.setVisible(true);
                frame.setLocationRelativeTo(null);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    public WindowAddGenrePage2() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 335, 160);
        setResizable(false);
        setTitle("[Page 2] Date");

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        JLabel labelGenreUnlockDate = new JLabel("Select the unlock date");
        labelGenreUnlockDate.setBounds(100, 5, 130, 23);
        contentPane.add(labelGenreUnlockDate);

        JLabel labelGenreUnlockMonth = new JLabel("Month: ");
        labelGenreUnlockMonth.setBounds(10, 35, 120, 23);
        contentPane.add(labelGenreUnlockMonth);

        JComboBox<String> comboBoxGenreUnlockMonth = new JComboBox<>();
        comboBoxGenreUnlockMonth.setBounds(120, 35, 100, 23);
        comboBoxGenreUnlockMonth.setToolTipText("This is the month when your genre will be unlocked.");
        comboBoxGenreUnlockMonth.setModel(new DefaultComboBoxModel<>(new String[]{"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"}));
        contentPane.add(comboBoxGenreUnlockMonth);

        JLabel labelGenreID = new JLabel("Unlock year: ");
        labelGenreID.setBounds(10, 60, 120, 23);
        contentPane.add(labelGenreID);

        JSpinner spinnerUnlockYear = new JSpinner();
        spinnerUnlockYear.setBounds(120, 60, 100, 23);
        spinnerUnlockYear.setToolTipText("This is the year when your genre will be unlocked.");
        spinnerUnlockYear.setModel(new SpinnerNumberModel(NewGenreManager.unlockYear, 1976, 2050, 1));
        contentPane.add(spinnerUnlockYear);

        JButton buttonNext = new JButton("Next");
        buttonNext.setBounds(220, 100, 100, 23);
        buttonNext.setToolTipText("Click to continue to the next step.");
        buttonNext.addActionListener((ignored) -> {
            saveInputs(spinnerUnlockYear, comboBoxGenreUnlockMonth);
            NewGenreManager.openStepWindow(3);
            frame.dispose();
        });
        contentPane.add(buttonNext);

        JButton buttonPrevious = new JButton("Previous");
        buttonPrevious.setBounds(10, 100, 100, 23);
        buttonPrevious.setToolTipText("Click to return to the previous page.");
        buttonPrevious.addActionListener((ignored) -> {
            saveInputs(spinnerUnlockYear, comboBoxGenreUnlockMonth);
            NewGenreManager.openStepWindow(1);
            frame.dispose();

        });
        contentPane.add(buttonPrevious);

        JButton buttonQuit = new JButton("Quit");
        buttonQuit.setBounds(120, 100, 90, 23);
        buttonQuit.setToolTipText("Click to quit this step by step guide and return to the add genre page.");
        buttonQuit.addActionListener((ignored) -> {
            if(JOptionPane.showConfirmDialog(null, "Are you sure?\nYour progress will be lost.", "Cancel add new genre", JOptionPane.YES_NO_OPTION) == 0){
                WindowAddNewGenre.createFrame();
                frame.dispose();
            }
        });
        contentPane.add(buttonQuit);
    }
    private static void saveInputs(JSpinner spinnerUnlockYear, JComboBox<String> comboBoxGenreUnlockMonth){
        NewGenreManager.unlockYear = Integer.parseInt(spinnerUnlockYear.getValue().toString());
        LOGGER.info("genre unlock year: " +  Integer.parseInt(spinnerUnlockYear.getValue().toString()));
        NewGenreManager.unlockMonth = Objects.requireNonNull(comboBoxGenreUnlockMonth.getSelectedItem()).toString();
        LOGGER.info("Genre unlock month: " + comboBoxGenreUnlockMonth.getSelectedItem().toString());
    }
}
