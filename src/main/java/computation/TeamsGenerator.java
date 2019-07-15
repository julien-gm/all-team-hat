package computation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import domain.Composition;
import domain.Player;
import domain.Team;

public class TeamsGenerator
{

	private static final int LOCAL_NUMBER_OF_OPTIMZATION = 50;
	private List<Player> players;

	public TeamsGenerator(List<Player> players)
	{
		this.players = players;
	}

	public int getNumberOfPlayersByTeam(int nbTeam)
	{
		return (int) Math.ceil(players.size() / (double) nbTeam);
	}

	public List<Team> initTeams(int nbTeams)
	{
		int numberOfPlayersByTeam = getNumberOfPlayersByTeam(nbTeams);
		List<Team> girlsTeams = new ArrayList<>(numberOfPlayersByTeam);
		List<Team> boysTeams = new ArrayList<>(numberOfPlayersByTeam);
		List<Team> teams = new ArrayList<>(numberOfPlayersByTeam);
		// Add nbTeams empty to each composition
		for (int i = 0; i < nbTeams; i++)
		{
			girlsTeams.add(new Team(new ArrayList<>()));
			boysTeams.add(new Team(new ArrayList<>()));
			teams.add(new Team(new ArrayList<>()));
		}
		int playerNumber = initTeam(nbTeams, girlsTeams, 0, Player.Gender.FEMME);
		initTeam(nbTeams, boysTeams, playerNumber, Player.Gender.HOMME);
		Composition girlsComposition = getLocalBestComposition(new Composition(girlsTeams, getTeamsCalculator(nbTeams)),
			1);
		Composition boysComposition = getLocalBestComposition(new Composition(boysTeams, getTeamsCalculator(nbTeams)),
			1);
		fillTeams(nbTeams, numberOfPlayersByTeam, teams, girlsComposition, boysComposition);
		return teams;
	}

	private int initTeam(int nbTeams, List<Team> boysTeams, int playerNumber, Player.Gender gender)
	{
		for (Player boy : getPlayersFromGender(gender))
		{
			Team playerTeam = boysTeams.get(playerNumber % nbTeams);
			playerNumber++;
			playerTeam.add(boy);
		}
		return playerNumber;
	}

	private void fillTeams(int nbTeams, int numberOfPlayersByTeam, List<Team> teams, Composition girlsComposition,
		Composition boysComposition)
	{
		int playerNumber;
		for (playerNumber = 0; playerNumber < nbTeams * numberOfPlayersByTeam; playerNumber++)
		{
			int numberOfGirls = girlsComposition.getNumberOfPlayers();
			if (playerNumber < numberOfGirls)
			{
				teams.get(playerNumber % nbTeams).add(girlsComposition.getPlayer(playerNumber));
			}
			else if (playerNumber < boysComposition.getNumberOfPlayers() + numberOfGirls)
			{
				teams.get(playerNumber % nbTeams).add(boysComposition.getPlayer(playerNumber - numberOfGirls));
			}
			else
			{
				teams.get(playerNumber % nbTeams).add(Team.fakePlayer);
			}
		}
	}

	private List<Player> getPlayersFromGender(Player.Gender gender)
	{
		return players.stream().filter(p -> p.getGender().equals(gender)).collect(Collectors.toList());
	}

	private Composition getLocalBestComposition(Composition currentComposition, int run)
	{
		double localBestScore;
		Composition localBestComposition = currentComposition;
		System.out.println("Initial score is: " + currentComposition.getScore());
		localBestScore = currentComposition.getScore();
		for (int optimizedNumber = 0; optimizedNumber < run; ++optimizedNumber)
		{
			currentComposition = getBestComposition(currentComposition);
			double score = currentComposition.getScore();
			if (score < localBestScore)
			{
				System.out.println("New local best score : " + score);
				localBestScore = score;
				localBestComposition = currentComposition;
			}
		}
		return localBestComposition;
	}

