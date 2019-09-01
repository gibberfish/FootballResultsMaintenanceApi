package mindbadger.football.maintenance.api.webreader;

import mindbadger.football.maintenance.api.MappingCache;
import mindbadger.football.maintenance.api.dataservice.MappingDataService;
import mindbadger.football.maintenance.api.dataservice.SeasonDivisionTeamDataService;
import mindbadger.football.maintenance.model.Fixture.Fixture;
import mindbadger.football.maintenance.model.seasondivision.SeasonDivision;
import mindbadger.football.maintenance.model.seasondivisionteam.SeasonDivisionTeam;
import mindbadger.football.maintenance.model.team.Team;
import mindbadger.football.maintenance.model.webreaderfixture.WebReaderFixture;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class WebReaderFixtureFixtureConverter {
    private Logger logger = Logger.getLogger(WebReaderFixtureFixtureConverter.class);

    @Autowired
    private MappingDataService mappingDataService;

    @Autowired
    private MappingCache mappingCache;

    @Autowired
    private SeasonDivisionTeamDataService seasonDivisionTeamDataService;

    public List<Fixture> convertWebReaderFixturesToFixtures (List<WebReaderFixture> webReaderFixtures) throws IOException {
        List<Fixture> fixtures = new ArrayList<>();
        for (WebReaderFixture webReaderFixture : webReaderFixtures) {
            ensureSeasonDivisionTeamStructureExistsForWebFixture(webReaderFixture);
            fixtures.add (convertWebFixtureToFixture(webReaderFixture));
        }
        return fixtures;
    }

    public Fixture convertWebFixtureToFixture (WebReaderFixture webReaderFixture) {
        String divisionId = mappingCache.getMappedDivisions().get(webReaderFixture.getDivisionId());
        String homeTeamId = mappingCache.getMappedTeams().get(webReaderFixture.getHomeTeamId());
        String awayTeamId = mappingCache.getMappedTeams().get(webReaderFixture.getAwayTeamId());

        // These should never occur if ensureSeasonDivisionTeamStructureExistsForWebFixture has done its job
        if (divisionId == null) throw new IllegalArgumentException("No mapping for div " + webReaderFixture.getDivisionId());
        if (homeTeamId == null) throw new IllegalArgumentException("No mapping for home team " + webReaderFixture.getHomeTeamId());
        if (awayTeamId == null) throw new IllegalArgumentException("No mapping for away team " + webReaderFixture.getAwayTeamId());

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

    public void ensureSeasonDivisionTeamStructureExistsForWebFixture (WebReaderFixture webReaderFixture) throws IOException {
        createSeasonIfNotExists(webReaderFixture.getSeasonId());
        SeasonDivision seasonDivision = createSeasonDivisionIfNotExists(webReaderFixture.getSeasonId(), webReaderFixture.getDivisionId());
        String homeTeamId = createTeamIfNotExists(webReaderFixture.getHomeTeamId(), webReaderFixture.getHomeTeamName());
        String awayTeamId = createTeamIfNotExists(webReaderFixture.getAwayTeamId(), webReaderFixture.getAwayTeamName());
        createSeasonDivisionTeamIfNotExists(seasonDivision, homeTeamId);
        createSeasonDivisionTeamIfNotExists(seasonDivision, awayTeamId);
    }

    private void createSeasonDivisionTeamIfNotExists(SeasonDivision seasonDivision, String teamId) throws IOException {
        SeasonDivisionTeam seasonDivisionTeam = new SeasonDivisionTeam();
        seasonDivisionTeam.getAttributes().setSeasonNumber(seasonDivision.getAttributes().getSeasonNumber());
        seasonDivisionTeam.getAttributes().setDivisionId(seasonDivision.getAttributes().getDivisionId());
        seasonDivisionTeam.getAttributes().setTeamId(teamId);

        SeasonDivisionTeam seasonDivisionTeamRetrieved = seasonDivisionTeamDataService.getSeasonDivisionTeam(seasonDivisionTeam);

        if (seasonDivisionTeamRetrieved == null) {
            logger.info("SeasonDivisionTeam " + seasonDivisionTeam + " doesn't exist, so creating it...");
            seasonDivisionTeamDataService.createSeasonDivisionTeam(seasonDivisionTeam);
        }
    }

    private String createTeamIfNotExists(Integer webReaderTeamId, String teamName) throws IOException {
        String teamId = mappingCache.getMappedTeams().get(webReaderTeamId);
        if (teamId == null) {
            logger.debug("We don't currently have a team mapping for " + teamName + " ("+teamId+")");
            Team team = seasonDivisionTeamDataService.getTeamWithName(teamName);
            if (team == null) {
                team = seasonDivisionTeamDataService.createTeam(teamName);
            }
            teamId = team.getId();
            mappingDataService.createTeamMapping (webReaderTeamId, teamId);
            mappingCache.getMappedTeams().put(webReaderTeamId, teamId);
        }
        logger.debug("Our team ID for web reader id " + webReaderTeamId + " is " + teamId);
        return  teamId;
    }

    private SeasonDivision createSeasonDivisionIfNotExists(Integer season, Integer webReaderDivisionId) throws IOException {
        String divisionId = mappingCache.getMappedDivisions().get(webReaderDivisionId);
        if (divisionId == null) {
            throw new IllegalStateException("You need to manually setup a tracked division for " + webReaderDivisionId);
        }

        SeasonDivision seasonDivision = new SeasonDivision();
        seasonDivision.getAttributes().setSeasonNumber(season);
        seasonDivision.getAttributes().setDivisionId(divisionId);

        SeasonDivision seasonDivisionRetrieved = seasonDivisionTeamDataService.getSeasonDivision(seasonDivision);

        if (seasonDivisionRetrieved == null) {
            logger.info("SeasonDivision " + seasonDivision + " doesn't exist, so creating it...");
            seasonDivisionTeamDataService.createSeasonDivision(seasonDivision);
            //TODO Ideally the create will return an object
            seasonDivisionRetrieved = seasonDivisionTeamDataService.getSeasonDivision(seasonDivision);
        }

        return seasonDivisionRetrieved;
    }

    private void createSeasonIfNotExists(Integer season) throws IOException {
        if (seasonDivisionTeamDataService.getSeason(season.toString()) == null) {
            logger.info("Season " + season + " doesn't exist, so creating it...");
            seasonDivisionTeamDataService.createSeason(season.toString());
        }
    }
}
