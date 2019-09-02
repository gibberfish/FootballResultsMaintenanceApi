package mindbadger.football.maintenance.api.initialiseseason;

import mindbadger.football.maintenance.api.MappingCache;
import mindbadger.football.maintenance.api.dataservice.FixtureDataService;
import mindbadger.football.maintenance.api.dataservice.MappingDataService;
import mindbadger.football.maintenance.api.dataservice.SeasonDivisionTeamDataService;
import mindbadger.football.maintenance.api.table.StatisticsCalculationService;
import mindbadger.football.maintenance.api.webreader.WebReaderService;
import mindbadger.football.maintenance.jms.LoadFixturesForTeamQueueSender;
import mindbadger.football.maintenance.model.Fixture.Fixture;
import mindbadger.football.maintenance.model.seasondivision.SeasonDivision;
import mindbadger.football.maintenance.model.seasondivisionteam.SeasonDivisionTeam;
import mindbadger.football.maintenance.model.team.Team;
import mindbadger.football.maintenance.model.webreaderfixture.WebReaderFixture;
import mindbadger.football.maintenance.util.FixtureCompositeKeyGenerator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InitialiseSeasonService {
    private Logger logger = Logger.getLogger(InitialiseSeasonService.class);

    @Autowired
    private LoadFixturesForTeamQueueSender loadFixturesForTeamQueueSender;

    @Autowired
    private SeasonDivisionTeamDataService seasonDivisionTeamDataService;

    @Autowired
    private FixtureDataService fixtureDataService;

    @Autowired
    private MappingDataService mappingDataService;

    @Autowired
    private WebReaderService webReaderService;

    @Autowired
    private StatisticsCalculationService statisticsCalculationService;

    @Autowired
    private MappingCache mappingCache;

    public void initialiseSeason (String season) throws IOException {
        createSeasonIfNotExists (season);

        for (Integer teamId : getTeamsPlayingOnBoxingDay(season)) {
            this.loadFixturesForTeamQueueSender.send(season, teamId);
        }
    }

    public void initialiseSeasonOLD (String season) {
//        Map<String, SeasonDivisionTeam> seasonDivisionTeamMap = new HashMap<>();
//        Map<String, SeasonDivision> seasonDivisionMap = new HashMap<>();
//
//        createSeasonIfNotExists (season);
//
//        Map<String, Fixture> fixtureMap = getAllFixturesForSeason(season);
//
//        Map<Integer, String> teamsToLoadFixtures = getTeamsPlayingOnBoxingDay(season);
//
//        int counter = 0;
//        while (counter < 3 && teamsToLoadFixtures.size() > 0) {
//            teamsToLoadFixtures = readFixturesForTeams(season, teamsToLoadFixtures, fixtureMap, seasonDivisionTeamMap, seasonDivisionMap);
//            counter++;
//        }
//
//        logger.debug("We have the following " + seasonDivisionMap.size() + " divisions: ");
//        for (Map.Entry<String, SeasonDivision> entry : seasonDivisionMap.entrySet()) {
//            if (seasonDivisionTeamDataService.getSeasonDivision(entry.getValue()) == null) {
//                seasonDivisionTeamDataService.createSeasonDivision(entry.getValue());
//            };
//        }
//
//        logger.debug("We have the following " + seasonDivisionTeamMap.size() + " teams: ");
//        for (Map.Entry<String, SeasonDivisionTeam> entry : seasonDivisionTeamMap.entrySet()) {
//            if (seasonDivisionTeamDataService.getSeasonDivisionTeam(entry.getValue()) == null) {
//                seasonDivisionTeamDataService.createSeasonDivisionTeam(entry.getValue());
//            }
//        }
//
//        addMissingFixtures(season, seasonDivisionTeamMap, fixtureMap);
//
//        logger.debug("We have the following " + fixtureMap.size() + " fixtures: ");
//
//        List<Fixture> fixtures = new ArrayList<Fixture>(fixtureMap.values());
//
//        fixtureDataService.saveFixtures(fixtures);
//
//        statisticsCalculationService.updateStatisticsForFixtures(fixtures);
    }

    private void addMissingFixtures (String season, Map<String, SeasonDivisionTeam> seasonDivisionTeamMap, Map<String, Fixture> fixtureMap) {
        Map<String, List<String>> divisions = new HashMap<>();

        for (Map.Entry<String, SeasonDivisionTeam> entry : seasonDivisionTeamMap.entrySet()) {
            String teamId = entry.getValue().getAttributes().getTeamId();
            String divId = entry.getValue().getAttributes().getDivisionId();

            List<String> divisionTeamList = divisions.get(divId);

            if (divisionTeamList == null) {
                divisionTeamList = new ArrayList<>();
                divisions.put(divId, divisionTeamList);
            }

            divisionTeamList.add(teamId);
        }

        for (Map.Entry<String, List<String>> entry : divisions.entrySet()) {
            String divId = entry.getKey();
            for (String homeTeamId : entry.getValue()) {
                for (String awayTeamId : entry.getValue()) {
                    if (!homeTeamId.equals(awayTeamId)) {
                        String key = season + "-" + divId + "-" + homeTeamId + "-" + awayTeamId;
                        if (fixtureMap.get(key) == null) {
                            Fixture fixture = new Fixture();
                            fixture.getAttributes().setSeasonNumber(Integer.parseInt(season));
                            fixture.getAttributes().setDivisionId(divId);
                            fixture.getAttributes().setHomeTeamId(homeTeamId);
                            fixture.getAttributes().setAwayTeamId(awayTeamId);
                            logger.info("Fixture missing,so adding : " + key);
                            fixtureMap.put(key, fixture);
                        }
                    }
                }
            }
        }
    }

    private Map<Integer, String> readFixturesForTeams (String season, Map<Integer, String> teams, Map<String, Fixture> fixtureMap, Map<String, SeasonDivisionTeam> seasonDivisionTeamMap, Map<String, SeasonDivision> seasonDivisionMap) throws IOException {
        logger.debug("****** readFixturesForTeams ******");
        logger.debug("   Season = " + season);
        logger.debug("   Teams = " + teams.size());
        logger.debug("   Fixture Map = " + fixtureMap.size());
        logger.debug("   Season Division Team Map = " + seasonDivisionTeamMap.size());
        logger.debug("   Season Division Map = " + seasonDivisionMap.size());

        Map<Integer, String> teamsToLoadInAnotherIteration = new HashMap<>();
        for (Map.Entry<Integer, String> entry : teams.entrySet()) {

            String teamIdToUseToLoadFixtures = getTeamIdForWebReaderTeamAndCreateIfNotExists(entry.getKey(), entry.getValue());

            List<WebReaderFixture> fixturesForTeam = webReaderService.getFixturesForTeam(season, entry.getKey().toString());

            if (fixturesForTeam == null) {
                logger.error("Failed to get fixtures for team for " + teamIdToUseToLoadFixtures + " ... adding to second list to reload later");
                teamsToLoadInAnotherIteration.put(entry.getKey(), entry.getValue());
                continue;
            }

            logger.debug("   Read " + fixturesForTeam.size() + " fixtures for team " + teamIdToUseToLoadFixtures);

            for (WebReaderFixture webReaderFixture : fixturesForTeam) {
                if (teams.get(webReaderFixture.getHomeTeamId()) == null) {
                    teamsToLoadInAnotherIteration.put(webReaderFixture.getHomeTeamId(), webReaderFixture.getHomeTeamName());
                }
                if (teams.get(webReaderFixture.getAwayTeamId()) == null) {
                    teamsToLoadInAnotherIteration.put(webReaderFixture.getAwayTeamId(), webReaderFixture.getAwayTeamName());
                }

                processWebReaderFixture(season, fixtureMap, seasonDivisionTeamMap, seasonDivisionMap, webReaderFixture);
            }
        }

        logger.debug("   Fixture Map (afterwards) = " + fixtureMap.size());
        logger.debug("   Season Division Team Map (afterwards) = " + seasonDivisionTeamMap.size());
        logger.debug("   Season Division Map (afterwards) = " + seasonDivisionMap.size());

        logger.debug("   Got another " + teamsToLoadInAnotherIteration.size() + " teams to load in another iteration");
        return teamsToLoadInAnotherIteration;
    }

    private Map<String, Fixture> getAllFixturesForSeason (String season) throws IOException {
        List<Fixture> allCurrentFixturesForSeason = fixtureDataService.getAllFixturesForSeason(season);
        Map<String, Fixture> fixtureMap = new HashMap<>();
        for (Fixture fixture : allCurrentFixturesForSeason) {
            fixtureMap.put(fixture.getUniqueCompositeKey(), fixture);
        }
        logger.debug("Got "+ fixtureMap.size() + " fixtures from DB...");
        return fixtureMap;
    }

    private void processWebReaderFixture(@RequestParam("season") String season, Map<String, Fixture> fixtureMap, Map<String, SeasonDivisionTeam> seasonDivisionTeamMap, Map<String, SeasonDivision> seasonDivisionMap, WebReaderFixture webReaderFixture2) throws IOException {
        String divisionId = mappingCache.getMappedDivisions().get(webReaderFixture2.getDivisionId());
        SeasonDivision seasonDivision = new SeasonDivision();
        seasonDivision.getAttributes().setSeasonNumber(Integer.parseInt(season));
        seasonDivision.getAttributes().setDivisionId(divisionId);
        seasonDivisionMap.put(seasonDivision.getUniqueKey(), seasonDivision);

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
        return FixtureCompositeKeyGenerator.generateFor(
                webReaderFixture.getSeasonId(),
                divisionId,
                homeTeamId,
                awayTeamId);
    }

    private void createSeasonIfNotExists(String season) throws IOException {
        if (seasonDivisionTeamDataService.getSeason(season) == null) {
            logger.info("Season " + season + " doesn't exist, so creating it...");
            seasonDivisionTeamDataService.createSeason(season);
        }
    }

    private List<Integer> getTeamsPlayingOnBoxingDay(String season) {
        String boxingDayFixtureDate = season + "-12-26";
        List<WebReaderFixture> fixtures = webReaderService.getFixturesForDate(boxingDayFixtureDate);

        // Get one fixture from each division
        Map<Integer, WebReaderFixture> fixtureForEachDivision = new HashMap<>();
        for (WebReaderFixture webReaderFixture : fixtures) {
            Integer divId = webReaderFixture.getDivisionId();
            if (fixtureForEachDivision.get(divId) == null) {
                fixtureForEachDivision.put(divId, webReaderFixture);
            }
        }

        List<Integer> teamsOnBoxingDay = new ArrayList<>();

        // Get all of the other teams that the home team is playing in this season
        for (Map.Entry<Integer, WebReaderFixture> entry : fixtureForEachDivision.entrySet()) {
            WebReaderFixture fixtureForDivision = entry.getValue();
            List<WebReaderFixture> fixturesForThisTeam = webReaderService.getFixturesForTeam(season, fixtureForDivision.getHomeTeamId().toString());
            teamsOnBoxingDay.add(fixtureForDivision.getHomeTeamId());

            for (WebReaderFixture webReaderFixture : fixturesForThisTeam) {
                if (webReaderFixture.getHomeTeamId().equals(fixtureForDivision.getHomeTeamId())) {
                    teamsOnBoxingDay.add(webReaderFixture.getAwayTeamId());
                }
            }
        }

        logger.debug("Loaded " + teamsOnBoxingDay.size() + " teams from date " + boxingDayFixtureDate);
        return teamsOnBoxingDay;
    }


    private Map<Integer, String> getTeamsPlayingOnBoxingDayOLD(String season) {
        String boxingDayFixtureDate = season + "-12-26";
        List<WebReaderFixture> fixtures = webReaderService.getFixturesForDate(boxingDayFixtureDate);

        Map<Integer, String> teamsOnBoxingDay = new HashMap<>();

        for (WebReaderFixture webReaderFixture : fixtures) {
            teamsOnBoxingDay.put(webReaderFixture.getHomeTeamId(), webReaderFixture.getHomeTeamName());
            teamsOnBoxingDay.put(webReaderFixture.getAwayTeamId(), webReaderFixture.getAwayTeamName());
        }

        logger.debug("Loaded " + teamsOnBoxingDay.size() + " teams from date " + boxingDayFixtureDate);
        return teamsOnBoxingDay;
    }

    private String getTeamIdForWebReaderTeamAndCreateIfNotExists (Integer webReaderTeamId, String teamName) throws IOException {
        String teamId = mappingCache.getMappedTeams().get(webReaderTeamId);
        if (teamId == null) {
            logger.debug("We don't currently have team " + teamName + " ("+teamId+")");
            Team team = seasonDivisionTeamDataService.createTeam(teamName);
            teamId = team.getId();
            mappingDataService.createTeamMapping (webReaderTeamId, teamId, "soccerbase");
            mappingCache.getMappedTeams().put(webReaderTeamId, teamId);
        }
        logger.debug("Our team ID for web reader id " + webReaderTeamId + " is " + teamId);
        return  teamId;
    }

}