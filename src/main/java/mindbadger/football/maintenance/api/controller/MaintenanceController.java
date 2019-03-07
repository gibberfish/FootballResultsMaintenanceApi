package mindbadger.football.maintenance.api.controller;

import mindbadger.football.maintenance.model.seasondivisionteam.SeasonDivisionTeam;
import mindbadger.football.maintenance.model.team.Team;
import mindbadger.football.maintenance.util.CurrentSeasonService;
import mindbadger.football.maintenance.api.DataService;
import mindbadger.football.maintenance.api.MappingCache;
import mindbadger.football.maintenance.api.WebReaderService;
import mindbadger.football.maintenance.model.Fixture.Fixture;
import mindbadger.football.maintenance.model.Fixture.SingleFixture;
import mindbadger.football.maintenance.model.seasondivision.SeasonDivision;
import mindbadger.football.maintenance.model.webreaderfixture.WebReaderFixture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("maintenance")
public class MaintenanceController {

    @Autowired
    private DataService dataService;

    @Autowired
    private WebReaderService webReaderService;

    @Autowired
    private CurrentSeasonService currentSeasonService;

    @Autowired
    private MappingCache mappingCache;


    @GetMapping(value = "/loadRecentResults", produces = "application/json")
    public ResponseEntity<String> loadRecentResults () {

//        List<WebReaderFixture> allFixtures = new ArrayList<>();

        List<Fixture> unplayedFixtures = getUnplayedFixturesBeforeToday();
        List<String> unplayedFixtureDatesBeforeToday = getUnplayedFixtureDates(unplayedFixtures);

        Map<String, Fixture> unplayedFixtureMap = new HashMap<>();
        for (Fixture fixture : unplayedFixtures) {
            unplayedFixtureMap.put(getKeyFromFixture(fixture), fixture);
        }


        int counter = 0;
        int numberOfDates = unplayedFixtureDatesBeforeToday.size();

        for (String fixtureDate : unplayedFixtureDatesBeforeToday) {
            counter++;
            System.out.println(String.format("--- Reading fixture date %d of %d ---", counter, numberOfDates));

            for (WebReaderFixture webReaderFixture : webReaderService.getFixturesForDate(fixtureDate)){
                System.out.println(webReaderFixture);
                String key = getKeyFromWebFixture(webReaderFixture);
                Fixture matchingFixture = unplayedFixtureMap.get(key);
                if (matchingFixture != null) {
                    System.out.println("Updating score for : " + matchingFixture);
                    matchingFixture.getAttributes().setFixtureDate(webReaderFixture.getFixtureDate());
                    matchingFixture.getAttributes().setHomeGoals(webReaderFixture.getHomeGoals());
                    matchingFixture.getAttributes().setAwayGoals(webReaderFixture.getAwayGoals());

                    System.out.println("UPDATED : " + matchingFixture);
//                    unplayedFixtureMap.put(key, matchingFixture);
                } else {
                    //TODO How do we know this fixture isn't already there?
                    //TODO Find fixture matching season, teams & date
                    System.out.println("Creating new fixture");
                    Fixture newFixture = convertWebFixtureToFixture(webReaderFixture);
                    unplayedFixtureMap.put(getKeyFromFixture(newFixture), newFixture);
                }
            }

            //TODO remove this
            break;
        };


        for (Fixture fixture : unplayedFixtureMap.values()) {

//            if (fixture.getAttributes().getHomeGoals() == null) {
//                System.out.println("Resetting fixture date for " + fixture);
//                fixture.getAttributes().setFixtureDate(null);
//            }

            if (fixture.getAttributes().getAwayGoals() != null){
                dataService.saveFixture(fixture);
                System.out.println("SAVING " + fixture);
            }
        }

//        for (SingleFixture fixture : fixturesList) {
//            Response response = dataService.saveFixture(fixture);
//            if (response.getStatus() != 201) {
//                System.out.println("Error saving fixture, status = " + response.getStatus());
//            }
//        }

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


        return new ResponseEntity<>("Load Recent Results Complete", HttpStatus.OK);
    }


