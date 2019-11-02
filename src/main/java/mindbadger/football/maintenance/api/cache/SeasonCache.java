package mindbadger.football.maintenance.api.cache;

import mindbadger.football.maintenance.api.dataservice.SeasonDivisionTeamDataService;
import mindbadger.football.maintenance.model.seasondivision.SeasonDivision;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/*
This class assumes that the season division team structure has already been setup separately
 */
@Component
public class SeasonCache {
    Logger logger = Logger.getLogger(SeasonCache.class);

    @Autowired
    private SeasonDivisionTeamDataService seasonDivisionTeamDataService;

    //Keyed on season_division (e.g. 2019_4)
    private Map<String, SeasonDivisionCache> seasonDivisions = new HashMap<>();

    public SeasonDivisionCache getCacheForSeasonDivision (String seasonDivision) {
        if (seasonDivisions.containsKey(seasonDivision)) {
            logger.debug("getCacheForSeasonDivision " + seasonDivision + " found");
            return seasonDivisions.get(seasonDivision);
        } else {
            logger.debug("getCacheForSeasonDivision " + seasonDivision + " not found");
            return initialise(seasonDivision);
        }
    }

    public SeasonDivisionCache initialise (String seasonDivisionId) {
        logger.debug("initialise " + seasonDivisionId);
        SeasonDivision seasonDivision = seasonDivisionTeamDataService.getSeasonDivision(seasonDivisionId);

        return new SeasonDivisionCache(seasonDivision);
    }
}
