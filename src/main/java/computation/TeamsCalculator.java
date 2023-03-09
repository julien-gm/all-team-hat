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

    public final List<Double> expectedNumberOfPlayersForDays;

    private final double expectedStdDev;

    private final double expectedAge;

    private final Map<String, Double> expectedClubsScore;

    TeamsCalculator(List<Double> pExpectedScores, double expectedNumberOfNoHandlers, double expectedNumberOfHandlers,
            double expectedNumberOfMaybeHandlers, Map<String, Double> expectedClubScore,
            List<Double> expectedNumberOfPlayersForDays, double expectedStdDev, double expectedAge) {
        expectedScores = pExpectedScores;
        this.expectedNumberOfNoHandlers = expectedNumberOfNoHandlers;
        this.expectedNumberOfHandlers = expectedNumberOfHandlers;
        this.expectedNumberOfMixedHandlers = expectedNumberOfMaybeHandlers;
        this.expectedClubsScore = expectedClubScore;
        this.expectedNumberOfPlayersForDays = expectedNumberOfPlayersForDays;
        this.expectedStdDev = expectedStdDev;
        this.expectedAge = expectedAge;
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
                + team.getHandlerScore(expectedNumberOfHandlers) + team.getStandardDeviationScore(expectedStdDev)
                + team.getAgeScore(expectedAge);
    }

    public double compute(List<Team> teams, double invalidTeamPenalty) {
        double scoreDayOne = teams.stream().mapToDouble(t -> computeForDay(teams, 1)).min().orElse(0);
        double scoreDayTwo = teams.stream().mapToDouble(t -> computeForDay(teams, 2)).min().orElse(0);
        return (isValid(teams) ? 0 : invalidTeamPenalty) + scoreDayOne + scoreDayTwo
                + (2 * Math.abs(scoreDayTwo - scoreDayOne));
    }

    public boolean isValid(List<Team> teams) {
        for (int day = 1; day <= 2; day++) {
            if (!numberOfPlayersPerTeamIsValidForDay(day, teams) || !numberOfClubsPerTeamIsValidForDay(day, teams)) {
                return false;
            }
        }
        return true;
    }

    public double computeForDay(List<Team> teams, int day) {
        return teams.stream().mapToDouble(t -> this.getTeamScoreByDay(t, day)).sum();
    }

    private Team filterTeamForDay(Team team, int day) {
        return new Team(team.getPlayers().stream().filter(p -> p.playsTheSameDay(day)).collect(Collectors.toList()));
    }

    public boolean numberOfPlayersPerTeamIsValidForDay(int day, List<Team> teams) {
        double numberOfPlayers = expectedNumberOfPlayersForDays.get(day - 1);
        for (Team team : teams) {
            int nbPlayers = team.getPlayersForDay(day).size();
            if (nbPlayers < Math.floor(numberOfPlayers) || nbPlayers > Math.ceil(numberOfPlayers)) {
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
