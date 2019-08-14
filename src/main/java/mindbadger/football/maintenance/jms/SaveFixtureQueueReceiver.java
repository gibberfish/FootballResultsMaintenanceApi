package mindbadger.football.maintenance.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import mindbadger.football.maintenance.model.Fixture.Fixture;
import org.apache.log4j.Logger;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SaveFixtureQueueReceiver {
    private Logger logger = Logger.getLogger(SaveFixtureQueueReceiver.class);

    @JmsListener(destination = "${queue.save.fixture}")
    public void receive(String fixtureJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Fixture fixture = objectMapper.readValue(fixtureJson, Fixture.class);

        logger.debug("Got message from 'save fixture' queue: web fixture = " + fixtureJson);
    }
}
