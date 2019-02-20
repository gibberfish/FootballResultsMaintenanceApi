package mindbadger.football.maintenance.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("maintenance")
public class MaintenanceController {

    @Autowired
    private DataService dataService;

    @Autowired
    private CurrentSeasonService currentSeasonService;

    @GetMapping(value = "/loadRecentResults", produces = "application/json")
    public ResponseEntity<String> loadRecentResults () {
        System.out.println("test endpoint hit 2");
        int season = currentSeasonService.getCurrentSeason();

        List<String> divisions = dataService.getSeasonDivisions(season);

        divisions.forEach(div -> {
            System.out.println("Division : " + div);

            dataService.getUnplayedFixturesForDivisionBeforeToday(season, div);
        });



        /*
        Get current season
        Get today's date
        Get all tracked divisions from DB
        For each tracked division...
            Get all unplayed fixture dates for the division from DB whose fixture date is less than or equal to today
        For each fixture date...
            Read Fixtures for the date from the web


         */


        return new ResponseEntity<>("Hello", HttpStatus.OK);
    }


    @GetMapping(value = "/loadAllFixturesForSeason", produces = "application/json")
    public ResponseEntity<String> loadAllFixturesForSeason (@RequestParam(value="season") String season) {

        System.out.println("test endpoint hit");

        return new ResponseEntity<>("Hello", HttpStatus.OK);
    }

}
