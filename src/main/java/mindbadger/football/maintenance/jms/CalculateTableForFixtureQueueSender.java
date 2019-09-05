package mindbadger.football.maintenance.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mindbadger.football.maintenance.model.Fixture.Fixture;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class CalculateTableForFixtureQueueSender {
    private Logger logger = Logger.getLogger(CalculateTableForFixtureQueueSender.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    private final String destination;

    @Autowired
    public CalculateTableForFixtureQueueSender(@Value("${queue.calculate.table.for.fixture}") String destination) {
        this.destination = destination;
    }

    public void send (Fixture fixture) throws JsonProcessingException {
        logger.debug("Writing to " + destination + " JMS queue, fixture: " + fixture);

        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(fixture);

        jmsTemplate.convertAndSend(destination, message);
    }
}
