package domain;

import computation.TeamsCalculator;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Composition {

    private int teammatePenalty = 50;

    private int invalidTeamPenalty = 200;

    private TeamsCalculator teamCalculator = null;

    private final List<Team> teams;

    private final double score;

    public Composition(List<Team> teams) {
        this.teams = teams;
        score = 0;
    }

    public Composition(List<Team> teams, TeamsCalculator teamCalculator, int invalidTeamPenalty, int teammatePenalty) {
        this.teams = teams;
        this.teamCalculator = teamCalculator;
        this.invalidTeamPenalty = invalidTeamPenalty;
        this.teammatePenalty = teammatePenalty;
        score = teamCalculator.compute(teams);
    }

    public double getScore() {
        int teamMateScore = 0;
        double clubScore = 20 * teamCalculator.getClubScore(teams);
        for (Team t : teams) {
            teamMateScore += t.getTeamMateScore(teammatePenalty);
        }
        return score + (isValid() ? 0 : invalidTeamPenalty) + teamMateScore + clubScore;
    }

    public Team getTeamFromPlayer(Player player) {
        return teams.stream().filter(team -> team.hasPlayer(player)).findFirst().orElse(null);
    }

    public List<Team> getTeams() {
        return teams;
    }

    public Composition switchPlayer(Player player1, Player player2) {
        if (!player1.getGender().equals(player2.getGender())) {
            return new Composition(teams, teamCalculator, invalidTeamPenalty, teammatePenalty);
        }
        final Team team1 = getTeamPlayer(player1);
        final Team team2 = getTeamPlayer(player2);
        if ((!player1.isReal() && (team2.hasPlayer(Team.fakePlayer)))
                || (!player2.isReal() && (team1.hasPlayer(Team.fakePlayer)))) {
            return new Composition(teams, teamCalculator, invalidTeamPenalty, teammatePenalty);
        }
        List<Team> teams = this.teams.stream().filter(t -> !t.equals(team1) && !t.equals(team2))
                .collect(Collectors.toList());
        if (team1 != null && team2 != null && !team1.equals(team2)) {
            List<Player> playersTeam1 = getAllTheOthersPlayersFromThisTeam(player1, team1);
            playersTeam1.add(player2);
            Team newTeam1 = new Team(playersTeam1);
            List<Player> playersTeam2 = getAllTheOthersPlayersFromThisTeam(player2, team2);
            playersTeam2.add(player1);
            Team newTeam2 = new Team(playersTeam2);
            teams.add(newTeam1);
            teams.add(newTeam2);
            return new Composition(teams, teamCalculator, invalidTeamPenalty, teammatePenalty);
        }
        if (team1 != null) {
            teams.add(team1);
        } else {
            teams.add(team2);
        }
        return new Composition(teams, teamCalculator, invalidTeamPenalty, teammatePenalty);
    }

    private Team getTeamPlayer(Player player1) {
        return this.teams.stream().filter(t -> t.getPlayers().contains(player1)).findFirst().orElse(null);
    }

    private List<Player> getAllTheOthersPlayersFromThisTeam(Player player, Team team) {
        final boolean[] found = { false };
        return team.getPlayers().stream().filter(p -> {
            if (!found[0] && p.equals(player)) {
                found[0] = true;
                return false;
            }
            return true;
        }).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(String.format("Score obtenu : %.2f\n", getScore()));
        int teamNumber = 1;
        for (Team team : getTeams()) {
            result.append(String.format(
                    "###########################\nteam %d\n%s - score: [%.2f]\n###########################\n",
                    teamNumber, team, teamCalculator.getTeamScore(team)));
            teamNumber++;
            int playerNumber = 1;
            for (Player player : team.getPlayers()) {
                if (player.isReal()) {
                    result.append(String.format("-%2d. %s\n", playerNumber, player));
                    playerNumber++;
                }
            }
        }
        return result.toString();
    }

    public Composition shuffle() {
        Composition tmpComposition = new Composition(teams, teamCalculator, invalidTeamPenalty, teammatePenalty);
        int nbRun = 0;
        while (nbRun < (getNumberOfPlayers() * 2)) {
            Player p1 = getRandomPlayer();
            Player p2 = getRandomPlayer();
            tmpComposition = tmpComposition.switchPlayer(p1, p2);
            nbRun++;
        }

        return tmpComposition;
    }

    private Player getRandomPlayer() {
        Random r = new Random();
        Supplier<Stream<Player>> playerStream = () -> this.teams.stream().flatMap(t -> t.getPlayers().stream());
        long randomIndex = r.nextInt((int) playerStream.get().count());
        return playerStream.get().skip(randomIndex).findAny().orElse(null);
    }

    public int getNumberOfPlayers() {
        return (int) this.teams.stream().mapToLong(t -> t.getRealPlayers().size()).sum();
    }

    public Player getPlayer(int playerNumber) {
        Supplier<Stream<Player>> playerStream = () -> this.teams.stream().flatMap(t -> t.getPlayers().stream());
        return playerStream.get().skip(playerNumber).findFirst().orElse(null);
    }

    public double getScoreForDay(int day) {
        return teamCalculator.computeForDay(teams, day);
    }

    public boolean isValid() {
        for (int day = 1; day <= 2; day++) {
            if (!teamCalculator.numberOfPlayersPerTeamIsValidForDay(day, teams)
                    || !teamCalculator.numberOfClubsPerTeamIsValidForDay(day, teams)) {
                return false;
            }
        }
        return true;
    }
}
