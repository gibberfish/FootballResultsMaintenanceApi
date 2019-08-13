package mindbadger.football.maintenance.api.controller;

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

    @GetMapping(value = "/loadRecentResultsOLD", produces = "application/json")
    public ResponseEntity<String> loadRecentResultsOLD () {
        logger.info("Load Recent Results");

        loadRecentResultsService.loadRecentResultsOLD();

        return new ResponseEntity<>("Load Recent Results Complete", HttpStatus.OK);
    }


    @GetMapping(value = "/initialiseSeason", produces = "application/json")
    public ResponseEntity<String> loadAllFixturesForSeason (@RequestParam(value="season") String season) {
        logger.info("Initialise Season");

        initialiseSeasonService.initialiseSeason(season);

        return new ResponseEntity<>("Initialise Team Complete", HttpStatus.OK);
    }

    @GetMapping(value = "/loadRecentResults", produces = "application/json")
    public ResponseEntity<String> loadRecentResults () {
        logger.info("Load Recent Results (using queues)");

        loadRecentResultsService.loadRecentResults();

        return new ResponseEntity<>("Load Recent Results Complete", HttpStatus.OK);
    }

}
