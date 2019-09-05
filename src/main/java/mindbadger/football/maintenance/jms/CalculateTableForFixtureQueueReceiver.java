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
public class CalculateTableForFixtureQueueReceiver {
    private Logger logger = Logger.getLogger(CalculateTableForFixtureQueueReceiver.class);

    @Autowired
    private FixtureDataService fixtureDataService;

    @JmsListener(destination = "${queue.calculate.table.for.fixture}")
    public void receive(String fixtureJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Fixture fixture = objectMapper.readValue(fixtureJson, Fixture.class);

        logger.debug("Got message from 'calculate table for fixture' queue: fixture = " + fixtureJson);

    }
}
