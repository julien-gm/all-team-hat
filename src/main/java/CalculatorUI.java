import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

import computation.TeamsGenerator;
import domain.Composition;
import utils.PlayersParser;

public class CalculatorUI
{

	private final static String LAST_USED_FILE = "LAST_USED_FILE";

	public static void main(String[] args) throws IOException
	{
		// User-friendly interface
		try
		{
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
			PlayersParser playersParser = new PlayersParser(new FileReader(csvFile));
			TeamsGenerator teamsGenerator = playersParser.getTeamsGeneratorFromFile();

			int nbTeams = numberOfTeams.getText().matches("[0-9]+") ? Integer.valueOf(numberOfTeams.getText()) : 8;
			int nbRuns = numberOfRuns.getText().matches("[0-9]+") ? Integer.valueOf(numberOfRuns.getText()) : 5;
			Composition bestComposition = teamsGenerator.computeBestComposition(nbTeams, nbRuns);
			System.out.println(bestComposition);

			PrintWriter writer = new PrintWriter(csvFile.getParent() + "\\" + "teams.txt", "UTF-8");
			writer.println(bestComposition);
			writer.close();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e)
		{
			System.err.println("An error occurred: " + e.getMessage());
		}
	}
}
