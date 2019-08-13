package mindbadger.football.maintenance.jms;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class LoadFixturesForDateQueueSender {
    private Logger logger = Logger.getLogger(LoadFixturesForDateQueueSender.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    private final String destination;

    @Autowired
    public LoadFixturesForDateQueueSender (@Value("${queue.load.fixtures.for.date}") String destination) {
        this.destination = destination;
    }

    public void send (String fixtureDate) {
        logger.debug("Writing to " + destination + " JMS queue, fixtureDate: " + fixtureDate);
        jmsTemplate.convertAndSend(destination, fixtureDate);
    }
}
