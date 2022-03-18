package computation;

import domain.Composition;
import domain.Player;
import domain.Team;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

public class TeamsGenerator {

    private static final int LOCAL_NUMBER_OF_OPTIMIZATION = 50;
    private List<Player> players;

    public TeamsGenerator(List<Player> players) {
        this.players = players;
    }

    public int getNumberOfPlayersByTeam(int nbTeam) {
        return (int) Math.ceil(players.size() / (double) nbTeam);
    }

    public int getExpectedPlayersByTeam(int nbTeam) {
        long nbPlayerDay1 = players.stream().filter(p -> p.playsTheSameDay(1)).count();
        long nbPlayerDay2 = players.stream().filter(p -> p.playsTheSameDay(2)).count();
        return (int) Math.ceil(Math.max(nbPlayerDay1, nbPlayerDay2) / (double) nbTeam);
    }

    public List<Team> initTeams(int nbTeams) {
        int numberOfPlayersByTeam = getNumberOfPlayersByTeam(nbTeams);
        List<Team> girlsTeams = new ArrayList<>(numberOfPlayersByTeam);
        List<Team> boysTeams = new ArrayList<>(numberOfPlayersByTeam);
        List<Team> teams = new ArrayList<>(numberOfPlayersByTeam);
        // Add nbTeams empty to each composition
        for (int i = 0; i < nbTeams; i++) {
            girlsTeams.add(new Team(new ArrayList<>()));
            boysTeams.add(new Team(new ArrayList<>()));
            teams.add(new Team(new ArrayList<>()));
        }
        int playerNumber = initTeam(nbTeams, girlsTeams, 0, Player.Gender.FEMME);
        initTeam(nbTeams, boysTeams, playerNumber, Player.Gender.HOMME);
        Composition girlsCompo = new Composition(girlsTeams);
        Composition boysCompos = new Composition(boysTeams);
        fillTeams(nbTeams, numberOfPlayersByTeam, teams, girlsCompo, boysCompos);
        return teams;
    }

    private int initTeam(int nbTeams, List<Team> boysTeams, int playerNumber, Player.Gender gender) {
        for (Player boy : getPlayersFromGender(gender)) {
            Team playerTeam = boysTeams.get(playerNumber % nbTeams);
            playerNumber++;
            playerTeam.add(boy);
        }
        return playerNumber;
    }

    private void fillTeams(int nbTeams, int numberOfPlayersByTeam, List<Team> teams, Composition girlsComposition,
            Composition boysComposition) {
        int playerNumber;
        int numberOfGirls = girlsComposition.getNumberOfPlayers();
        for (playerNumber = 0; playerNumber < nbTeams * (numberOfPlayersByTeam + 1); playerNumber++) {
            if (playerNumber < numberOfGirls) {
                teams.get(playerNumber % nbTeams).add(girlsComposition.getPlayer(playerNumber));
            } else if (playerNumber < boysComposition.getNumberOfPlayers() + numberOfGirls) {
                teams.get(playerNumber % nbTeams).add(boysComposition.getPlayer(playerNumber - numberOfGirls));
            } else {
                teams.get(playerNumber % nbTeams).add(Team.fakePlayer);
            }
        }
    }

    private List<Player> getPlayersFromGender(Player.Gender gender) {
        return players.stream().filter(p -> p.getGender().equals(gender))
                .sorted(Comparator.comparingInt(Player::getRevertDay)).collect(Collectors.toList());
    }

    private Composition getLocalBestComposition(Composition currentComposition) {
        double localBestScore;
        Composition localBestComposition = currentComposition;
        localBestScore = currentComposition.getScore();
        for (int optimizedNumber = 0; optimizedNumber < LOCAL_NUMBER_OF_OPTIMIZATION; ++optimizedNumber) {
            System.out.print('.');
            currentComposition = getBestComposition(currentComposition);
            if (currentComposition != null) {
                double score = currentComposition.getScore();
                if (score < localBestScore) {
                    localBestScore = score;
                    localBestComposition = currentComposition;
                }
            } else {
                System.out.print(
                        String.format("\nNo more valid composition for this run after %d tries", optimizedNumber));
                break;
            }
        }
        System.out.println(String.format("\nNew local best score : %.2f", localBestScore));
        return localBestComposition;
    }

    public Composition computeBestComposition(int nbTeams, int nbShuffles, int invalidTeamPenalty,
            int teammatePenalty) {
        List<Team> teams = initTeams(nbTeams);
        TeamsCalculator teamCalculator = getTeamsCalculator(nbTeams);
        Composition currentComposition = new Composition(teams, teamCalculator, invalidTeamPenalty, teammatePenalty)
                .shuffle();
        double bestScore = currentComposition.getScore();
        Composition bestComposition = currentComposition;
        for (int tryNumber = 0; tryNumber < nbShuffles; ++tryNumber) {
            System.out.println(
                    String.format("Run number #%d, initial score: %.2f", tryNumber + 1, currentComposition.getScore()));
            Composition localBestComposition = getLocalBestComposition(currentComposition);
            double score = localBestComposition.getScore();
            if (score < bestScore) {
                System.out.println(String.format("New best score ever: %.2f", score));
                bestComposition = localBestComposition;
                bestScore = score;
            }
            currentComposition = currentComposition.shuffle();
        }
        return bestComposition;
    }

