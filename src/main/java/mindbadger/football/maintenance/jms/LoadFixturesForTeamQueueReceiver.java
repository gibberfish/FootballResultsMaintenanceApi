package mindbadger.football.maintenance.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import mindbadger.football.maintenance.api.loadresults.LoadResultsService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.StringTokenizer;

@Component
public class LoadFixturesForTeamQueueReceiver {
    private Logger logger = Logger.getLogger(LoadFixturesForTeamQueueReceiver.class);

    @Autowired
    private LoadResultsService loadResultsService;

    @JmsListener(destination = "${queue.load.fixtures.for.team}")
    public void receive(String message) throws JsonProcessingException {
        StringTokenizer tokenizer = new StringTokenizer(message, ",");
        String season = tokenizer.nextToken();
        String teamId = tokenizer.nextToken();

        logger.debug("Got message from 'load fixtures for team' queue: season= " + season + ", team= " + teamId);

        loadResultsService.loadFixturesForTeam(season, teamId);
    }
}
