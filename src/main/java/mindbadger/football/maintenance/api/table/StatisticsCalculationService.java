package mindbadger.football.maintenance.api.table;

import mindbadger.football.maintenance.model.Fixture.Fixture;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StatisticsCalculationService {
    private Logger logger = Logger.getLogger(StatisticsCalculationService.class);

    public void updateStatisticsForFixtures (List<Fixture> fixtures) {
        logger.info("Updating Statistics for fixtures");
        //TODO update statistics records

    }
}
