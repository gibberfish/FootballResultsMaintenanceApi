package mindbadger.football.maintenance.api.loadresults;

import com.fasterxml.jackson.core.JsonProcessingException;
import mindbadger.football.maintenance.api.MappingCache;
import mindbadger.football.maintenance.api.dataservice.FixtureDataService;
import mindbadger.football.maintenance.api.rest.ExternalServiceInvocationException;
import mindbadger.football.maintenance.api.webreader.WebReaderFixtureFixtureConverter;
import mindbadger.football.maintenance.api.webreader.WebReaderService;
import mindbadger.football.maintenance.jms.PrepareFixtureQueueReceiver;
import mindbadger.football.maintenance.jms.PrepareFixtureQueueSender;
import mindbadger.football.maintenance.jms.SaveFixtureQueueSender;
import mindbadger.football.maintenance.model.Fixture.Fixture;
import mindbadger.football.maintenance.model.webreaderfixture.WebReaderFixture;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoadResultsService {
    private Logger logger = Logger.getLogger(LoadResultsService.class);

    @Autowired
    private WebReaderService webReaderService;

    @Autowired
    private FixtureDataService fixtureDataService;

    @Autowired
    WebReaderFixtureFixtureConverter webReaderFixtureFixtureConverter;

    @Autowired
    private SaveFixtureQueueSender saveFixtureQueueSender;

    @Autowired
    private PrepareFixtureQueueSender prepareFixtureQueueSender;

    @Autowired
    private MappingCache mappingCache;

    public void loadResultsForDate (String fixtureDate) throws JsonProcessingException, ExternalServiceInvocationException {
        final List<WebReaderFixture> webReaderFixturesForDate = webReaderService.getFixturesForDate(fixtureDate);
        List<Fixture> newFixtures = webReaderFixtureFixtureConverter.convertWebReaderFixturesToFixtures(webReaderFixturesForDate);
        List<Fixture> existingFixtures = fixtureDataService.getFixturesOnDate(fixtureDate);
        saveFixtures(newFixtures, existingFixtures);
    }

    public void loadFixturesForTeam (String season, String webFixtureTeamId) throws JsonProcessingException {
        final List<WebReaderFixture> fixturesForTeam = webReaderService.getFixturesForTeam(season, webFixtureTeamId);

        for (WebReaderFixture webReaderFixture : fixturesForTeam) {
            prepareFixtureQueueSender.send(webReaderFixture);
        }


        //TODO Remove this, once the fixtures are handled by a queue
//        List<Fixture> newFixtures = webReaderFixtureFixtureConverter.convertWebReaderFixturesToFixtures(fixturesForTeam);
//        List<Fixture> existingFixtures = fixtureDataService.getFixturesForTeam(season, mappingCache.getMappedTeams().get(webFixtureTeamId));
//        saveFixtures(newFixtures, existingFixtures);
    }

    public Fixture prepareWebReaderFixture (WebReaderFixture webReaderFixture) throws ExternalServiceInvocationException {
        //TODO call the webReaderFixtureFixtureConverter
        logger.debug("About to preare Web Reader Fixture : " + webReaderFixture);

        webReaderFixtureFixtureConverter.ensureSeasonDivisionTeamStructureExistsForWebFixture(webReaderFixture);
        Fixture fixture = webReaderFixtureFixtureConverter.convertWebFixtureToFixture(webReaderFixture);

        return fixture;
    }

    //TODO Do all of this within the save fixture queue receiever
    private void saveFixtures (List<Fixture> newFixtures, List<Fixture> existingFixtures) throws JsonProcessingException {

        for (Fixture newFixture : newFixtures) {
            int index = existingFixtures.indexOf(newFixture);

            if (index != -1) {
                // Update the existing fixture
                Fixture existingFixture = existingFixtures.get(index);
                existingFixture.getAttributes().setFixtureDate(newFixture.getAttributes().getFixtureDate());
                existingFixture.getAttributes().setHomeGoals(newFixture.getAttributes().getHomeGoals());
                existingFixture.getAttributes().setAwayGoals(newFixture.getAttributes().getAwayGoals());

                logger.debug("Sending " + existingFixture + " to save fixture queue for update");
                saveFixtureQueueSender.send(existingFixture);
                existingFixtures.remove(index);
            } else {
                // Create new fixture
                logger.debug("Sending " + newFixture + " to save fixture queue for creation");
                saveFixtureQueueSender.send(newFixture);
            }
        }

        // If any existing fixtures remain, then they have been postponed and the date needs resetting
        for (Fixture existingFixture : existingFixtures) {
            existingFixture.getAttributes().setFixtureDate(null);
            existingFixture.getAttributes().setHomeGoals(null);
            existingFixture.getAttributes().setAwayGoals(null);

            logger.debug("Sending " + existingFixture + " to save fixture queue for removal of fixture date (postponed)");
            saveFixtureQueueSender.send(existingFixture);
        }
    }
}
