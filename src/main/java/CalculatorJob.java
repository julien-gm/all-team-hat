import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import computation.TeamsGenerator;
import domain.Composition;
import domain.Player;
import domain.Player.Gender;
import domain.Player.Handler;

public class CalculatorJob
{

	private final static String LAST_USED_FILE = "LAST_USED_FILE";

	public static void main(String[] args) throws IOException, ParseException
	{
		// TODO on s'en sert juste pour les tests
		if (false)
		{
			CommandLineParser commandParser = new DefaultParser();
			Options options = new Options();
			options.addOption("file", true, "file to parse");
			options.addOption("nbTeams", true, "number of team to create");
			options.addOption("nbRuns", true, "number of shuffle to run");
			CommandLine commandline = commandParser.parse(options, args, false);

			String file = commandline.getOptionValue("file", "players.csv");
			new File("src/main/resources/" + file);
			CSVParser parser = CSVParser.parse(new FileReader(file), CSVFormat.RFC4180.withHeader());
			List<Player> allPlayers = new ArrayList();

			for (CSVRecord record : parser)
			{
				// Reading with headers so we can get the values via the name
				// and they can be in any order (as long as the skills are last)
				Player player = new Player(record.get("Pseudo"));
				player.setLastName(record.get("Nom"));
				player.setFirstName(record.get("Prénom"));
				player.setEmail(record.get("Email"));
				player.setClub(record.get("Club"));
				player.setAge(Integer.parseInt(record.get("Age")));
				player.setGender(record.get("Sexe").startsWith("F") ? Gender.FEMME : Gender.HOMME);
				String handler = record.get("Handler");
				player.setHandler(
					handler.equals("oui") ? Handler.YES : handler.equals("non") ? Handler.NO : Handler.MAYBE);

				// Getting skills
				// Skipping the first 8 columns that we just read
				Iterator<String> iterator = record.iterator();
				for (int i = 0; i < 8; i++)
				{
					iterator.next();
				}
				while (iterator.hasNext())
				{
					player.getSkillsList().add(Double.parseDouble(iterator.next()));
				}

				allPlayers.add(player);
			}

			TeamsGenerator teamsGenerator = new TeamsGenerator(allPlayers);
			int nbTeams = Integer.valueOf(commandline.getOptionValue("nbTeams", "6"));
			int nbRuns = Integer.valueOf(commandline.getOptionValue("nbRuns", "20"));
			Composition bestComposition = teamsGenerator.computeBestComposition(nbTeams, nbRuns);
			System.out.println(bestComposition);
		}
		else
		{
			// User-friendly interface
			try
			{
				// Beauty
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

				// Retrieve users preferences (values at last use)
				final Preferences preferences = Preferences.userRoot().node(CalculatorJob.class.getName());

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
				new File("src/main/resources/" + csvFile);
				CSVParser parser = CSVParser.parse(new FileReader(csvFile), CSVFormat.RFC4180.withHeader());
				List<Player> allPlayers = new ArrayList<>();

				for (CSVRecord record : parser)
				{
					// Reading with headers so we can get the values via the
					// name
					// and they can be in any order (as long as the skills are
					// last)
					Player player = new Player(record.get("Pseudo"));
					player.setLastName(record.get("Nom"));
					player.setFirstName(record.get("Prénom"));
					player.setEmail(record.get("Email"));
					player.setClub(record.get("Club"));
					player.setAge(Integer.parseInt(record.get("Age")));
					player.setGender(record.get("Sexe").startsWith("F") ? Gender.FEMME : Gender.HOMME);
					String handler = record.get("Handler");
					player.setHandler(
						handler.equals("oui") ? Handler.YES : handler.equals("non") ? Handler.NO : Handler.MAYBE);

					// Getting skills
					// Skipping the first 8 columns that we just read
					Iterator<String> iterator = record.iterator();
					for (int i = 0; i < 8; i++)
					{
						iterator.next();
					}
					while (iterator.hasNext())
					{
						player.getSkillsList().add(Double.parseDouble(iterator.next()));
					}

					allPlayers.add(player);
				}
				TeamsGenerator teamsGenerator = new TeamsGenerator(allPlayers);
				int nbTeams = numberOfTeams.getText().matches("[0-9]+") ? Integer.valueOf(numberOfTeams.getText()) : 6;
				int nbRuns = numberOfRuns.getText().matches("[0-9]+") ? Integer.valueOf(numberOfRuns.getText()) : 20;
				Composition bestComposition = teamsGenerator.computeBestComposition(nbTeams, nbRuns);
				System.out.println(bestComposition);

			}
			catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e)
			{
				// TODO message
			}

		}
	}
}
