package domain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import computation.TeamsGenerator;

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

    public double getSportScore(double expectedEnduranceNumber, double expectedSpeedNumber, double expectedTechNumber) {
        double expectedSportScore = expectedEnduranceNumber + expectedSpeedNumber + expectedTechNumber;
        double score = teamGenerator.getEnduranceAverage() + teamGenerator.getSpeedAverage() + teamGenerator.getTechAverage();
        return getScore(expectedSportScore, score) * 3;
    }

    public double getEnduranceScore(double expectedEnduranceNumber) {
        return getScore(expectedEnduranceNumber, teamGenerator.getEnduranceAverage());
    }

    public double getSpeedScore(double expectedSpeedNumber) {
        return getScore(expectedSpeedNumber, teamGenerator.getSpeedAverage());
    }

    public double getTechScore(double expectedTechNumber) {
        return getScore(expectedTechNumber, teamGenerator.getTechAverage());
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
                        clubScore += Math.max(1, Math.pow(1.0 + clubsInfo.get(clubToCheck).size() - expectedClubScore.getValue(), 2));
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
        return String.format("Girls: %d/%d, H/M: %d/%d, Age : %.2f, Sport: %.2f - Tech: %.2f, Endurance: %.2f, Speed: %.2f\n",
            teamsGenerator.getNbGirls(),
            this.getNbPlayers(),
            teamsGenerator.getNbHandlers(),
            teamsGenerator.getNbNoHandlers(),
            teamsGenerator.getAgeAverage(),
            (teamsGenerator.getTechAverage() + teamsGenerator.getEnduranceAverage() + teamsGenerator.getSpeedAverage()) / 3,
            teamsGenerator.getTechAverage(),
            teamsGenerator.getEnduranceAverage(),
            teamsGenerator.getSpeedAverage()
        ) + getClubStats();
    }

    private String getClubStats() {
        if (!this.players.isEmpty()) {
            StringBuilder sb = new StringBuilder("");
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
}
