package mindbadger.football.maintenance.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mindbadger.football.maintenance.model.webreaderfixture.WebReaderFixture;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class PrepareWebFixtureQueueSender {
    private Logger logger = Logger.getLogger(PrepareWebFixtureQueueSender.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    private final String destination;

    @Autowired
    public PrepareWebFixtureQueueSender(@Value("${queue.prepare.web.fixture}") String destination) {
        this.destination = destination;
    }

    public void send (WebReaderFixture webReaderFixture) throws JsonProcessingException {
        logger.debug("Writing to " + destination + " JMS queue, fixtureDate: " + webReaderFixture);

        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(webReaderFixture);

        jmsTemplate.convertAndSend(destination, message);
    }
}
