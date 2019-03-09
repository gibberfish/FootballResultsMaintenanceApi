package mindbadger.football.maintenance.api.initialiseseason;

import mindbadger.football.maintenance.api.MappingCache;
import mindbadger.football.maintenance.api.dataservice.FixtureDataService;
import mindbadger.football.maintenance.api.dataservice.MappingDataService;
import mindbadger.football.maintenance.api.dataservice.SeasonDivisionTeamDataService;
import mindbadger.football.maintenance.api.webreader.WebReaderService;
import mindbadger.football.maintenance.model.Fixture.Fixture;
import mindbadger.football.maintenance.model.seasondivision.SeasonDivision;
import mindbadger.football.maintenance.model.seasondivisionteam.SeasonDivisionTeam;
import mindbadger.football.maintenance.model.team.Team;
import mindbadger.football.maintenance.model.webreaderfixture.WebReaderFixture;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InitialiseSeasonService {
    private Logger logger = Logger.getLogger(InitialiseSeasonService.class);

    @Autowired
    private SeasonDivisionTeamDataService seasonDivisionTeamDataService;

    @Autowired
    private FixtureDataService fixtureDataService;

    @Autowired
    private MappingDataService mappingDataService;

    @Autowired
    private WebReaderService webReaderService;

    @Autowired
    private MappingCache mappingCache;

    public void initialiseSeason (String season) {
        Map<Integer, String> otherTeams = new HashMap<>();
        Map<String, SeasonDivisionTeam> seasonDivisionTeamMap = new HashMap<>();
        Map<String, SeasonDivision> seasonDivisionMap = new HashMap<>();

        createSeasonIfNotExists (season);

        Map<String, Fixture> fixtureMap = getAllFixturesForSeason(season);

        Map<Integer, String> teamsOnBoxingDay = getTeamsPlayingOnBoxingDay(season);

        for (Map.Entry<Integer, String> entry : teamsOnBoxingDay.entrySet()) {

            String teamIdToUseToLoadFixtures = getTeamIdForWebReaderTeamAndCreateIfNotExists(entry.getKey(), entry.getValue());

            List<WebReaderFixture> fixturesForTeam = webReaderService.getFixturesForTeam(season, entry.getKey().toString());

            if (fixturesForTeam == null) {
                logger.error("Failed to get fixtures for team for " + teamIdToUseToLoadFixtures + " ... adding to second list to reload later");
                otherTeams.put(entry.getKey(), entry.getValue());
                continue;
            }

            for (WebReaderFixture webReaderFixture2 : fixturesForTeam) {
                otherTeams.put(webReaderFixture2.getHomeTeamId(), webReaderFixture2.getHomeTeamName());
                otherTeams.put(webReaderFixture2.getAwayTeamId(), webReaderFixture2.getAwayTeamName());

                processWebReaderFixture(season, fixtureMap, seasonDivisionTeamMap, seasonDivisionMap, webReaderFixture2);
            }
        }

        for (Map.Entry<Integer, String> entry : otherTeams.entrySet()) {

            String teamIdToUseToLoadFixtures = getTeamIdForWebReaderTeamAndCreateIfNotExists(entry.getKey(), entry.getValue());

            List<WebReaderFixture> fixturesForTeam = webReaderService.getFixturesForTeam(season, entry.getKey().toString());

            if (fixturesForTeam == null) {
                logger.error("Failed to get fixtures for team for " + teamIdToUseToLoadFixtures + " ... skipping");
                continue;
            }

            for (WebReaderFixture webReaderFixture2 : fixturesForTeam) {
                processWebReaderFixture(season, fixtureMap, seasonDivisionTeamMap, seasonDivisionMap, webReaderFixture2);
            }
        }

        logger.debug("We have the following divisions: ");
        for (Map.Entry<String, SeasonDivision> entry : seasonDivisionMap.entrySet()) {
            seasonDivisionTeamDataService.createSeasonDivisionIfNotExists(entry.getValue());
        }

        logger.debug("We have the following teams: ");
        for (Map.Entry<String, SeasonDivisionTeam> entry : seasonDivisionTeamMap.entrySet()) {
            seasonDivisionTeamDataService.createSeasonDivisionTeamIfNotExists(entry.getValue());
        }

        logger.debug("We have the following " + fixtureMap.size() + "fixtures: ");
        for (Map.Entry<String, Fixture> entry : fixtureMap.entrySet()) {
            if (entry.getValue().isModified()) {
                fixtureDataService.saveFixture(entry.getValue());
            }
        }
    }

    private Map<String, Fixture> getAllFixturesForSeason (String season) {
        List<Fixture> allCurrentFixturesForSeason = fixtureDataService.getAllFixturesForSeason(season);
        Map<String, Fixture> fixtureMap = new HashMap<>();
        for (Fixture fixture : allCurrentFixturesForSeason) {
            fixtureMap.put(fixture.getUniqueCompositeKey(), fixture);
        }
        logger.debug("Got "+ fixtureMap.size() + " fixtures from DB...");
        return fixtureMap;
    }

    private void processWebReaderFixture(@RequestParam("season") String season, Map<String, Fixture> fixtureMap, Map<String, SeasonDivisionTeam> seasonDivisionTeamMap, Map<String, SeasonDivision> seasonDivisionMap, WebReaderFixture webReaderFixture2) {
        String divisionId = mappingCache.getMappedDivisions().get(webReaderFixture2.getDivisionId());
        SeasonDivision seasonDivision = new SeasonDivision();
        seasonDivision.getAttributes().setSeasonNumber(Integer.parseInt(season));
        seasonDivision.getAttributes().setDivisionId(divisionId);
        seasonDivisionMap.put(seasonDivision.getUniqueKey(), seasonDivision);

        logger.debug("Web reader home team id = " + webReaderFixture2.getHomeTeamId());
        logger.debug("Web reader away team id = " + webReaderFixture2.getAwayTeamId());

        String homeTeamId = getTeamIdForWebReaderTeamAndCreateIfNotExists(webReaderFixture2.getHomeTeamId(), webReaderFixture2.getHomeTeamName());
        SeasonDivisionTeam seasonDivisionTeam = new SeasonDivisionTeam();
        seasonDivisionTeam.getAttributes().setSeasonNumber(Integer.parseInt(season));
        seasonDivisionTeam.getAttributes().setDivisionId(divisionId);
        seasonDivisionTeam.getAttributes().setTeamId(homeTeamId);
        seasonDivisionTeamMap.put(seasonDivisionTeam.getUniqueKey(), seasonDivisionTeam);

        String awayTeamId = getTeamIdForWebReaderTeamAndCreateIfNotExists(webReaderFixture2.getAwayTeamId(), webReaderFixture2.getAwayTeamName());
        seasonDivisionTeam = new SeasonDivisionTeam();
        seasonDivisionTeam.getAttributes().setSeasonNumber(Integer.parseInt(season));
        seasonDivisionTeam.getAttributes().setDivisionId(divisionId);
        seasonDivisionTeam.getAttributes().setTeamId(awayTeamId);
        seasonDivisionTeamMap.put(seasonDivisionTeam.getUniqueKey(), seasonDivisionTeam);

        String key = getKeyFromWebFixture (webReaderFixture2);
        logger.debug("Key from webReaderFixture=" + key);
        Fixture fixture = fixtureMap.get(key);
        if (fixture != null) {
            logger.debug("Got key... updating...");
            fixture.updateFixtureFromWebFixture(webReaderFixture2,mappingCache);
        } else {
            logger.debug("No match... creating new...");
            fixture = new Fixture();
            fixture.updateFixtureFromWebFixture(webReaderFixture2,mappingCache);
            logger.debug("Key of new fixture = " + fixture.getUniqueCompositeKey());
            fixtureMap.put(fixture.getUniqueCompositeKey(), fixture);
        }

    }

    private String getKeyFromWebFixture (WebReaderFixture webReaderFixture) {
        String divisionId = mappingCache.getMappedDivisions().get(webReaderFixture.getDivisionId());
        String homeTeamId = mappingCache.getMappedTeams().get(webReaderFixture.getHomeTeamId());
        String awayTeamId = mappingCache.getMappedTeams().get(webReaderFixture.getAwayTeamId());
        return String.format("%d-%s-%s-%s",
                webReaderFixture.getSeasonId(),
                divisionId,
                homeTeamId,
                awayTeamId);
    }

    private void createSeasonIfNotExists(String season) {
        seasonDivisionTeamDataService.createSeasonIfNotExists(season);
    }

    private Map<Integer, String> getTeamsPlayingOnBoxingDay(String season) {
        String boxingDayFixtureDate = season + "-12-26";
        List<WebReaderFixture> fixtures = webReaderService.getFixturesForDate(boxingDayFixtureDate);

        Map<Integer, String> teamsOnBoxingDay = new HashMap<>();

        for (WebReaderFixture webReaderFixture : fixtures) {
            teamsOnBoxingDay.put(webReaderFixture.getHomeTeamId(), webReaderFixture.getHomeTeamName());
            teamsOnBoxingDay.put(webReaderFixture.getAwayTeamId(), webReaderFixture.getAwayTeamName());
        }

        return teamsOnBoxingDay;
    }

    private String getTeamIdForWebReaderTeamAndCreateIfNotExists (Integer webReaderTeamId, String teamName) {
        logger.debug("Find our team ID for web reader team id " + webReaderTeamId);
        String teamId = mappingCache.getMappedTeams().get(webReaderTeamId);
        if (teamId == null) {
            logger.debug("We don't currently have team " + teamName + " ("+teamId+")");
            Team team = seasonDivisionTeamDataService.createTeam(teamName);
            teamId = team.getId();
            mappingDataService.createTeamMapping (webReaderTeamId, teamId);
            mappingCache.getMappedTeams().put(webReaderTeamId, teamId);
        }
        logger.debug("Our team ID is " + teamId);
        return  teamId;
    }

}
