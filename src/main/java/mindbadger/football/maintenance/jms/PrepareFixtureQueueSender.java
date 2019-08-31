package mindbadger.football.maintenance.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mindbadger.football.maintenance.model.Fixture.Fixture;
import mindbadger.football.maintenance.model.webreaderfixture.WebReaderFixture;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class PrepareFixtureQueueSender {
    private Logger logger = Logger.getLogger(PrepareFixtureQueueSender.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    private final String destination;

    @Autowired
    public PrepareFixtureQueueSender(@Value("${queue.prepare.fixture}") String destination) {
        this.destination = destination;
    }

    public void send (WebReaderFixture fixture) throws JsonProcessingException {
        logger.debug("Writing to " + destination + " JMS queue, webReaderFixture: " + fixture);

        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(fixture);

        jmsTemplate.convertAndSend(destination, message);
    }
}
