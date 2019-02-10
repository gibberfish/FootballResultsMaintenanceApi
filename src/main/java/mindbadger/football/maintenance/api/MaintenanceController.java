package mindbadger.football.maintenance.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
//@RequestMapping("maintenance")
public class MaintenanceController {

    @GetMapping(value = "/maintenance/test")
    public String loadAllFixturesForSeason () {
        System.out.println("test endpoint hit");
        return "jobstatusviewer";
    }

}
