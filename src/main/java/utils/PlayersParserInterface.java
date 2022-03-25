package utils;

import computation.TeamsGenerator;
import domain.Composition;

public interface PlayersParserInterface {

    String EMAIL = "Email";
    String FIRST_NAME = "Prénom";
    String LAST_NAME = "Nom";
    String NICKNAME = "Pseudo";
    String CLUB = "Club";
    String AGE = "Age";
    String GENDER = "Sexe";
    String HANDLING = "Handler?";
    String DAY = "Jour";
    String YES = "Oui";
    String NO = "Non";
    String TEAMMATE = "Binôme";

    TeamsGenerator getTeamsGenerator();

    void write(Composition bestComposition);
}
