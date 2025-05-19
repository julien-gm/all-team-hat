package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Player {

    private Player teamMate;

    private String lastName;

    private String firstName;

    private String nickName;

    private String email;

    private Handler handler;

    private String club;

    private int age = 0;

    private List<Double> skillsList = new ArrayList<>();

    private Gender gender;

    private int day;

    private boolean real = true;

    /**
     * Default constructor
     */
    public Player() {
    }

    public Player(String nickname) {
        this.nickName = nickname;
    }

    public Player(boolean real) {
        this.real = real;
        this.club = "not a player";
        this.gender = Gender.HOMME;
    }

    public boolean isReal() {
        return this.real;
    }

    public double getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getNickName() {
        return this.nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Handler getHandler() {
        return this.handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public String getClub() {
        return this.club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public Gender getGender() {
        return this.gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public List<Double> getSkillsList() {
        return this.skillsList;
    }

    public void setSkillsList(List<Double> skillsList) {
        this.skillsList = skillsList;
    }

    public Player getTeamMate() {
        return teamMate;
    }

    public void setTeamMate(Player teamMate) {
        this.teamMate = teamMate;
    }

    public int getDay() {
        return day;
    }

    public int getRevertDay() {
        return -day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public boolean playsTheSameDay(Player p) {
        return playsTheSameDay(p.getDay());
    }

    public boolean playsTheSameDay(int day) {
        return this.getDay() == 0 || day == 0 || this.getDay() == day;
    }

    public boolean hasTeamMate() {
        return teamMate != null && teamMate != Team.fakePlayer;
    }

    @Override
    public String toString() {
        if (!this.isReal()) {
            return "";
        }
        // double sport = skillsList.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double sport = skillsList.get(skillsList.size()-1);
        return (handler.equals(Handler.YES) ? "H " : (handler.equals(Handler.MAYBE) ? "(H) " : ""))
                + String.format(Locale.FRANCE, "%s %s%s [%s] (%d) score %.2f - %s", firstName, lastName,
                        (nickName != null && !nickName.isEmpty()) ? " (" + nickName + ")" : "",
                        (gender.equals(Gender.HOMME) ? "H" : "F"), age, sport, club)
                + ((day != 0) ? String.format(" (playing day %d only)", day) : "");
    }

    public double getSkillAverage() {
        return getSkillsList().stream().mapToDouble(a -> a).average().orElse(0.0);
    }

    public String getHandlerStr() {
        switch (getHandler()) {
        case YES:
            return "H";
        case MAYBE:
            return "(h)";
        case NO:
            return "M";
        }
        return "";
    }

    public String getGenderStr() {
        return getGender().equals(Gender.HOMME) ? "H" : "F";
    }

    public String getSkillsStr() {
        StringBuilder stb = new StringBuilder();
        for (double s : skillsList) {
            stb.append(String.format(Locale.FRANCE, "\"%.2f\"", s)).append(",");
        }
        stb.append(String.format(Locale.FRANCE, "\"%.2f\"", getSkillAverage()));
        return stb.toString();
    }

    public boolean isHandler() {
        return getHandler().equals(Handler.YES);
    }

    public boolean canBeHandler() {
        return !getHandler().equals(Handler.NO);
    }

    public boolean isGirl() {
        return getGender().equals(Gender.FEMME);
    }

    public enum Handler {
        YES, MAYBE, NO
    }

    public enum Gender {
        HOMME, FEMME
    }
}
