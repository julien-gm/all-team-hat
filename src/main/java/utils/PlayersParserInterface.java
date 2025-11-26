package utils;

import computation.TeamsGenerator;
import domain.Composition;

public interface PlayersParserInterface {

    String EMAIL = "Email payeur";
    String FIRST_NAME = "Prenom";
    String LAST_NAME = "Nom";
    String NICKNAME = "Pseudo";
    String CLUB = "Club";
    String AGE = "Âge";
    String GENDER = "Sexe";
    String HANDLING = "Handler?";
    String DAY = "Jour";
    String HANDLER = "oui";
    String MIDDLE = "non";
    String TEAMMATE = "Binôme";

    TeamsGenerator getTeamsGenerator();

    void write(Composition bestComposition);
}
