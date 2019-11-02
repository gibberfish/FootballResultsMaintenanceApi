package mindbadger.football.maintenance.api.controller;

import mindbadger.football.maintenance.api.cache.SeasonCache;
import mindbadger.football.maintenance.api.cache.SeasonDivisionCache;
import mindbadger.football.maintenance.api.initialiseseason.InitialiseSeasonService;
import mindbadger.football.maintenance.api.loadrecent.LoadRecentResultsService;
import mindbadger.football.maintenance.jms.LoadFixturesForDateQueueSender;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
@RequestMapping("maintenance")
public class MaintenanceController {
    Logger logger = Logger.getLogger(MaintenanceController.class);

    @Autowired
    private LoadFixturesForDateQueueSender sender;

    @Autowired
    private InitialiseSeasonService initialiseSeasonService;

    @Autowired
    private LoadRecentResultsService loadRecentResultsService;

    @Autowired
    private SeasonCache seasonCache;

    @GetMapping(value = "/loadRecentResultsOLD", produces = "application/json")
    public ResponseEntity<String> loadRecentResultsOLD () {
        logger.info("Load Recent Results");

        loadRecentResultsService.loadRecentResultsOLD();

        return new ResponseEntity<>("Load Recent Results Complete", HttpStatus.OK);
    }


    @GetMapping(value = "/initialiseSeason", produces = "application/json")
    public ResponseEntity<String> loadAllFixturesForSeason (@RequestParam(value="season") String season) {
        logger.info("Initialise Season");

        try {
            initialiseSeasonService.initialiseSeason(season);
            return new ResponseEntity<>("Initialise Season Complete", HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error in Initialise Season", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/loadRecentResults", produces = "application/json")
    public ResponseEntity<String> loadRecentResults () {
        logger.info("Load Recent Results (using queues)");

        loadRecentResultsService.loadRecentResults();

        return new ResponseEntity<>("Load Recent Results Complete", HttpStatus.OK);
    }

    @GetMapping(value = "/test", produces = "application/json")
    public ResponseEntity<String> test () {
        logger.info("TEST!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        SeasonDivisionCache seasonDivisionCache = seasonCache.getCacheForSeasonDivision("2017_2");

        logger.info("TEST DONE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        String lastPlayedDate = seasonDivisionCache.getLastPlayedFixtureDate();
        logger.debug("Last played date : "  + lastPlayedDate);

        String lastDateWithStats = seasonDivisionCache.getLastFixtureDateWithStats();
        logger.debug("Last date with stats : " + lastDateWithStats);

        return new ResponseEntity<>("TEST Complete", HttpStatus.OK);
    }

}
