package computation;

import domain.Team;

import java.util.List;
import java.util.Map;

public class TeamsCalculator {

    private final double expectedTechScore;

    private final double expectedEnduranceScore;

    private final double expectedSpeedScore;

    private final double expectedNumberOfNoHandlers;

    private final double expectedNumberOfHandlers;

    private final double expectedAgeAverage;

    private final Map<String, Double> expectedClubsScore;

    TeamsCalculator(double expectedTechScore,
                    double expectedEnduranceScore,
                    double expectedSpeedScore,
                    double expectedNumberOfNoHandlers,
                    double expectedNumberOfHandlers, double expectedAgeAverage,
                    Map<String, Double> expectedClubScore) {
        this.expectedTechScore = expectedTechScore;
        this.expectedEnduranceScore = expectedEnduranceScore;
        this.expectedSpeedScore = expectedSpeedScore;
        this.expectedNumberOfNoHandlers = expectedNumberOfNoHandlers;
        this.expectedNumberOfHandlers = expectedNumberOfHandlers;
        this.expectedAgeAverage = expectedAgeAverage;
        this.expectedClubsScore = expectedClubScore;
    }

    public double getTeamScore(Team team) {
        return team.getSportScore(expectedEnduranceScore, expectedSpeedScore, expectedTechScore) +
            team.getEnduranceScore(expectedEnduranceScore) +
            team.getSpeedScore(expectedSpeedScore) +
            team.getTechScore(expectedTechScore) +
            team.getNoHandlerScore(expectedNumberOfNoHandlers) +
            team.getHandlerScore(expectedNumberOfHandlers) +
            team.getAgeScore(expectedAgeAverage) +
            team.getClubScore(expectedClubsScore) +
            team.getStandardDeviation(expectedEnduranceScore, expectedSpeedScore, expectedTechScore);
    }

    public double compute(List<Team> teams) {
        return teams.stream().mapToDouble(this::getTeamScore).sum();
    }
}