	public Composition computeBestComposition(int nbTeams, int nbShuffles)
	{
		List<Team> teams = initTeams(nbTeams);
		TeamsCalculator teamCalculator = getTeamsCalculator(nbTeams);
		Composition currentComposition = new Composition(teams, teamCalculator);
		System.out.println(currentComposition);
		double bestScore = currentComposition.getScore();
		Composition bestComposition = currentComposition;
		for (int tryNumber = 0; tryNumber < nbShuffles; ++tryNumber)
		{
			Composition localBestComposition = getLocalBestComposition(currentComposition, LOCAL_NUMBER_OF_OPTIMZATION);
			double score = localBestComposition.getScore();
			if (score < bestScore)
			{
				System.out.println("New best score ever: " + score);
				bestComposition = localBestComposition;
				bestScore = score;
			}
			currentComposition = currentComposition.shuffle();
		}
		return bestComposition;
	}

	private Composition getBestComposition(Composition currentComposition)
	{
		Composition bestComposition = currentComposition;
		List<Team> teams = currentComposition.getTeams();
		double localBestScore = bestComposition.getScore();
		for (Player player : players)
		{
			Team playerTeam = currentComposition.getTeamFromPlayer(player);
			for (Player switchPlayer : teams.stream().filter(t -> !t.equals(playerTeam))
				.flatMap(t -> t.getPlayers().stream()).collect(Collectors.toList()))
			{
				Composition newComposition = currentComposition.switchPlayer(player, switchPlayer);
				if (newComposition != null)
				{
					double newScore = newComposition.getScore();
					if (newScore < localBestScore)
					{
						localBestScore = newScore;
						bestComposition = newComposition;
					}
				}
			}
		}

		return bestComposition;
	}

	public TeamsCalculator getTeamsCalculator(int nbTeams)
	{
		List<Double> skillsAverage = getSkillAverages();
		long nbHandlers = getNbHandlers();
		long nbNoHandlers = getNbNoHandlers();
		double ageAverage = getAgeAverage();
		Map<String, Double> expectedClubScore = new HashMap<>();
		for (Map.Entry<String, List<Player>> entry : this.players.stream()
			.collect(Collectors.groupingBy(Player::getClub)).entrySet())
		{
			expectedClubScore.put(entry.getKey(), (double) entry.getValue().size() / nbTeams);
		}
		return new TeamsCalculator(skillsAverage, nbNoHandlers, nbHandlers, ageAverage, expectedClubScore);
	}

	public List<Double> getSkillAverages()
	{
		List<Double> averageScores = new ArrayList<>();
		int numberOfSkills = players.size() == 0 ? 0 : players.get(0).getSkillsList().size();
		// for (int i = 0; i < numberOfSkills; i++)
		// {
		// averageScores.add(getAverage(player ->
		// player.getSkillsList().get(i)));
		// }
		// TODO essayer de faire marcher ce qu'il y a au dessus ou faire un truc
		// mieux
		try
		{
			averageScores.add(getAverage(player -> player.getSkillsList().get(0)));
			averageScores.add(getAverage(player -> player.getSkillsList().get(1)));
			averageScores.add(getAverage(player -> player.getSkillsList().get(2)));
			averageScores.add(getAverage(player -> player.getSkillsList().get(3)));
			averageScores.add(getAverage(player -> player.getSkillsList().get(4)));
			averageScores.add(getAverage(player -> player.getSkillsList().get(5)));
			averageScores.add(getAverage(player -> player.getSkillsList().get(6)));
			averageScores.add(getAverage(player -> player.getSkillsList().get(7)));
			averageScores.add(getAverage(player -> player.getSkillsList().get(8)));
		}
		catch (Exception e)
		{
			// c'est normal de planter au bout d'un moment -> on fait rien.
		}
		return averageScores;

	}

	public double getAgeAverage()
	{
		return getAverage(Player::getAge);
	}

	private double getAverage(ToDoubleFunction<Player> function)
	{
		return players.stream().filter(Player::isReal).mapToDouble(function).average().orElse(0.0);
	}

	public long getNbGirls()
	{
		return players.stream().filter(player -> Player.Gender.FEMME.equals(player.getGender())).count();
	}

	public long getNbHandlers()
	{
		return getNbHandlersByKind(Player.Handler.YES);
	}

	public long getNbNoHandlers()
	{
		return getNbHandlersByKind(Player.Handler.NO);
	}

	private long getNbHandlersByKind(Player.Handler handling)
	{
		return players.stream().filter(player -> handling.equals(player.getHandler())).count();
	}
}
