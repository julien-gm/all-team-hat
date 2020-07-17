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
            final JFileChooser csvFileChooser = new JFileChooser(
                    new File(preferences.get(LAST_USED_FILE, new File(".").getAbsolutePath())));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
            csvFileChooser.setFileFilter(filter);
            final Object[] message = { "List of players: ", csvFileChooser, "Number of teams:", numberOfTeams,
                    "Number of runs:", numberOfRuns };
            JOptionPane.showInputDialog(message);

            preferences.put(LAST_USED_FILE, csvFileChooser.getSelectedFile().getPath());

            File csvFile = csvFileChooser.getSelectedFile();
            FilePlayersParser playersParser = new FilePlayersParser(new FileReader(csvFile));
            TeamsGenerator teamsGenerator = playersParser.getTeamsGenerator();

            int nbTeams = numberOfTeams.getText().matches("[0-9]+") ? Integer.valueOf(numberOfTeams.getText()) : 6;
            int nbRuns = numberOfRuns.getText().matches("[0-9]+") ? Integer.valueOf(numberOfRuns.getText()) : 20;
            Composition bestComposition = teamsGenerator.computeBestComposition(nbTeams, nbRuns);
            System.out.println(bestComposition);

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }
}
