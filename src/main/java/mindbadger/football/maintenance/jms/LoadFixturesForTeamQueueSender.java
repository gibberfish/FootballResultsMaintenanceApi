package mindbadger.football.maintenance.jms;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class LoadFixturesForTeamQueueSender {
    private Logger logger = Logger.getLogger(LoadFixturesForTeamQueueSender.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    private final String destination;

    @Autowired
    public LoadFixturesForTeamQueueSender(@Value("${queue.load.fixtures.for.team}") String destination) {
        this.destination = destination;
    }

    public void send (String seasonId, Integer webFixtureTeamId) {
        logger.debug("Writing to " + destination + " JMS queue, seasonId: " + seasonId + ", webFixtureTeamId: " + webFixtureTeamId);

        String message = seasonId + "," + webFixtureTeamId;

        jmsTemplate.convertAndSend(destination, message);
    }
}
