package mindbadger.football.maintenance.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import mindbadger.football.maintenance.api.loadresults.LoadResultsService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoadFixturesForDateQueueReceiver {
    private Logger logger = Logger.getLogger(LoadFixturesForDateQueueReceiver.class);

    @Autowired
    private LoadResultsService loadResultsService;

    @JmsListener(destination = "${queue.load.fixtures.for.date}")
    public void receive(String fixtureDate) throws JsonProcessingException, IOException {
        logger.debug("Got message from 'load fixtures for date' queue: fixture date = " + fixtureDate);
        loadResultsService.loadResultsForDate(fixtureDate);
    }
}
