package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import computation.TeamsGenerator;

public class Team {

    public static final Player fakePlayer = new Player(false);
    private static final int SKILL_SCORE_COEFF = 5;
    private static final int HANDLER_SCORE_COEFF = 8;
    private static final double CLUB_SCORE_COEFF = 0.3;

    private final TeamsGenerator teamGenerator;

    private List<Player> players;

    private List<Skill> skills;

    public Team(List<Player> players) {
        this.players = players;
        this.teamGenerator = new TeamsGenerator(players);
        initSkills();
    }

    private int getNbPlayers() {
        return (int) players.stream().filter(p -> !p.equals(fakePlayer)).count();
    }

    public double getGirlScore(double expectedGirlNumber) {
        return getScore(expectedGirlNumber, teamGenerator.getNbGirls());
    }

    public double getSkillsScore(List<Double> expectedValues) {
        double skillsScore = 0;
        int skillIndex = 0;
        for (Skill skill : skills) {
            double skillValue = skill.getValue();
            double stdDev = skill.getStdDev();
            Double expectedValue = expectedValues.get(skillIndex);
            double score = getScore(expectedValue, skillValue, stdDev);
            double skillsScore1 = score / stdDev;
            skillsScore += skillsScore1;
            skillIndex++;
        }
        return skillsScore * SKILL_SCORE_COEFF;
    }

    public void initSkills() {
        skills = new ArrayList<>();
        if (players.size() > 0) {
            Player firstPlayer = players.get(0);
            for (int skillIndex = 0; skillIndex < firstPlayer.getSkillsList().size(); skillIndex++) {
                skills.add(getSkillTeam(skillIndex));
            }
        }
    }

    private double getSkillScoreAverageForTeam(int skillIndex) {
        double sumSkillScore = 0.0;
        List<Player> realPlayers = players.stream().filter(Player::isReal).collect(Collectors.toList());
        for (Player p : realPlayers) {
            sumSkillScore += p.getSkillsList().get(skillIndex);
        }
        return sumSkillScore / realPlayers.size();
    }

    private Skill getSkillTeam(int skillIndex) {
        double averageSkillScoreForTeam = getSkillScoreAverageForTeam(skillIndex);
        double stdDevSkill = 0.0;
        List<Player> listRealPlayers = players.stream().filter(Player::isReal).collect(Collectors.toList());
        for (Player p : listRealPlayers) {
            stdDevSkill += Math.abs(p.getSkillsList().get(skillIndex) - averageSkillScoreForTeam);
        }
        return new Skill(averageSkillScoreForTeam, stdDevSkill / listRealPlayers.size());
    }

    public double getHandlerScore(double expectedHandlerNumber) {
        return getScore(expectedHandlerNumber, teamGenerator.getNbHandlers());
    }

    public double getMixedHandlerScore(double expectedHandlerNumber) {
        return getScore(expectedHandlerNumber, teamGenerator.getNbHandlers() + 2 * teamGenerator.getNbMaybeHandlers())
                * HANDLER_SCORE_COEFF;
    }

    public double getNoHandlerScore(double expectedNoHandlerNumber) {
        return getScore(expectedNoHandlerNumber, teamGenerator.getNbNoHandlers()) * HANDLER_SCORE_COEFF;
    }

    private double getScore(double expectedScore, double actualScore) {
        return Math.abs(expectedScore - actualScore);
    }

    private double getScore(double expectedScore, double actualScore, double stdDev) {
        return (Math.abs(expectedScore - actualScore) / stdDev) / (expectedScore / 10);
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

    public double getClubScore(Map<String, Double> expectedClubsScore) {
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
            return clubScore * CLUB_SCORE_COEFF;
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
        return String.format("Girls: %d/%d, H/M: %d/%d, Skills: %.2f\n", teamsGenerator.getNbGirls(),
                this.getNbPlayers(), teamsGenerator.getNbHandlers(), teamsGenerator.getNbNoHandlers(), getSkillScore())
                + getClubStats();
    }

    private double getSkillScore() {
        return players.stream().filter(Player::isReal)
                .mapToDouble(p -> p.getSkillScore(teamGenerator.getSkillAverages())).sum();
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
        double teamSportAverage = getSkillsScore(pExpectedScores);
        return this.players.stream()
                .mapToDouble(player -> Math.abs(teamSportAverage - player.getSkillScore(pExpectedScores))).average()
                .orElse(0);
    }

    public double getSkillsAverage() {
        return skills.stream().mapToDouble(Skill::getValue).average().orElse(0.0);
    }

    public double getTeamMateScore() {
        return players.stream().mapToDouble(p -> (p.hasTeamMate() && !players.contains(p.getTeamMate())) ? 10 : 0)
                .sum();
    }
}
