package computation;

import domain.Team;

import java.util.List;
import java.util.Map;

public class TeamsCalculator {

    private final List<Double> expectedScores;

    private final double expectedNumberOfNoHandlers;

    private final double expectedNumberOfHandlers;

    private final double expectedAgeAverage;

    private final Map<String, Double> expectedClubsScore;

    TeamsCalculator(List<Double> pExpectedScores, double expectedNumberOfNoHandlers, double expectedNumberOfHandlers,
                    double expectedAgeAverage, Map<String, Double> expectedClubScore) {
        expectedScores = pExpectedScores;
        this.expectedNumberOfNoHandlers = expectedNumberOfNoHandlers;
        this.expectedNumberOfHandlers = expectedNumberOfHandlers;
        this.expectedAgeAverage = expectedAgeAverage;
        this.expectedClubsScore = expectedClubScore;
    }

    public double getTeamScore(Team team) {
        return team.getSportScore(expectedScores) + team.getNoHandlerScore(expectedNumberOfNoHandlers)
                + team.getHandlerScore(expectedNumberOfHandlers) + team.getAgeScore(expectedAgeAverage)
                + team.getClubScore(expectedClubsScore) + team.getStandardDeviation(expectedScores);
    }

    public double compute(List<Team> teams) {
        return teams.stream().mapToDouble(this::getTeamScore).sum();
    }
}
