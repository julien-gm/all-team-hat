package computation;

import domain.Team;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TeamsCalculator {

    private final List<Double> expectedScores;

    private final double expectedNumberOfNoHandlers;

    private final double expectedNumberOfHandlers;

    private final double expectedNumberOfMixedHandlers;

    public final double expectedNumberOfPlayers;

    private final double expectedStdDev;

    private final Map<String, Double> expectedClubsScore;

    TeamsCalculator(List<Double> pExpectedScores, double expectedNumberOfNoHandlers, double expectedNumberOfHandlers,
            double expectedNumberOfMaybeHandlers, Map<String, Double> expectedClubScore, double expectedNumberOfPlayers,
            double expectedStdDev) {
        expectedScores = pExpectedScores;
        this.expectedNumberOfNoHandlers = expectedNumberOfNoHandlers;
        this.expectedNumberOfHandlers = expectedNumberOfHandlers;
        this.expectedNumberOfMixedHandlers = expectedNumberOfMaybeHandlers;
        this.expectedClubsScore = expectedClubScore;
        this.expectedNumberOfPlayers = expectedNumberOfPlayers;
        this.expectedStdDev = expectedStdDev;
    }

    public double getTeamScore(Team team) {
        if (team.getDays().size() == 1) {
            return getTeamScoreByDay(team, 0);
        }
        double scoreDay1 = getTeamScoreByDay(team, 1);
        double scoreDay2 = getTeamScoreByDay(team, 2);
        return ((scoreDay1 + scoreDay2) / 2) + (2 * Math.abs(scoreDay1 - scoreDay2));
    }

    public double getTeamScoreByDay(Team globalTeam, int day) {
        Team team = filterTeamForDay(globalTeam, day);
        return team.getSkillsScore(expectedScores) + team.getNoHandlerScore(expectedNumberOfNoHandlers)
                + team.getMixedHandlerScore(expectedNumberOfHandlers + expectedNumberOfMixedHandlers / 2)
                + team.getHandlerScore(expectedNumberOfHandlers) + team.getStandardDeviationScore(expectedStdDev);
    }

    public double compute(List<Team> teams) {
        double sumScore = 0;
        for (int day = 1; day <= 2; day++) {
            int finalDay = day;
            sumScore += teams.stream().mapToDouble(t -> computeForDay(teams, finalDay)).min().orElse(0);
        }
        return sumScore;
    }

    public double computeForDay(List<Team> teams, int day) {
        return teams.stream().mapToDouble(t -> this.getTeamScoreByDay(t, day)).sum();
    }

    private Team filterTeamForDay(Team team, int day) {
        return new Team(team.getPlayers().stream().filter(p -> p.playsTheSameDay(day)).collect(Collectors.toList()));
    }

    public boolean numberOfPlayersPerTeamIsValidForDay(int day, List<Team> teams) {
        for (Team team : teams) {
            int nbPlayers = team.getPlayersForDay(day).size();
            if (nbPlayers < (expectedNumberOfPlayers - 1) || nbPlayers > expectedNumberOfPlayers) {
                return false;
            }
            if (nbPlayers <= (team.getRealPlayers().size() - 2)) {
                return false;
            }
        }
        return true;
    }

    public boolean numberOfClubsPerTeamIsValidForDay(int day, List<Team> teams) {
        int nbException = 0;
        int maxException = 1;
        for (Team team : teams) {
            for (Map.Entry<String, Double> expectedClubScore : expectedClubsScore.entrySet()) {
                String clubName = expectedClubScore.getKey();
                double nbPlayersForClub = expectedClubScore.getValue();
                double nbPlayersForThisClubForDay = team.getPlayersForDay(day).stream()
                        .filter(p -> p.getClub().equals(clubName)).count();

                if (nbPlayersForThisClubForDay >= nbPlayersForClub) {
                    nbException += (nbPlayersForThisClubForDay - nbPlayersForClub);
                    if (nbException > maxException) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public double getClubScore(List<Team> teams) {
        double score = 0;
        for (Team t : teams) {
            score += t.getClubScore(this.expectedClubsScore);
        }
        return score;
    }
}