    private Composition getBestComposition(Composition currentComposition) {
        Composition bestComposition = null;
        List<Team> teams = currentComposition.getTeams();
        double localBestScore = currentComposition.getScore();
        for (Player player : players) {
            Team playerTeam = currentComposition.getTeamFromPlayer(player);
            for (Player switchPlayer : teams.stream().filter(t -> !t.equals(playerTeam))
                    .flatMap(t -> t.getPlayers().stream()).collect(Collectors.toList())) {
                Composition newComposition = currentComposition.switchPlayer(player, switchPlayer);
                if (newComposition != null) {
                    double newScore = newComposition.getScore();
                    if (newScore < localBestScore) {
                        localBestScore = newScore;
                        bestComposition = newComposition;
                    }
                }
            }
        }

        return bestComposition;
    }

    public TeamsCalculator getTeamsCalculator(int nbTeams) {
        List<Double> skillsAverage = getSkillAverages();
        long nbHandlers = getNbHandlers();
        long nbNoHandlers = getNbNoHandlers();
        long nbMaybeHandlers = getNbMaybeHandlers();
        Map<String, Double> expectedClubScore = new HashMap<>();
        for (Map.Entry<String, List<Player>> entry : this.players.stream().filter(p -> p.playsTheSameDay(1))
                .collect(Collectors.groupingBy(Player::getClub)).entrySet()) {
            expectedClubScore.put(entry.getKey(), Math.ceil((double) entry.getValue().size() / nbTeams) + 1);
        }
        return new TeamsCalculator(skillsAverage, nbNoHandlers, nbHandlers, nbMaybeHandlers, expectedClubScore,
                getExpectedPlayersByTeam(nbTeams), getExpectedStdDev());
    }

    private double getExpectedStdDev() {
        double skillAverage = getAverage(Player::getSkillAverage);
        return players.stream().filter(Player::isReal).mapToDouble(p -> skillAverage - p.getSkillAverage()).average()
                .orElse(0.0);
    }

    public List<Double> getSkillAverages() {
        List<Player> realPlayers = players.stream().filter(Player::isReal).collect(Collectors.toList());
        Player firstRealPlayer = realPlayers.get(0);
        List<Double> averageScores = new ArrayList<>();
        if (firstRealPlayer != null) {
            double[] skillAverage = new double[firstRealPlayer.getSkillsList().size()];
            for (Player player : realPlayers) {
                int skillIndex = 0;
                for (double skill : player.getSkillsList()) {
                    skillAverage[skillIndex] += skill;
                    skillIndex++;
                }
            }
            long nbRealPlayers = realPlayers.size();
            for (double skillSum : skillAverage) {
                averageScores.add(skillSum / nbRealPlayers);
            }
        }
        return averageScores;
    }

    public List<Double> getSkillStdDev() {
        List<Player> realPlayers = players.stream().filter(Player::isReal).collect(Collectors.toList());
        Player firstRealPlayer = realPlayers.get(0);
        List<Double> averagesScores = getSkillAverages();
        List<Double> stdDevScores = new ArrayList<>();
        if (firstRealPlayer != null) {
            double[] skillScoreDistance = new double[firstRealPlayer.getSkillsList().size()];
            for (Player player : realPlayers) {
                int skillIndex = 0;
                for (double skill : player.getSkillsList()) {
                    skillScoreDistance[skillIndex] += Math.abs(skill - averagesScores.get(skillIndex));
                    skillIndex++;
                }
            }
            for (double skillStdDev : skillScoreDistance) {
                stdDevScores.add(skillStdDev);
            }
        }
        return stdDevScores;
    }

    public double getAgeAverage() {
        return getAverage(Player::getAge);
    }

    private double getAverage(ToDoubleFunction<Player> function) {
        return players.stream().filter(Player::isReal).mapToDouble(function).average().orElse(0.0);
    }

    public long getNbGirls() {
        return players.stream().filter(player -> Player.Gender.FEMME.equals(player.getGender())).count();
    }

    public long getNbHandlers() {
        return getNbHandlersByKind(Player.Handler.YES);
    }

    public long getNbNoHandlers() {
        return getNbHandlersByKind(Player.Handler.NO);
    }

    private long getNbHandlersByKind(Player.Handler handling) {
        return players.stream().filter(player -> handling.equals(player.getHandler())).count();
    }

    public long getNbMaybeHandlers() {
        return getNbHandlersByKind(Player.Handler.MAYBE);
    }

    public long getNbPlayers() {
        return players.stream().filter(Player::isReal).count();
    }

    public double getSkillsAverage() {
        return players.stream().mapToDouble(Player::getSkillAverage).average().orElse(0.0);
    }

    public double getSkillsStdDev() {
        double teamSportAverage = getSkillsAverage();
        return players.stream().filter(Player::isReal)
                .mapToDouble(player -> Math.abs(teamSportAverage - player.getSkillAverage())).average().orElse(0);
    }
}
