package mindbadger.football.maintenance.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import mindbadger.football.maintenance.api.calculatestatistics.CalculateStatisticsService;
import mindbadger.football.maintenance.api.dataservice.FixtureDataService;
import mindbadger.football.maintenance.model.Fixture.Fixture;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*

Example for the queue
{
            "id": "430663",
            "type": "fixtures",
            "attributes": {
                "divisionId": "8",
                "seasonNumber": 2019,
                "fixtureDate": "2019-08-17",
                "homeTeamId": "14",
                "awayTeamId": "17",
                "homeGoals": 0,
                "awayGoals": 2
            }
}


 */



@Component
public class CalculateTableForFixtureQueueReceiver {
    private Logger logger = Logger.getLogger(CalculateTableForFixtureQueueReceiver.class);

    @Autowired
    private CalculateStatisticsService calculateStatisticsService;

    @JmsListener(destination = "${queue.calculate.table.for.fixture}")
    public void receive(String fixtureJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Fixture fixture = objectMapper.readValue(fixtureJson, Fixture.class);

        logger.debug("Got message from 'calculate table for fixture' queue: fixture = " + fixtureJson);

        calculateStatisticsService.calculateStatisticsForFixture (fixture);
    }
}
