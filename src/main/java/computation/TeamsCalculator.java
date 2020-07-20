package computation;

import domain.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TeamsCalculator {

    private final List<Double> expectedScores;

    private final double expectedNumberOfNoHandlers;

    private final double expectedNumberOfHandlers;

    private final double expectedNumberOfMixedHandlers;

    private final Map<String, Double> expectedClubsScore;

    TeamsCalculator(List<Double> pExpectedScores, double expectedNumberOfNoHandlers, double expectedNumberOfHandlers,
            double expectedNumberOfMaybeHandlers, Map<String, Double> expectedClubScore) {
        expectedScores = pExpectedScores;
        this.expectedNumberOfNoHandlers = expectedNumberOfNoHandlers;
        this.expectedNumberOfHandlers = expectedNumberOfHandlers;
        this.expectedNumberOfMixedHandlers = expectedNumberOfMaybeHandlers;
        this.expectedClubsScore = expectedClubScore;
    }

    public double getTeamScore(Team team) {
        return team.getSkillsScore(expectedScores) + team.getNoHandlerScore(expectedNumberOfNoHandlers)
                + team.getMixedHandlerScore(expectedNumberOfHandlers + expectedNumberOfMixedHandlers / 2)
                + team.getHandlerScore(expectedNumberOfHandlers) + team.getClubScore(expectedClubsScore)
                + team.getStandardDeviation(expectedScores) + team.getTeamMateScore();
    }

    public double compute(List<Team> teams, int day) {
        return filterPlayerForDay(teams, day).stream().mapToDouble(this::getTeamScore).sum();
    }

    private List<Team> filterPlayerForDay(List<Team> teams, int day) {
        List<Team> newTeams = new ArrayList<>();
        for (Team team : teams) {
            newTeams.add(new Team(
                    team.getPlayers().stream().filter(p -> p.playsTheSameDay(day)).collect(Collectors.toList())));
        }
        return newTeams;
    }

    public double compute(List<Team> teams) {
        return teams.stream().mapToDouble(this::getTeamScore).sum();
    }
}
