package computation;

import domain.Team;

import java.util.List;
import java.util.Map;

public class TeamsCalculator {

    private final List<Double> expectedScores;

    private final double expectedNumberOfNoHandlers;

    private final double expectedNumberOfHandlers;

    private final double expectedNumberOfMixedHandlers;

    private final double expectedAgeAverage;

    private final Map<String, Double> expectedClubsScore;

    TeamsCalculator(List<Double> pExpectedScores, double expectedNumberOfNoHandlers, double expectedNumberOfHandlers,
            double expectedNumberOfMaybeHandlers, double expectedAgeAverage, Map<String, Double> expectedClubScore) {
        expectedScores = pExpectedScores;
        this.expectedNumberOfNoHandlers = expectedNumberOfNoHandlers;
        this.expectedNumberOfHandlers = expectedNumberOfHandlers;
        this.expectedNumberOfMixedHandlers = expectedNumberOfMaybeHandlers;
        this.expectedAgeAverage = expectedAgeAverage;
        this.expectedClubsScore = expectedClubScore;
    }

    public double getTeamScore(Team team) {
        return team.getSkillsScore(expectedScores) + team.getNoHandlerScore(expectedNumberOfNoHandlers)
                + team.getMixedHandlerScore(expectedNumberOfNoHandlers + expectedNumberOfMixedHandlers / 2)
                + team.getClubScore(expectedClubsScore) + team.getStandardDeviation(expectedScores)
                + team.getTeamMateScore();
    }

    public double compute(List<Team> teams) {
        return teams.stream().mapToDouble(this::getTeamScore).sum();
    }
}
