package mindbadger.football.maintenance.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import mindbadger.football.maintenance.api.dataservice.FixtureDataService;
import mindbadger.football.maintenance.api.webreader.WebReaderFixtureFixtureConverter;
import mindbadger.football.maintenance.api.webreader.WebReaderService;
import mindbadger.football.maintenance.model.Fixture.Fixture;
import mindbadger.football.maintenance.model.webreaderfixture.WebReaderFixture;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoadFixturesForDateQueueReceiver {
    private Logger logger = Logger.getLogger(LoadFixturesForDateQueueReceiver.class);

    @Autowired
    private WebReaderService webReaderService;

    @Autowired
    private FixtureDataService fixtureDataService;

    @Autowired
    WebReaderFixtureFixtureConverter webReaderFixtureFixtureConverter;

    @Autowired
    private SaveFixtureQueueSender saveFixtureQueueSender;

    @JmsListener(destination = "${queue.load.fixtures.for.date}")
    public void receive(String fixtureDate) throws JsonProcessingException {
        logger.debug("Got message from 'load fixtures for date' queue: fixture date = " + fixtureDate);

        final List<WebReaderFixture> webReaderFixturesForDate = webReaderService.getFixturesForDate(fixtureDate);
        List<Fixture> newFixtures = webReaderFixtureFixtureConverter.convertWebReaderFixturesToFixtures(webReaderFixturesForDate);

        List<Fixture> existingFixtures = fixtureDataService.getFixturesOnDate(fixtureDate);

//        for (int i=0; i<newFixtures.size(); i++) { // Use traditional loop to avoid CME
//            Fixture newFixture = newFixtures.get(i);
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
