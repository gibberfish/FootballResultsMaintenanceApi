package mindbadger.football.maintenance.api.cache;

import mindbadger.football.maintenance.api.dataservice.FixtureDateDataService;
import mindbadger.football.maintenance.model.fixturedate.FixtureDate;
import mindbadger.football.maintenance.model.seasondivision.SeasonDivision;
import mindbadger.football.maintenance.model.teamstatistics.TeamStatistics;
import mindbadger.football.maintenance.util.ApplicationContextProvider;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class SeasonDivisionCache {
    Logger logger = Logger.getLogger(SeasonDivisionCache.class);

    private Map<String, FixtureDateCache> fixtureDates = new TreeMap<>();

    private FixtureDateDataService fixtureDateDataService;

    public SeasonDivisionCache (SeasonDivision seasonDivision) {
        logger.debug("Create new SeasonDivisionCache for " + seasonDivision.getUniqueKey());

        fixtureDateDataService = (FixtureDateDataService) ApplicationContextProvider.getApplicationContext().getBean("fixtureDateDataService");

        final String fixtureDatesUrl = seasonDivision.getRelationships().getFixtureDates().getLinks().getRelated();
        List<FixtureDate> fixtureDateList = fixtureDateDataService.getFixtureDatesFromHyperlink(fixtureDatesUrl);

        logger.debug("...got " + fixtureDateList.size() + " fixture dates");

        for (FixtureDate fixtureDate : fixtureDateList) {
            FixtureDateCache fixtureDateCache = new FixtureDateCache(fixtureDate);
            logger.debug("Adding fixture date cache for " + fixtureDate.getAttributes().getFixtureDate());
            fixtureDates.put(fixtureDate.getAttributes().getFixtureDate(), fixtureDateCache);
        }
    }

    public Set<String> getFixtureDates () {
        return fixtureDates.keySet();
    }

    public FixtureDateCache getFixtureDateCacheForFixtureDate (String fixtureDate) {
        if (fixtureDates.containsKey(fixtureDate)) {
            logger.debug("getFixtureDateCacheForFixtureDate " + fixtureDate + " found");
            return fixtureDates.get(fixtureDate);
        } else {
            logger.debug("getFixtureDateCacheForFixtureDate " + fixtureDate + " not found");
            //TODO what should happen here?
            //return initialise(fixtureDate);
            throw new IllegalArgumentException("Fixture date " + fixtureDate + " does not exist");
        }
    }

    public String getLastPlayedFixtureDate () {
        //TODO Unit tests should prove whether things are correctly done in order
        String lastPlayedFixtureDate = null;
        for (Map.Entry<String, FixtureDateCache> stringFixtureDateEntry : fixtureDates.entrySet()) {
            if (!stringFixtureDateEntry.getValue().areAllFixturesPlayed()) {
                return lastPlayedFixtureDate;
            } else {
                lastPlayedFixtureDate = stringFixtureDateEntry.getKey();
            }
        }
        return lastPlayedFixtureDate;
    }

    public String getLastFixtureDateWithStats () {
        //TODO Unit tests should prove whether things are correctly done in order
        String lastFixtureDateWithStats = null;
        for (Map.Entry<String, FixtureDateCache> stringFixtureDateEntry : fixtureDates.entrySet()) {
            if (!stringFixtureDateEntry.getValue().hasStatisticis()) {
                return lastFixtureDateWithStats;
            } else {
                lastFixtureDateWithStats = stringFixtureDateEntry.getKey();
            }
        }
        return lastFixtureDateWithStats;
    }
}
