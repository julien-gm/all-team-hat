package domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import computation.TeamsGenerator;
import domain.Player.Gender;
import domain.Player.Handler;

public class Team {

    public static final Player fakePlayer = new Player(false);
    private static final int SKILL_SCORE_COEFF = 120;
    private static final int HANDLER_SCORE_COEFF = 10;
    private static final double CLUB_SCORE_COEFF = 20;
    public static final int STD_DEV_COEFF = 20;
    public static final int NUMBER_OF_PLAYERS_BY_DAY_COEFF = 200;

    private final TeamsGenerator teamGenerator;

    private final List<Player> players;

    private List<Skill> skills;

    public Team(List<Player> players) {
        this.players = players;
        this.teamGenerator = new TeamsGenerator(players);
        initSkills();
    }

    public double getGirlScore(double expectedGirlNumber) {
        return getScore(expectedGirlNumber, teamGenerator.getNbGirls());
    }

    private double getSkillScoreValue(Skill skill, double expectedValue, double skillValue) {
        double stdDev = skill.getStdDev();
        double score = getScore(expectedValue, skillValue, stdDev);
        return score * stdDev;
    }

    public double getSkillsScore(List<Double> expectedValues) {
        double skillsScore = 0;
        int skillIndex = 0;
        for (Skill skill : skills) {
            double skillValue = skill.getValue();
            if (skillValue > 0) {
                skillsScore += getSkillScoreValue(skill, expectedValues.get(skillIndex), skillValue);
            }
            skillIndex++;
        }
        skillsScore += getSkillScore(expectedValues.stream().mapToDouble(a -> a).average().orElse(0.0));
        return skillsScore * SKILL_SCORE_COEFF;
    }

    public void initSkills() {
        if (skills == null || skills.isEmpty()) {
            skills = new ArrayList<>();
            if (getRealPlayers().size() > 0) {
                Player firstPlayer = getRealPlayers().get(0);
                for (int skillIndex = 0; skillIndex < firstPlayer.getSkillsList().size(); skillIndex++) {
                    skills.add(getSkillTeam(skillIndex));
                }
            }
        }
    }

    private double getSkillScoreAverageForTeam(int skillIndex) {
        double sumSkillScore = getRealPlayers().stream().mapToDouble(p -> p.getSkillsList().get(skillIndex)).sum();
        return sumSkillScore / getRealPlayers().size();
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
        return (Math.abs(expectedScore - actualScore) * stdDev) / (expectedScore / 10);
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
        return (this.players == null) ? (team.players != null) : this.getRealPlayers().equals(team.getRealPlayers());
    }

    public double getClubScore(Map<String, Double> expectedClubsScore) {
        if (!this.players.isEmpty()) {
            int clubScore = 0;
            Map<String, List<Player>> clubsInfo = getClubsInfo();
            for (Map.Entry<String, Double> expectedClubScore : expectedClubsScore.entrySet()) {
                String clubToCheck = expectedClubScore.getKey();
                if (!clubToCheck.isEmpty()) {
                    if (clubsInfo.containsKey(clubToCheck)) {
                        if (clubsInfo.get(clubToCheck).size() > expectedClubScore.getValue()) {
                            clubScore += clubsInfo.get(clubToCheck).size() - expectedClubScore.getValue();
                        }
                    }
                }
            }
            return clubScore * CLUB_SCORE_COEFF;
        }
        return 1;
    }

    private Map<String, List<Player>> getClubsInfo() {
        return this.players.stream().filter(Player::isReal).filter(p -> p.playsTheSameDay(1))
                .collect(Collectors.groupingBy(Player::getClub));
    }

    @Override
    public int hashCode() {
        return (this.players != null ? this.players.hashCode() : 0);
    }

    public List<Player> getPlayers() {
        return players;
    }

    boolean hasPlayer(Player player) {
        return players.stream().anyMatch(player1 -> player1.equals(player));
    }

    @Override
    public String toString() {
        StringBuilder stb = new StringBuilder();
        for (int day = 1; day <= 2; day++) {
            TeamsGenerator teamsGenerator = new TeamsGenerator(getPlayersForDay(day));

            stb.append(String.format(Locale.FRANCE,
                    "Day #%d - %d Women / %d Men, %d Handlers / %d Middles, Skills: %.2f (%.2f) [%.2f]\n", day,
                    teamsGenerator.getNbGirls(), teamsGenerator.getNbPlayers() - teamsGenerator.getNbGirls(),
                    teamsGenerator.getNbHandlers(), teamsGenerator.getNbNoHandlers(), teamsGenerator.getSkillsAverage(),
                    teamsGenerator.getSkillsStdDev(), teamsGenerator.getAgeAverage()))
                    .append(getClubStats(getPlayersForDay(day))).append("\n");
        }
        return stb.toString();
    }

