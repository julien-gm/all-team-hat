package domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Player {

    @JsonProperty("Nom")
    private String lastName;

    @JsonProperty("Prénom")
    private String firstName;

    @JsonProperty("Pseudo")
    private String nickName;

    @JsonProperty("email")
    private String email;
    private Handler handler;
    @JsonProperty("Club")
    private String club;
    @JsonProperty("Age")
    private int age;
    @JsonProperty("Vitesse")
    private double speed;
    @JsonProperty("Technique")
    private double tech;
    @JsonProperty("Endurance")
    private double endurance;
    private Gender gender;
    private boolean real = true;

    public Player(String nickName, String club, String gender, String name, String firstName, String email, String handler, String age, String endurance, String speed, String tech, String comment) {
        this.nickName = nickName;
        this.club = club;
        setGender(gender);
        this.lastName = name;
        this.firstName = firstName;
        this.email = email;
        setHandler(handler);
        this.endurance = Double.valueOf(endurance.replace(",", "."));
        this.speed = Double.valueOf(speed.replace(",", "."));
        this.tech = Double.valueOf(tech.replace(",", "."));
    }

    public Player() {
    }

    public Player(String nickname) {
        this.nickName = nickname;
    }

    public Player(boolean real) {
        this.real = real;
        this.club = "not a player";
    }

    public boolean isReal() {
        return real;
    }

    public void setReal(boolean real) {
        this.real = real;
    }

    public double getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @JsonProperty("handler ?")
    public void setHandler(String handler) {
        switch (handler) {
            case "oui":
                setHandler(Handler.YES);
                break;
            case "si besoin":
                setHandler(Handler.MAYBE);
                break;
            default:
                setHandler(Handler.NO);
                break;
        }
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getTech() {
        return tech;
    }

    public void setTech(double tech) {
        this.tech = tech;
    }

    public double getEndurance() {
        return endurance;
    }

    public void setEndurance(double endurance) {
        this.endurance = endurance;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public double getEnduranceScore(double expectedEndurance) {
        return score(endurance, expectedEndurance);
    }

    public double getSpeedScore(double expectedSpeed) {
        return score(speed, expectedSpeed);
    }

    public double getTechScore(double expectedTech) {
        return score(tech, expectedTech);
    }

    public double getSportScore(double expectedEndurance, double expectedSpeed, double expectedTech) {
        return getEnduranceScore(expectedEndurance) + getSpeedScore(expectedSpeed) + getTechScore(expectedTech);
    }

    private double score(double value, double expected) {
        return Math.abs(value - expected);
    }

    @JsonProperty("Sexe")
    public void setGender(String gender) {
        switch (gender) {
            case "M":
                setGender(Gender.M);
                break;
            default:
                setGender(Gender.F);
        }
    }

    @Override
    public String toString() {
        if (!isReal()) {
            return "";
        }
        String handler;
        if (this.handler == null) {
            return "X";
        }
        switch (this.handler) {
            case YES:
                handler = "H";
                break;
            case MAYBE:
                handler = "h";
                break;
            case NO:
            default:
                handler = "M";
                break;
        }
        double sport = (endurance + tech + speed) / 3.0;
        return String.format("%s (%s %s) [%s](%s) score %.2f [%.2f/%.2f/%.2f] - %s", nickName, firstName, lastName, gender, handler, sport, tech, endurance, speed, club);
    }

    public enum Handler {YES, MAYBE, NO}

    public enum Gender {F, M}
}
