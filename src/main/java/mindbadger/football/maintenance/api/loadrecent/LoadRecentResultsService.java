package mindbadger.football.maintenance.api.loadrecent;

import mindbadger.football.maintenance.api.MappingCache;
import mindbadger.football.maintenance.api.dataservice.FixtureDataService;
import mindbadger.football.maintenance.api.dataservice.MappingDataService;
import mindbadger.football.maintenance.api.dataservice.SeasonDivisionTeamDataService;
import mindbadger.football.maintenance.api.rest.ExternalServiceInvocationException;
import mindbadger.football.maintenance.api.table.StatisticsCalculationService;
import mindbadger.football.maintenance.api.webreader.WebReaderService;
import mindbadger.football.maintenance.jms.LoadFixturesForDateQueueSender;
import mindbadger.football.maintenance.model.Fixture.Fixture;
import mindbadger.football.maintenance.model.seasondivision.SeasonDivision;
import mindbadger.football.maintenance.model.webreaderfixture.WebReaderFixture;
import mindbadger.football.maintenance.util.CurrentSeasonService;
import mindbadger.football.maintenance.util.FixtureCompositeKeyGenerator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class LoadRecentResultsService {
    private Logger logger = Logger.getLogger(LoadRecentResultsService.class);

    @Autowired
    private SeasonDivisionTeamDataService seasonDivisionTeamDataService;

    @Autowired
    private FixtureDataService fixtureDataService;

    @Autowired
    private MappingDataService mappingDataService;

    @Autowired
    private WebReaderService webReaderService;

    @Autowired
    private CurrentSeasonService currentSeasonService;

    @Autowired
    private StatisticsCalculationService statisticsCalculationService;

    @Autowired
    private MappingCache mappingCache;

    @Autowired
    private LoadFixturesForDateQueueSender loadFixturesForDateQueueSender;

    public void loadRecentResults() throws ExternalServiceInvocationException {
        logger.info("Load Recent Results (using queues)- starting");

        List<Fixture> unplayedFixtures = getUnplayedFixturesBeforeToday();
        logger.debug("Number of unplayed fixtures before today = " + unplayedFixtures.size());

        List<String> unplayedFixtureDatesBeforeToday = getUnplayedFixtureDates(unplayedFixtures);

        Map<String, Fixture> unplayedFixtureMap = new HashMap<>();
        for (Fixture fixture : unplayedFixtures) {
            unplayedFixtureMap.put(getKeyFromFixture(fixture), fixture);
        }

        int counter = 0;
        int numberOfDates = unplayedFixtureDatesBeforeToday.size();
        logger.debug("Number of dates to load fixtures for = " + numberOfDates);

        for (String fixtureDate : unplayedFixtureDatesBeforeToday) {
            loadFixturesForDateQueueSender.send(fixtureDate);
        }
    }













    public void loadRecentResultsOLD() throws ExternalServiceInvocationException {
        logger.info("Load Recent Results (OLD)- starting");
        /*

        Get current season
        Get today's date
        Get all tracked divisions from DB
        For each tracked division...
            Get all unplayed fixtures for the division from DB whose fixture date is less than or equal to today
            Add to Map keyed on fixture ID (ssn_num-div id-team id-fixture date)
        For each distinct fixture dates...
            Read Fixtures for the date from the web

        Loop through each fixture read from the web
            Form the fixture id from record
            See if it exists in the map, if so update the score and date in the map

        Loop through each fixture in the map
            if the score is not set (it wasn't updated by the page), then the fixture date must have moved, so set the fixture date to null
            issue a put to the database


         */


//        List<WebReaderFixture> allFixtures = new ArrayList<>();

        List<Fixture> unplayedFixtures = getUnplayedFixturesBeforeToday();
        logger.debug("Number of unplayed fixtures before today = " + unplayedFixtures.size());

        List<String> unplayedFixtureDatesBeforeToday = getUnplayedFixtureDates(unplayedFixtures);

        Map<String, Fixture> unplayedFixtureMap = new HashMap<>();
        for (Fixture fixture : unplayedFixtures) {
            unplayedFixtureMap.put(getKeyFromFixture(fixture), fixture);
        }

        int counter = 0;
        int numberOfDates = unplayedFixtureDatesBeforeToday.size();
        logger.debug("Number of dates to load fixtures for = " + numberOfDates);

        for (String fixtureDate : unplayedFixtureDatesBeforeToday) {
            counter++;
            logger.debug(String.format("--- Reading fixture date %d of %d ---", counter, numberOfDates));
logger.error("XXXXXXX: " + fixtureDate);
            for (WebReaderFixture webReaderFixture : webReaderService.getFixturesForDate(fixtureDate)){
                //logger.debug(webReaderFixture);
                String key = getKeyFromWebFixture(webReaderFixture);
                Fixture matchingFixture = unplayedFixtureMap.get(key);

                if (matchingFixture == null || matchingFixture.getAttributes() == null || matchingFixture.getAttributes().getHomeTeamId() == null || matchingFixture.getAttributes().getAwayTeamId() == null) {
                    logger.warn("We have null team IDs! Skipping...");
                    continue;
                }


                if (matchingFixture != null) {
                    logger.debug("Updating score for : " + matchingFixture);
                    matchingFixture.updateFixtureFromWebFixture(webReaderFixture, mappingCache);
                    unplayedFixtureMap.put(key, matchingFixture);
                } else {
                    logger.debug("Creating new fixture");
                    Fixture newFixture = convertWebFixtureToFixture(webReaderFixture);
                    unplayedFixtureMap.put(getKeyFromFixture(newFixture), newFixture);
                }
            }
        };

        // Any fixtures left in the map haven't been played on the dates
        for (Fixture fixture : unplayedFixtureMap.values()) {

            if (fixture.getAttributes().getHomeGoals() == null) {
                logger.debug("Resetting fixture date for " + fixture);
                fixture.getAttributes().setFixtureDate(null);
            }
        }

        List<Fixture> fixtures = new ArrayList<Fixture>(unplayedFixtureMap.values());

        fixtureDataService.saveFixtures(fixtures);

        statisticsCalculationService.updateStatisticsForFixtures(fixtures);

        logger.info("Load Recent Results - complete");
    }

    private List<String> getUnplayedFixtureDates (List<Fixture> unplayedFixtures) {
        Map<String,String> fixtureDates = new HashMap<>();
        for (Fixture fixture : unplayedFixtures) {
            String fixtureDate = fixture.getAttributes().getFixtureDate();
            fixtureDates.put(fixtureDate, fixtureDate);
        }
        return fixtureDates.keySet().stream().collect(Collectors.toList());
    }

    private List<Fixture> getUnplayedFixturesBeforeToday () throws ExternalServiceInvocationException {
        int season = currentSeasonService.getCurrentSeason();

        List<Fixture> fixtures = new ArrayList<>();

        List<SeasonDivision> seasonDivisions = seasonDivisionTeamDataService.getSeasonDivisions(season);
        for (SeasonDivision seasonDivision : seasonDivisions) {
            List<Fixture> fixturesForThisDivision =
                    fixtureDataService.getUnplayedFixturesForDivisionBeforeToday(seasonDivision.getId());
            fixtures.addAll(fixturesForThisDivision);
        }

        return fixtures;
    }

    private Fixture convertWebFixtureToFixture (WebReaderFixture webReaderFixture) {
        String divisionId = mappingCache.getMappedDivisions().get(webReaderFixture.getDivisionId());
        String homeTeamId = mappingCache.getMappedTeams().get(webReaderFixture.getHomeTeamId());
        String awayTeamId = mappingCache.getMappedTeams().get(webReaderFixture.getAwayTeamId());

        Fixture fixture = new Fixture();

        fixture.getAttributes().setSeasonNumber(webReaderFixture.getSeasonId());
        fixture.getAttributes().setDivisionId(divisionId);
        fixture.getAttributes().setHomeTeamId(homeTeamId);
        fixture.getAttributes().setAwayTeamId(awayTeamId);
        fixture.getAttributes().setHomeGoals(webReaderFixture.getHomeGoals());
        fixture.getAttributes().setAwayGoals(webReaderFixture.getAwayGoals());
        fixture.getAttributes().setFixtureDate(webReaderFixture.getFixtureDate());

        //logger.debug("Converted to : " + fixture);

        return fixture;
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

    public String getKeyFromFixture (Fixture fixture) {
        return FixtureCompositeKeyGenerator.generateFor(
                fixture.getAttributes().getSeasonNumber(),
                fixture.getAttributes().getDivisionId(),
                fixture.getAttributes().getHomeTeamId(),
                fixture.getAttributes().getAwayTeamId());
    }
}
