package mindbadger.football.maintenance.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import mindbadger.football.maintenance.api.webreader.WebReaderService;
import mindbadger.football.maintenance.model.webreaderfixture.WebReaderFixture;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoadFixturesForDateQueueReceiver {
    private Logger logger = Logger.getLogger(LoadFixturesForDateQueueReceiver.class);

    @Autowired
    private WebReaderService webReaderService;

    @Autowired
    private PrepareWebFixtureQueueSender prepareWebFixtureQueueSender;

    @JmsListener(destination = "${queue.load.fixtures.for.date}")
    public void receive(String fixtureDate) throws JsonProcessingException {
        logger.debug("Got message from 'load fixtures for date' queue: fixture date = " + fixtureDate);

        // Use a lambda to make this more elegant...
        for (WebReaderFixture webReaderFixture : webReaderService.getFixturesForDate(fixtureDate)){
            logger.debug("Sending " + webReaderFixture + " to prepare web fixture queue");
            prepareWebFixtureQueueSender.send(webReaderFixture);
        }
    }
}
