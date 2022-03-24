import computation.TeamsGenerator;
import domain.Composition;
import utils.FilePlayersParser;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.prefs.Preferences;

public class CalculatorUI {

    private final static String LAST_USED_FILE = "LAST_USED_FILE";

    public static void main(String[] args) throws IOException {
        // User-friendly interface
        try {
            // Beauty
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Retrieve users preferences (values at last use)
            final Preferences preferences = Preferences.userRoot().node(CalculatorUI.class.getName());

            final JTextField numberOfTeams = new JTextField();
            final JTextField numberOfRuns = new JTextField();
            final JTextField numberOfTeammateFailure = new JTextField();
            final JTextField invalidTeamPenaltyField = new JTextField();
            final JTextField numberOfSkillField = new JTextField();
            final JTextField firstSkillColField = new JTextField();
            final JFileChooser csvFileChooser = new JFileChooser(
                    new File(preferences.get(LAST_USED_FILE, new File(".").getAbsolutePath())));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
            csvFileChooser.setFileFilter(filter);
            final Object[] message = { "List of players: ", csvFileChooser, "Number of teams:", numberOfTeams,
                    "Number of runs:", numberOfRuns, "Teammate failure penalty: ", numberOfTeammateFailure,
                    "Invalid team penalty: ", invalidTeamPenaltyField, "Number of skills: ", numberOfSkillField,
                    "Skill first column: ", firstSkillColField };
            JOptionPane.showInputDialog(message);

            preferences.put(LAST_USED_FILE, csvFileChooser.getSelectedFile().getPath());

            File csvFile = csvFileChooser.getSelectedFile();

            int nbTeams = getTextFieldAsInt(numberOfTeams, 6);
            int nbRuns = getTextFieldAsInt(numberOfRuns, 20);
            int teammatePenalty = getTextFieldAsInt(numberOfTeammateFailure, 50);
            int invalidTeamPenalty = getTextFieldAsInt(invalidTeamPenaltyField, 200);
            int numberOfSkill = getTextFieldAsInt(numberOfSkillField, 3);
            int firstSkillCol = getTextFieldAsInt(firstSkillColField, 9);
            FilePlayersParser playersParser = new FilePlayersParser(new FileReader(csvFile), firstSkillCol,
                    numberOfSkill);
            TeamsGenerator teamsGenerator = playersParser.getTeamsGenerator();
            Composition bestComposition = teamsGenerator.computeBestComposition(nbTeams, nbRuns, invalidTeamPenalty,
                    teammatePenalty);
            System.out.println(bestComposition);

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    private static int getTextFieldAsInt(JTextField textField, int defaultValue) {
        return textField.getText().matches("[0-9]+") ? Integer.parseInt(textField.getText()) : defaultValue;
    }
}
