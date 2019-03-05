package mindbadger.football.maintenance.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CurrentSeasonService {
    public Integer getCurrentSeason () {
        LocalDate date = LocalDate.now();
        int year = date.getYear();
        int month = date.getMonthValue();

        if (month < 8) year--;

        //TODO Remove this...
        year = 2017;

        return year;
    }
}
