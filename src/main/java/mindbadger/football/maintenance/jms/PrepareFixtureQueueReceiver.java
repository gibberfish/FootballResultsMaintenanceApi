package mindbadger.football.maintenance.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import mindbadger.football.maintenance.api.loadresults.LoadResultsService;
import mindbadger.football.maintenance.model.Fixture.Fixture;
import mindbadger.football.maintenance.model.webreaderfixture.WebReaderFixture;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
example message that fails
{"seasonId":2019,"fixtureDate":"2019-08-31","divisionId":4,"divisionName":"League Two","homeTeamId":4880,"homeTeamName":"Salford","awayTeamId":1537,"awayTeamName":"Leyton Orient","homeGoals":null,"awayGoals":null}
 */
@Component
public class PrepareFixtureQueueReceiver {
    private Logger logger = Logger.getLogger(PrepareFixtureQueueReceiver.class);

    @Autowired
    private LoadResultsService loadResultsService;

    @Autowired
    private SaveFixtureQueueSender saveFixtureQueueSender;

    @JmsListener(destination = "${queue.prepare.fixture}")
    public void receive(String fixtureJson) throws IOException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        WebReaderFixture webReaderFixture = objectMapper.readValue(fixtureJson, WebReaderFixture.class);

        logger.debug("Got message from 'prepare fixture' queue: web webReaderFixture = " + fixtureJson);

        // This will ensure the season is setup to receive this fixture
        // This will convert the web reader fixture to a fixture
        // This will place the fixture on the save fixture queue

        Fixture fixture = loadResultsService.prepareWebReaderFixture(webReaderFixture);

        saveFixtureQueueSender.send(fixture);
    }
}
