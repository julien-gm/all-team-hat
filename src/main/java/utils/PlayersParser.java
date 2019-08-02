package utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import computation.TeamsGenerator;
import domain.Player;

public class PlayersParser
{
	private static final List<String> VALUE_NAMES_PSEUDO = Arrays.asList("Pseudo", "pseudo", "PSEUDO", "Nickname",
			"nickname", "NICKNAME", "Surnom", "surnom", "SURNOM");
	private static final List<String> VALUE_NAMES_LAST_NAME = Arrays.asList("Last Name", "Last name", "last name",
			"LAST NAME", "Nom De Famille", "Nom de Famille", "Nom de famille", "nom de famille", "NOM DE FAMILLE",
			"Nom", "nom", "NOM");
	private static final List<String> VALUE_NAMES_FIRST_NAME = Arrays.asList("First Name", "First name", "first name",
			"FIRST NAME", "Prénom", "prénom", "Prenom", "prénom", "PRENOM", "PRÉNOM");
	private static final List<String> VALUE_NAMES_EMAIL = Arrays.asList("Email", "email", "EMAIL", "E-mail", "e-mail",
			"E-MAIL");
	private static final List<String> VALUE_NAMES_CLUB = Arrays.asList("Club", "club", "CLUB");
	private static final List<String> VALUE_NAMES_AGE = Arrays.asList("Age", "age", "AGE", "Âge", "âge", "ÂGE");
	private static final List<String> VALUE_NAMES_GENDER = Arrays.asList("Gender", "gender", "GENDER", "Genre", "genre",
			"GENRE", "Sexe", "sexe", "SEXE");
	private static final List<String> VALUE_NAMES_HANDLER = Arrays.asList("Handler", "handler", "HANDLER");

	private static Iterable<? extends CSVRecord> parser;

	private int numberOfColumnsToSkip;

	public PlayersParser(FileReader file) throws IOException
	{
		parser = CSVParser.parse(file, CSVFormat.RFC4180.withHeader());
	}

	public TeamsGenerator getTeamsGeneratorFromFile()
	{
		List<Player> allPlayers = new ArrayList<>();

		for (CSVRecord record : parser)
		{
			// Reset
			numberOfColumnsToSkip = 0;

			// Reading with headers so we can get the values via the name
			// and they can be in any order (as long as the skills are last)
			Player player = new Player(parseValueByName(record, VALUE_NAMES_PSEUDO));
			player.setLastName(parseValueByName(record, VALUE_NAMES_LAST_NAME));
			player.setFirstName(parseValueByName(record, VALUE_NAMES_FIRST_NAME));
			player.setEmail(parseValueByName(record, VALUE_NAMES_EMAIL));
			player.setClub(parseValueByName(record, VALUE_NAMES_CLUB));
			player.setAge(defaultValue(parseValueByName(record, VALUE_NAMES_AGE)));
			player.setGender(parseGender(parseValueByName(record, VALUE_NAMES_GENDER)));
			player.setHandler(parseHandler(parseValueByName(record, VALUE_NAMES_HANDLER)));

			// Getting skills
			// Skipping the first columns that we just read
			Iterator<String> iterator = record.iterator();
			for (int i = 0; i < numberOfColumnsToSkip; i++)
			{
				iterator.next();
			}
			while (iterator.hasNext())
			{
				player.getSkillsList().add(Double.parseDouble(iterator.next()));
			}
			allPlayers.add(player);
		}
		return new TeamsGenerator(allPlayers);
	}

	/**
	 * Tries to parse a value based on a list of similar names. Having a large
	 * list of names for a same values allows more flexibility on the input csv
	 * 
	 * @param pValueNamesList
	 * @return the value if found, null otherwise
	 */
	private String parseValueByName(CSVRecord pCSVRecord, List<String> pValueNamesList)
	{
		for (String valueName : pValueNamesList)
		{
			try
			{
				String value = pCSVRecord.get(valueName);
				numberOfColumnsToSkip++;
				return value;
			} catch (IllegalArgumentException e)
			{
				// We do nothing
			}
		}
		return null;
	}

	private Player.Handler parseHandler(String pHandler)
	{
		if (pHandler.startsWith("n") || pHandler.startsWith("N"))
		{
			return Player.Handler.NO;
		} else if (pHandler.startsWith("y") || pHandler.startsWith("Y") || pHandler.startsWith("o")
				|| pHandler.startsWith("O"))
		{
			return Player.Handler.YES;
		} else
		{
			return Player.Handler.MAYBE;
		}
	}

	private int defaultValue(String pString)
	{
		return pString == null || pString.isEmpty() ? 0 : Integer.parseInt(pString);
	}

	private Player.Gender parseGender(String pGender)
	{
		return pGender.startsWith("F") || pGender.startsWith("f") ? Player.Gender.FEMME : Player.Gender.HOMME;
	}
}
