package mindbadger.football.maintenance.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import mindbadger.football.maintenance.api.dataservice.FixtureDataService;
import mindbadger.football.maintenance.model.Fixture.Fixture;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SaveFixtureQueueReceiver {
    private Logger logger = Logger.getLogger(SaveFixtureQueueReceiver.class);

    @Autowired
    private FixtureDataService fixtureDataService;

    @Autowired
    private CalculateTableForFixtureQueueSender calculateTableForFixtureQueueSender;

    @JmsListener(destination = "${queue.save.fixture}")
    public void receive(String fixtureJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Fixture fixture = objectMapper.readValue(fixtureJson, Fixture.class);

        logger.debug("Got message from 'save fixture' queue: web fixture = " + fixtureJson);

        fixtureDataService.saveFixture(fixture);

        /* TODO Logic to see if table needed here

        (1) Delete all tables up to and including this fixture date
        (2) Get last table date
        (3) Get last played fixture date
        (4) Loop through each fixture date in order from after last table to last played fixture
            (a) Place the season, division and fixture date onto the calculate table queue

         */

        calculateTableForFixtureQueueSender.send(fixture);
    }
}
