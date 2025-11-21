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

            int DEFAULT_NUM_TEAMS = 4;
            int DEFAULT_NUM_RUN = 15;
            int DEFAULT_TEAMMATE_FAILURE_PENALTY = 50;
            int DEFAULT_INVALID_TEAM_PENALTY = 250;
            int DEFAULT_NUMBER_OF_SKILLS = 3;
            int DEFAULT_FIRST_SKILL_COL = 8;
            String DEFAULT_TEAMMATE_COL_NAME = "Binôme";

            final JTextField numberOfTeams = new JTextField(String.valueOf(DEFAULT_NUM_TEAMS));
            final JTextField numberOfRuns = new JTextField(String.valueOf(DEFAULT_NUM_RUN));
            final JTextField numberOfTeammateFailure = new JTextField(String.valueOf(DEFAULT_TEAMMATE_FAILURE_PENALTY));
            final JTextField invalidTeamPenaltyField = new JTextField(String.valueOf(DEFAULT_INVALID_TEAM_PENALTY));
            final JTextField numberOfSkillField = new JTextField(String.valueOf(DEFAULT_NUMBER_OF_SKILLS));
            final JTextField firstSkillColField = new JTextField(String.valueOf(DEFAULT_FIRST_SKILL_COL));

            final JFileChooser csvFileChooser = new JFileChooser(
                    new File(preferences.get(LAST_USED_FILE, new File(".").getAbsolutePath())));
            csvFileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));

            final Object[] message = { "List of players: ", csvFileChooser, "Number of teams:", numberOfTeams,
                    "Number of runs:", numberOfRuns, "Number of skills: ", numberOfSkillField, "Skill first column: ",
                    firstSkillColField, "teammate column name: " };
            String teamMateColName = JOptionPane.showInputDialog(message);

            preferences.put(LAST_USED_FILE, csvFileChooser.getSelectedFile().getPath());

            File csvFile = csvFileChooser.getSelectedFile();

            int nbTeams = getTextFieldAsInt(numberOfTeams, DEFAULT_NUM_TEAMS);
            int nbRuns = getTextFieldAsInt(numberOfRuns, DEFAULT_NUM_RUN);
            int teammatePenalty = getTextFieldAsInt(numberOfTeammateFailure, DEFAULT_TEAMMATE_FAILURE_PENALTY);
            int invalidTeamPenalty = getTextFieldAsInt(invalidTeamPenaltyField, DEFAULT_INVALID_TEAM_PENALTY);
            int numberOfSkill = getTextFieldAsInt(numberOfSkillField, DEFAULT_NUMBER_OF_SKILLS);
            int firstSkillCol = getTextFieldAsInt(firstSkillColField, DEFAULT_FIRST_SKILL_COL);
            FilePlayersParser playersParser = new FilePlayersParser(new FileReader(csvFile), firstSkillCol,
                    numberOfSkill, teamMateColName);
            TeamsGenerator teamsGenerator = playersParser.getTeamsGenerator();
            Composition bestComposition = teamsGenerator.computeBestComposition(nbTeams, nbRuns, invalidTeamPenalty,
                    teammatePenalty);
            playersParser.write(bestComposition);
            System.out.println(bestComposition.getScoreForDay(1));
            System.out.println(bestComposition.getScoreForDay(2));

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    private static int getTextFieldAsInt(JTextField textField, int defaultValue) {
        return textField.getText().matches("[0-9]+") ? Integer.parseInt(textField.getText()) : defaultValue;
    }
}
