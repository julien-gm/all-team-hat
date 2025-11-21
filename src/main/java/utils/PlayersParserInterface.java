package utils;

import computation.TeamsGenerator;
import domain.Composition;

public interface PlayersParserInterface {

    String EMAIL = "Email payeur";
    String FIRST_NAME = "Prénom participant";
    String LAST_NAME = "Nom participant";
    String NICKNAME = "Surnom";
    String CLUB = "Nom du club d'inscription pour la saison en cours (si tu n'es pas (encore) licencié·e, inscris \"sans\", on te prendra une assurance pour la journée)";
    String AGE = "Votre âge";
    String GENDER = "Sexe";
    String HANDLING = "Votre poste ";
    String DAY = "Jour";
    String YES = "Handler";
    String NO = "Non";
    String TEAMMATE = "Binôme";

    TeamsGenerator getTeamsGenerator();

    void write(Composition bestComposition);
}
