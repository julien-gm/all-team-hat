package utils;

import computation.TeamsGenerator;
import domain.Composition;


public interface PlayersParserInterface {

    TeamsGenerator getTeamsGenerator();

    void write(Composition bestComposition);
}