    public String toCSV(List<Team> teams, int teamNumber, boolean use_day) {
        StringBuilder stb = new StringBuilder();
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.FRANCE);
        otherSymbols.setDecimalSeparator(',');
        DecimalFormat format = new DecimalFormat("#0.00", otherSymbols);
        stb.append(String.format("Team #%d\n#,Poste,Genre,Prenom,Nom,Pseudo,Age,", teamNumber));
        for (int skillIndex = 0; skillIndex < skills.size(); skillIndex++) {
            stb.append("skill_").append(skillIndex + 1).append(",");
        }
        stb.append("Moyenne compétence,Club");
        if (use_day) {
            stb.append(",Jour");
        }
        stb.append("\n");
        int index = 0;
        for (Player p : getRealPlayers()) {
            index++;
            stb.append(String.format(Locale.FRANCE, "%d,%s,%s,%s,%s,%s,%d,%s,%s", index, p.getHandlerStr(),
                    p.getGenderStr(), p.getFirstName(), p.getLastName(), p.getNickName(), (int) p.getAge(),
                    p.getSkillsStr(), p.getClub()));
            if (use_day) {
                stb.append(",").append(p.getDay());
            }
            stb.append("\n");
        }
        int nbOfHeaderLines = 2;
        int blankLines = 3;
        int firstLineNumber = nbOfHeaderLines + 1;
        for (int i = 1; i < teamNumber; i++) {
            firstLineNumber += teams.get(i - 1).getRealPlayers().size() + blankLines + nbOfHeaderLines + 1;
        }
        int lastLineNumber = firstLineNumber + getRealPlayers().size() - 1;
        StringBuilder skillsAvgStr = new StringBuilder();
        char col = 'H';
        for (int skillIndex = 0; skillIndex <= skills.size(); skillIndex++) {
            skillsAvgStr.append(
                    String.format("=ROUND(AVERAGE(%c%d:%c%d); 2),", col, lastLineNumber + 2, col, lastLineNumber + 3));
            col++;
        }
        if (use_day) {
            stb.append(String.format(
                    "=COUNTIF(B%d:B%d;\"H\"),=A%d+COUNTIF(B%d:B%d;\"(h)\"),=COUNTIF(C%d:C%d;\"F\"),,,,=ROUND(AVERAGE(G%d:G%d); 2),",
                    firstLineNumber, lastLineNumber, lastLineNumber + 1, firstLineNumber, lastLineNumber,
                    firstLineNumber, lastLineNumber, lastLineNumber + 2, lastLineNumber + 3));
            stb.append(skillsAvgStr);
            stb.append(String.format("=ARRAYFORMULA(MAX(COUNTIF(%c%d:%c%d;%c%d:%c%d))),%d\n", col, firstLineNumber, col,
                    lastLineNumber, col, firstLineNumber, col, lastLineNumber, getRealPlayers().size()));
            for (Integer day : Arrays.asList(1, 2)) {
                List<Player> dayPlayers = this.getPlayersForDay(day);
                long nbHandlers = dayPlayers.stream().filter(p -> p.getHandler().equals(Handler.YES)).count();
                long nbTotalHandlers = dayPlayers.stream().filter(p -> !p.getHandler().equals(Handler.NO)).count();
                long nbGirls = dayPlayers.stream().filter(p -> p.getGender().equals(Gender.FEMME)).count();
                double ageAverage = dayPlayers.stream().mapToDouble(Player::getAge).average().orElse(0);
                stb.append(String.format("%d,%d,%d,,,,\"%s\",", nbHandlers, nbTotalHandlers, nbGirls,
                        format.format(ageAverage)));
                for (int skillIndex = 0; skillIndex < skills.size(); skillIndex++) {
                    final int skillI = skillIndex;
                    stb.append(String.format("\"%s\",", format.format(
                            dayPlayers.stream().mapToDouble(p -> p.getSkillsList().get(skillI)).average().orElse(0))));
                }
                stb.append(String.format("\"%s\",",
                        format.format(dayPlayers.stream()
                                .mapToDouble(p -> p.getSkillsList().stream().mapToDouble(s -> s).average().orElse(0))
                                .average().orElse(0))));
                List<String> listClubs = dayPlayers.stream().map(Player::getClub).collect(Collectors.toList());
                int maxClubOccurence = 0;
                for (String club : new HashSet<String>(listClubs)) {
                    int clubOccurence = Collections.frequency(listClubs, club);
                    if (clubOccurence > maxClubOccurence) {
                        maxClubOccurence = clubOccurence;
                    }
                }

                stb.append(String.format("%d,%d\n", maxClubOccurence, dayPlayers.size()));
            }
        } else {
            long nbHandlers = this.getRealPlayers().stream().filter(p -> p.getHandler().equals(Handler.YES)).count();
            long nbTotalHandlers = this.getRealPlayers().stream().filter(p -> !p.getHandler().equals(Handler.NO))
                    .count();
            long nbGirls = this.getRealPlayers().stream().filter(p -> p.getGender().equals(Gender.FEMME)).count();
            double ageAverage = this.getRealPlayers().stream().mapToDouble(Player::getAge).average().orElse(0);
            stb.append(String.format("%d,%d,%d,,,,\"%s\",", nbHandlers, nbTotalHandlers, nbGirls,
                    format.format(ageAverage)));
            for (int skillIndex = 0; skillIndex < skills.size(); skillIndex++) {
                final int skillI = skillIndex;
                stb.append(String.format("\"%s\",", format.format(this.getRealPlayers().stream()
                        .mapToDouble(p -> p.getSkillsList().get(skillI)).average().orElse(0))));
            }
            double skillAverage = this.getRealPlayers().stream()
                    .mapToDouble(p -> p.getSkillsList().stream().mapToDouble(s -> s).average().orElse(0)).average()
                    .orElse(0);
            List<String> listClubs = this.getRealPlayers().stream().map(Player::getClub).collect(Collectors.toList());
            int maxClubOccurence = 0;
            for (String club : new HashSet<String>(listClubs)) {
                int clubOccurence = Collections.frequency(listClubs, club);
                if (clubOccurence > maxClubOccurence) {
                    maxClubOccurence = clubOccurence;
                }
            }
            stb.append(String.format("\"%s\",%d\n", format.format(skillAverage), maxClubOccurence));

        }
        stb.append("\n");
        return stb.toString();
    }

    private double getSkillsStdDev() {
        double teamSportAverage = getSkillsAverage();
        return getRealPlayers().stream().mapToDouble(player -> Math.abs(teamSportAverage - player.getSkillAverage()))
                .average().orElse(0);
    }

    private double getSkillScore(double expectedSkillsAverage) {
        return Math.abs(getSkillsAverage() - expectedSkillsAverage);
    }

    private String getClubStats(List<Player> players) {
        if (!players.isEmpty()) {
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

    public double getStandardDeviationScore(double pExpectedStdDev) {
        double skillsStdDev = getSkillsStdDev();
        return Math.abs(pExpectedStdDev - skillsStdDev) * STD_DEV_COEFF;
    }

    public double getStandardDeviation() {
        double teamSportAverage = getSkillsAverage();
        return getRealPlayers().stream().mapToDouble(player -> Math.abs(teamSportAverage - player.getSkillAverage()))
                .average().orElse(0);
    }

    double getSkillsAverage() {
        int count = 0;
        double skillScore = 0;
        for (Player p : getRealPlayers()) {
            for (double skill : p.getSkillsList()) {
                skillScore += skill;
                count++;
            }
        }
        return skillScore / count;
    }

    public double getTeamMateScore(int penalty) {
        return getRealPlayers().stream()
                .mapToDouble(p -> (p.hasTeamMate() && !players.contains(p.getTeamMate())) ? penalty : 0).sum();
    }

    public double getAgeScore(double pExpectedAge) {
        double stdDev = getRealPlayers().stream().mapToDouble(p -> Math.abs(pExpectedAge - p.getAge())).average()
                .orElse(0.0);
        return getScore(pExpectedAge, getRealPlayers().stream().mapToDouble(Player::getAge).average().orElse(0.0),
                stdDev);
    }

    public List<Integer> getDays() {
        return players.stream().mapToInt(Player::getDay).distinct().boxed().collect(Collectors.toList());
    }

    public List<Player> getPlayersForDay(int day) {
        return players.stream().filter(p -> p.playsTheSameDay(day) && p.isReal()).collect(Collectors.toList());
    }

    public List<Player> getRealPlayers() {
        return players.stream().filter(Player::isReal).collect(Collectors.toList());
    }
}
