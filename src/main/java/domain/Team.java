package domain;

import computation.TeamsGenerator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Team {

    public static final Player fakePlayer = new Player(false);

    private final TeamsGenerator teamGenerator;

    private List<Player> players;

    public Team(List<Player> players) {
        this.players = players;
        this.teamGenerator = new TeamsGenerator(players);
    }

    private int getNbPlayers() {
        return (int) players.stream().filter(p -> !p.equals(fakePlayer)).count();
    }

    public double getGirlScore(double expectedGirlNumber) {
        return getScore(expectedGirlNumber, teamGenerator.getNbGirls());
    }

    public double getSportScore(List<Double> pExpectedScores) {
        double expectedSportScore = pExpectedScores.stream().mapToDouble(Double::doubleValue).sum();
        double score = teamGenerator.getSkillAverages().stream().mapToDouble(Double::doubleValue).sum();
        return getScore(expectedSportScore, score) * 3;
    }

    public double getSkillsScore(List<Double> expectedScores) {
        List<Double> skillsScore = teamGenerator.getSkillAverages();
        int index = 0;
        double score = 0;
        for (double expectedScore : expectedScores) {
            score += getScore(expectedScore, skillsScore.get(index));
            index++;
        }
        return score;
    }

    public double getHandlerScore(double expectedHandlerNumber) {
        return getScore(expectedHandlerNumber, teamGenerator.getNbHandlers());
    }

    public double getNoHandlerScore(double expectedNoHandlerNumber) {
        return getScore(expectedNoHandlerNumber, teamGenerator.getNbNoHandlers()) * 5;
    }

    public double getAgeScore(double expectedAgeAverage) {
        return getScore(expectedAgeAverage, teamGenerator.getAgeAverage()) / 10;
    }

    private double getScore(double expectedScore, double actualScore) {
        return Math.abs(expectedScore - actualScore);
    }

    public void add(Player player) {
        players.add(player);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!Team.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final Team team = (Team) obj;
        return (this.players == null) ? (team.players != null) : this.players.equals(team.players);
    }

    public int getClubScore(Map<String, Double> expectedClubsScore) {
        if (!this.players.isEmpty()) {
            int clubScore = 1;
            Map<String, List<Player>> clubsInfo = getClubsInfo();
            for (Map.Entry<String, Double> expectedClubScore : expectedClubsScore.entrySet()) {
                String clubToCheck = expectedClubScore.getKey();
                if (!clubToCheck.isEmpty()) {
                    if (clubsInfo.containsKey(clubToCheck)) {
                        clubScore += Math.max(1,
                                Math.pow(1.0 + clubsInfo.get(clubToCheck).size() - expectedClubScore.getValue(), 2));
                    } else {
                        clubScore += 1;
                    }
                }
            }
            return clubScore;
        }
        return 1;
    }

    private Map<String, List<Player>> getClubsInfo() {
        return this.players.stream().collect(Collectors.groupingBy(Player::getClub));
    }

    @Override
    public int hashCode() {
        return (this.players != null ? this.players.hashCode() : 0);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public boolean hasPlayer(Player player) {
        return players.stream().anyMatch(player1 -> player1.equals(player));
    }

    @Override
    public String toString() {
        TeamsGenerator teamsGenerator = new TeamsGenerator(players);
        return String.format("Girls: %d/%d, H/M: %d/%d, Age : %.2f, Sport: %.2f\n", teamsGenerator.getNbGirls(),
                this.getNbPlayers(), teamsGenerator.getNbHandlers(), teamsGenerator.getNbNoHandlers(),
                teamsGenerator.getAgeAverage(),
                teamsGenerator.getSkillAverages().stream().mapToDouble(Double::doubleValue).sum()) + getClubStats();
    }

    private String getClubStats() {
        if (!this.players.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, List<Player>> entry : getClubsInfo().entrySet()) {
                int size = entry.getValue().size();
                if (size > 1) {
                    sb.append(entry.getKey()).append(": ").append(size).append(" ");
                }
            }
            return sb.toString();
        }
        return "\n";
    }

    public double getStandardDeviation(List<Double> pExpectedScores) {
        double teamSportAverage = getSportScore(pExpectedScores);
        return this.players.stream().mapToDouble(player -> Math.abs(
                teamSportAverage -
                        player.getSportScores(pExpectedScores).stream().mapToDouble(Double::doubleValue).average().orElse(0)
        )).average().orElse(0);
    }
}