    @GetMapping(value = "/initialiseSeason", produces = "application/json")
    public ResponseEntity<String> loadAllFixturesForSeason (@RequestParam(value="season") String season) {

        System.out.println("Initialise Season");

        createSeasonIfNotExists (season);

        List<Fixture> allCurrentFixturesForSeason = dataService.getAllFixturesForSeason(season);
        Map<String, Fixture> fixtureMap = new HashMap<>();
        for (Fixture fixture : allCurrentFixturesForSeason) {
            fixtureMap.put(fixture.getUniqueCompositeKey(), fixture);
        }
        System.out.println("Got "+ fixtureMap.size() + " fixtures from DB...");

        List<WebReaderFixture> fixturesForBoxingDay = getFixturesOnBoxingDay (season);

        Map<Integer, String> teamsOnBoxingDay = new HashMap<>();
        Map<Integer, String> otherTeams = new HashMap<>();

        Map<String, SeasonDivisionTeam> seasonDivisionTeamMap = new HashMap<>();
        Map<String, SeasonDivision> seasonDivisionMap = new HashMap<>();

        for (WebReaderFixture webReaderFixture : fixturesForBoxingDay) {
            teamsOnBoxingDay.put(webReaderFixture.getHomeTeamId(), webReaderFixture.getHomeTeamName());
            teamsOnBoxingDay.put(webReaderFixture.getAwayTeamId(), webReaderFixture.getAwayTeamName());
        }

        for (Map.Entry<Integer, String> entry : teamsOnBoxingDay.entrySet()) {

            String teamIdToUseToLoadFixtures = getTeamIdForWebReaderTeamAndCreateIfNotExists(entry.getKey(), entry.getValue());

            List<WebReaderFixture> fixturesForTeam = webReaderService.getFixturesForTeam(season, entry.getKey().toString());

            if (fixturesForTeam == null) {
                System.out.println("Failed to get fixtures for team for " + teamIdToUseToLoadFixtures + " ... skipping");
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
                System.out.println("Failed to get fixtures for team for " + teamIdToUseToLoadFixtures + " ... skipping");
                continue;
            }

            for (WebReaderFixture webReaderFixture2 : fixturesForTeam) {
                processWebReaderFixture(season, fixtureMap, seasonDivisionTeamMap, seasonDivisionMap, webReaderFixture2);
            }
        }

        System.out.println("We have the following divisions: ");
        for (Map.Entry<String, SeasonDivision> entry : seasonDivisionMap.entrySet()) {
            dataService.createSeasonDivisionIfNotExists(entry.getValue());
        }

        System.out.println("We have the following teams: ");
        for (Map.Entry<String, SeasonDivisionTeam> entry : seasonDivisionTeamMap.entrySet()) {
            dataService.createSeasonDivisionTeamIfNotExists(entry.getValue());
        }

        System.out.println("We have the following " + fixtureMap.size() + "fixtures: ");
        for (Map.Entry<String, Fixture> entry : fixtureMap.entrySet()) {
            if (entry.getValue().isModified()) {
                dataService.saveFixture(entry.getValue());
            }
        }

        return new ResponseEntity<>("Initialise Team Complete", HttpStatus.OK);
    }

    private void processWebReaderFixture(@RequestParam("season") String season, Map<String, Fixture> fixtureMap, Map<String, SeasonDivisionTeam> seasonDivisionTeamMap, Map<String, SeasonDivision> seasonDivisionMap, WebReaderFixture webReaderFixture2) {
        String divisionId = mappingCache.getMappedDivisions().get(webReaderFixture2.getDivisionId());
        SeasonDivision seasonDivision = new SeasonDivision();
        seasonDivision.getAttributes().setSeasonNumber(Integer.parseInt(season));
        seasonDivision.getAttributes().setDivisionId(divisionId);
        seasonDivisionMap.put(seasonDivision.getUniqueKey(), seasonDivision);

        System.out.println("Web reader home team id = " + webReaderFixture2.getHomeTeamId());
        System.out.println("Web reader away team id = " + webReaderFixture2.getAwayTeamId());

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
        System.out.println("Key from webReaderFixture=" + key);
        Fixture fixture = fixtureMap.get(key);
        if (fixture != null) {
            System.out.println("Got key... updating...");
            fixture.updateFixtureFromWebFixture(webReaderFixture2,mappingCache);
        } else {
            System.out.println("No match... creating new...");
            fixture = new Fixture();
            fixture.updateFixtureFromWebFixture(webReaderFixture2,mappingCache);
            System.out.println("Key of new fixture = " + fixture.getUniqueCompositeKey());
            fixtureMap.put(fixture.getUniqueCompositeKey(), fixture);
        }
    }

