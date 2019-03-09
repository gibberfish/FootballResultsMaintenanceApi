package mindbadger.football.maintenance.api.loadrecent;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class LoadRecentResultsService {
    private Logger logger = Logger.getLogger(LoadRecentResultsService.class);

//    @Autowired
//    private SeasonDivisionTeamDataService seasonDivisionTeamDataService;
//
//    @Autowired
//    private FixtureDataService fixtureDataService;
//
//    @Autowired
//    private MappingDataService mappingDataService;
//
//    @Autowired
//    private WebReaderService webReaderService;
//
//    @Autowired
//    private CurrentSeasonService currentSeasonService;
//
//    @Autowired
//    private MappingCache mappingCache;

    public void loadRecentResults() {
////        List<WebReaderFixture> allFixtures = new ArrayList<>();
//
//        List<Fixture> unplayedFixtures = getUnplayedFixturesBeforeToday();
//        List<String> unplayedFixtureDatesBeforeToday = getUnplayedFixtureDates(unplayedFixtures);
//
//        Map<String, Fixture> unplayedFixtureMap = new HashMap<>();
//        for (Fixture fixture : unplayedFixtures) {
//            unplayedFixtureMap.put(getKeyFromFixture(fixture), fixture);
//        }
//
//
//        int counter = 0;
//        int numberOfDates = unplayedFixtureDatesBeforeToday.size();
//
//        for (String fixtureDate : unplayedFixtureDatesBeforeToday) {
//            counter++;
//            logger.debug(String.format("--- Reading fixture date %d of %d ---", counter, numberOfDates));
//
//            for (WebReaderFixture webReaderFixture : webReaderService.getFixturesForDate(fixtureDate)){
//                logger.debug(webReaderFixture);
//                String key = getKeyFromWebFixture(webReaderFixture);
//                Fixture matchingFixture = unplayedFixtureMap.get(key);
//                if (matchingFixture != null) {
//                    logger.debug("Updating score for : " + matchingFixture);
//                    matchingFixture.getAttributes().setFixtureDate(webReaderFixture.getFixtureDate());
//                    matchingFixture.getAttributes().setHomeGoals(webReaderFixture.getHomeGoals());
//                    matchingFixture.getAttributes().setAwayGoals(webReaderFixture.getAwayGoals());
//
//                    logger.debug("UPDATED : " + matchingFixture);
////                    unplayedFixtureMap.put(key, matchingFixture);
//                } else {
//                    //TODO How do we know this fixture isn't already there?
//                    //TODO Find fixture matching season, teams & date
//                    logger.debug("Creating new fixture");
//                    Fixture newFixture = convertWebFixtureToFixture(webReaderFixture);
//                    unplayedFixtureMap.put(getKeyFromFixture(newFixture), newFixture);
//                }
//            }
//
//            //TODO remove this
//            break;
//        };
//
//
//        for (Fixture fixture : unplayedFixtureMap.values()) {
//
////            if (fixture.getAttributes().getHomeGoals() == null) {
////                logger.debug("Resetting fixture date for " + fixture);
////                fixture.getAttributes().setFixtureDate(null);
////            }
//
//            if (fixture.getAttributes().getAwayGoals() != null){
//                fixtureDataService.saveFixture(fixture);
//                logger.debug("SAVING " + fixture);
//            }
//        }
//
////        for (SingleFixture fixture : fixturesList) {
////            Response response = seasonDivisionTeamDataService.saveFixture(fixture);
////            if (response.getStatus() != 201) {
////                logger.debug("Error saving fixture, status = " + response.getStatus());
////            }
////        }
//
//        /*
//        Get current season
//        Get today's date
//        Get all tracked divisions from DB
//        For each tracked division...
//            Get all unplayed fixtures for the division from DB whose fixture date is less than or equal to today
//            Add to Map keyed on fixture ID (ssn_num-div id-team id-fixture date)
//        For each distinct fixture dates...
//            Read Fixtures for the date from the web
//
//        Loop through each fixture read from the web
//            Form the fixture id from record
//            See if it exists in the map, if so update the score and date in the map
//
//        Loop through each fixture in the map
//            if the score is not set (it wasn't updated by the page), then the fixture date must have moved, so set the fixture date to null
//            issue a put to the database
//
//
//         */
//
    }

//    private List<String> getUnplayedFixtureDates (List<Fixture> unplayedFixtures) {
//        Map<String,String> fixtureDates = new HashMap<>();
//        for (Fixture fixture : unplayedFixtures) {
//            String fixtureDate = fixture.getAttributes().getFixtureDate();
//            fixtureDates.put(fixtureDate, fixtureDate);
//        }
//        return fixtureDates.keySet().stream().collect(Collectors.toList());
//    }
//
//    private List<Fixture> getUnplayedFixturesBeforeToday () {
//        int season = currentSeasonService.getCurrentSeason();
//
//        List<Fixture> fixtures = new ArrayList<>();
//
//        List<SeasonDivision> seasonDivisions = seasonDivisionTeamDataService.getSeasonDivisions(season);
//        seasonDivisions.forEach(seasonDivision -> {
//            List<Fixture> fixturesForThisDivision =
//                    fixtureDataService.getUnplayedFixturesForDivisionBeforeToday(seasonDivision.getId());
//            fixtures.addAll(fixturesForThisDivision);
//        });
//
//        return fixtures;
//    }
//
//    private Fixture convertWebFixtureToFixture (WebReaderFixture webReaderFixture) {
//        String divisionId = mappingCache.getMappedDivisions().get(webReaderFixture.getDivisionId());
//        String homeTeamId = mappingCache.getMappedTeams().get(webReaderFixture.getHomeTeamId());
//        String awayTeamId = mappingCache.getMappedTeams().get(webReaderFixture.getAwayTeamId());
//
//        Fixture fixture = new Fixture();
//
//        fixture.getAttributes().setSeasonNumber(webReaderFixture.getSeasonId());
//        fixture.getAttributes().setDivisionId(divisionId);
//        fixture.getAttributes().setHomeTeamId(homeTeamId);
//        fixture.getAttributes().setAwayTeamId(awayTeamId);
//        fixture.getAttributes().setHomeGoals(webReaderFixture.getHomeGoals());
//        fixture.getAttributes().setAwayGoals(webReaderFixture.getAwayGoals());
//        fixture.getAttributes().setFixtureDate(webReaderFixture.getFixtureDate());
//
//        logger.debug("Converted to : " + fixture);
//
//        return fixture;
//    }
//
//    private String getKeyFromWebFixture (WebReaderFixture webReaderFixture) {
//        String divisionId = mappingCache.getMappedDivisions().get(webReaderFixture.getDivisionId());
//        String homeTeamId = mappingCache.getMappedTeams().get(webReaderFixture.getHomeTeamId());
//        String awayTeamId = mappingCache.getMappedTeams().get(webReaderFixture.getAwayTeamId());
//        return String.format("%d-%s-%s-%s",
//                webReaderFixture.getSeasonId(),
//                divisionId,
//                homeTeamId,
//                awayTeamId);
//    }
//
//    public String getKeyFromFixture (Fixture fixture) {
//        return String.format("%d-%s-%s-%s",
//                fixture.getAttributes().getSeasonNumber(),
//                fixture.getAttributes().getDivisionId(),
//                fixture.getAttributes().getHomeTeamId(),
//                fixture.getAttributes().getAwayTeamId());
//    }

    //TODO Create Team Structure from Fixtures (or can we do this in the loadAllFixturesForSeason
    // Maybe call it initialiseSeason

}
