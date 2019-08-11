package mindbadger.football.maintenance.jms;

import org.apache.log4j.Logger;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class Receiver {
    private Logger logger = Logger.getLogger(Receiver.class);

    @JmsListener(destination = "${queue.boot}")
    public void receive(String message) {
        logger.debug("Got message from queue: " + message);
    }
}
