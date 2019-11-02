package mindbadger.football.maintenance.api.cache;

import mindbadger.football.maintenance.api.dataservice.FixtureDataService;
import mindbadger.football.maintenance.api.dataservice.TeamStatisticsDataService;
import mindbadger.football.maintenance.model.Fixture.Fixture;
import mindbadger.football.maintenance.model.fixturedate.FixtureDate;
import mindbadger.football.maintenance.model.teamstatistics.TeamStatistics;
import mindbadger.football.maintenance.util.ApplicationContextProvider;
import org.apache.log4j.Logger;

import java.util.*;

public class FixtureDateCache {
    Logger logger = Logger.getLogger(FixtureDateCache.class);

    private final FixtureDate fixtureDate;
    private Map<String, TeamStatistics> teamStatistics = new TreeMap<>();
    private Map<String, Fixture> fixtures = new TreeMap<>();

    private FixtureDataService fixtureDataService;

    private TeamStatisticsDataService teamStatisticsDataService;

    public FixtureDateCache (FixtureDate fixtureDate) {
        logger.debug("Create new FixtureDateCache for " + fixtureDate.getId());

        this.fixtureDate = fixtureDate;

        teamStatisticsDataService = (TeamStatisticsDataService) ApplicationContextProvider.getApplicationContext().getBean("teamStatisticsDataService");
        fixtureDataService = (FixtureDataService) ApplicationContextProvider.getApplicationContext().getBean("fixtureDataService");

        final String fixturesUrl = fixtureDate.getRelationships().getFixtures().getLinks().getRelated();
        List<Fixture> fixturesList = fixtureDataService.getFixturesFromHyperlink(fixturesUrl);

        logger.debug("...got " + fixturesList.size() + " fixtures");

        for (Fixture fixture : fixturesList) {
            logger.debug("Adding fixture " + fixture.getUniqueCompositeKey());
            fixtures.put(fixture.getUniqueCompositeKey(), fixture);
        }

        final String teamStatisticsUrl = fixtureDate.getRelationships().getTeamStatistics().getLinks().getRelated();
        List<TeamStatistics> teamStatisticsList = teamStatisticsDataService.getTeamStatisticsFromHyperlink(teamStatisticsUrl);

        logger.debug("...got " + teamStatisticsList.size() + " teams statistics");

        for (TeamStatistics teamStatistic : teamStatisticsList) {
            logger.debug("Adding team statistics " + teamStatistic.getId());
            teamStatistics.put(teamStatistic.getId(), teamStatistic);
        }
    }

    public void saveTeamStatistics (TeamStatistics teamStatistic) {
        teamStatisticsDataService.saveTeamStatistics(teamStatistic);
        teamStatistics.put(teamStatistic.getId(), teamStatistic);
    }

    public void deleteTeamStatisticsForThisDate () {
        teamStatisticsDataService.deleteTeamStatistics(fixtureDate.getRelationships().getTeamStatistics().getLinks().getRelated());
        teamStatistics = new TreeMap<>();
    }

    public Set<Fixture> getFixtures () {
        return new HashSet<Fixture> (fixtures.values());
    }

    public boolean areAllFixturesPlayed () {
        boolean allPlayed = true;
        for (Map.Entry<String, Fixture> stringFixtureEntry : fixtures.entrySet()) {
            if (stringFixtureEntry.getValue().getAttributes().getHomeGoals() == null) {
                allPlayed = false;
                continue;
            }
        }
        return allPlayed;
    }

    public boolean hasStatisticis () {
        boolean hasStatistics = false;
        for (Map.Entry<String, TeamStatistics> stringTeamStatisticsEntry : teamStatistics.entrySet()) {
            if (stringTeamStatisticsEntry.getValue().getAttributes().getStatistics().size() > 0) {
                hasStatistics = true;
                continue;
            }
        }
        return hasStatistics;
    }
}
