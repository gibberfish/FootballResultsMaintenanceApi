package mindbadger.football.maintenance.util;

import org.springframework.stereotype.Component;

@Component
public class FixtureCompositeKeyGenerator {
    public static String generateFor (Integer seasonNumber, String divisionId,
                                         String homeTeamId, String awayTeamId) {
        return String.format("%d-%s-%s-%s",
                seasonNumber,
                divisionId,
                homeTeamId,
                awayTeamId);
    }
}