    private String getTeamIdForWebReaderTeamAndCreateIfNotExists (Integer webReaderTeamId, String teamName) {
        System.out.println("Find our team ID for web reader team id " + webReaderTeamId);
        String teamId = mappingCache.getMappedTeams().get(webReaderTeamId);
        if (teamId == null) {
            System.out.println("We don't currently have team " + teamName + " ("+teamId+")");
            Team team = dataService.createTeam(teamName);
            teamId = team.getId();
            dataService.createTeamMapping (webReaderTeamId, teamId);
            mappingCache.getMappedTeams().put(webReaderTeamId, teamId);
        }
        System.out.println("Our team ID is " + teamId);
        return  teamId;
    }

    private List<WebReaderFixture> getFixturesOnBoxingDay(String season) {
        String boxingDayFixtureDate = season + "-12-26";
        List<WebReaderFixture> fixtures = webReaderService.getFixturesForDate(boxingDayFixtureDate);
        return fixtures;
    }

    private List<String> getUnplayedFixtureDatesBeforeToday () {
        int season = currentSeasonService.getCurrentSeason();

        Map<String,String> fixtureDates = new HashMap<>();

        List<SeasonDivision> seasonDivisions = dataService.getSeasonDivisions(season);
        seasonDivisions.forEach(seasonDivision -> {
            List<Fixture> fixtures = dataService.getUnplayedFixturesForDivisionBeforeToday(seasonDivision.getId());

            for (Fixture fixture : fixtures) {
                String fixtureDate = fixture.getAttributes().getFixtureDate();
                fixtureDates.put(fixtureDate, fixtureDate);
            }
        });

        return fixtureDates.keySet().stream().collect(Collectors.toList());
    }

    private List<String> getUnplayedFixtureDates (List<Fixture> unplayedFixtures) {
        Map<String,String> fixtureDates = new HashMap<>();
        for (Fixture fixture : unplayedFixtures) {
            String fixtureDate = fixture.getAttributes().getFixtureDate();
            fixtureDates.put(fixtureDate, fixtureDate);
        }
        return fixtureDates.keySet().stream().collect(Collectors.toList());
    }

    private List<Fixture> getUnplayedFixturesBeforeToday () {
        int season = currentSeasonService.getCurrentSeason();

        List<Fixture> fixtures = new ArrayList<>();

        List<SeasonDivision> seasonDivisions = dataService.getSeasonDivisions(season);
        seasonDivisions.forEach(seasonDivision -> {
            List<Fixture> fixturesForThisDivision =
                    dataService.getUnplayedFixturesForDivisionBeforeToday(seasonDivision.getId());
            fixtures.addAll(fixturesForThisDivision);
        });

        return fixtures;
    }

    private List<SingleFixture> convertWebReaderFixturesToFixtureList (List<WebReaderFixture> webReaderFixtures) {
        List<SingleFixture> fixtures = new ArrayList<>();

        for (WebReaderFixture webReaderFixture : webReaderFixtures) {

            System.out.println(webReaderFixture);

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

            System.out.println("Converted to : " + fixture);

            SingleFixture singleFixture = new SingleFixture();
            singleFixture.setData(fixture);

            fixtures.add(singleFixture);
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

        System.out.println("Converted to : " + fixture);

        return fixture;
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

    public String getKeyFromFixture (Fixture fixture) {
        return String.format("%d-%s-%s-%s",
                fixture.getAttributes().getSeasonNumber(),
                fixture.getAttributes().getDivisionId(),
                fixture.getAttributes().getHomeTeamId(),
                fixture.getAttributes().getAwayTeamId());
    }

    private void createSeasonIfNotExists(String season) {
        dataService.createSeasonIfNotExists(season);
    }


    //TODO Create Team Structure from Fixtures (or can we do this in the loadAllFixturesForSeason
    // Maybe call it initialiseSeason


}
