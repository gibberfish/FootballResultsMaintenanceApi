package mindbadger.football.maintenance.jms;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class Sender {
    private Logger logger = Logger.getLogger(Sender.class);
    @Autowired
    private JmsTemplate jmsTemplate;

    public void send (String destination, String message) {
        logger.debug("Writing to JMS queue " + destination + ", message: " + message);
        jmsTemplate.convertAndSend(destination, message);
    }
}
