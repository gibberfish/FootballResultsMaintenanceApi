package mindbadger.football.maintenance.api.calculatestatistics;

import mindbadger.football.maintenance.api.cache.FixtureDateCache;
import mindbadger.football.maintenance.api.cache.SeasonCache;
import mindbadger.football.maintenance.api.cache.SeasonDivisionCache;
import mindbadger.football.maintenance.api.dataservice.FixtureDataService;
import mindbadger.football.maintenance.api.dataservice.FixtureDateDataService;
import mindbadger.football.maintenance.api.dataservice.SeasonDivisionTeamDataService;
import mindbadger.football.maintenance.api.dataservice.TeamStatisticsDataService;
import mindbadger.football.maintenance.model.Fixture.Fixture;
import mindbadger.football.maintenance.model.fixturedate.FixtureDate;
import mindbadger.football.maintenance.model.teamstatistics.TeamStatistics;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Component
public class CalculateStatisticsService {
    Logger logger = Logger.getLogger(CalculateStatisticsService.class);
    
    
    //TODO PROBABLY DON'T NEED THESE IF WE USE THE CACHE!...
    @Autowired
    private FixtureDataService fixtureDataService;

    @Autowired
    private FixtureDateDataService fixtureDateDataService;

    @Autowired
    private TeamStatisticsDataService teamStatisticsDataService;

    @Autowired
    private SeasonDivisionTeamDataService seasonDivisionTeamDataService;



    @Autowired
    private SeasonCache seasonCache;


    public void calculateStatisticsForFixture(Fixture fixtureToCalculateFor) throws ParseException {
        String seasonDivisionId = fixtureToCalculateFor.getAttributes().getSeasonNumber() + "_" + fixtureToCalculateFor.getAttributes().getDivisionId();
        String incomingFixtureDate = fixtureToCalculateFor.getAttributes().getFixtureDate();

        logger.debug("Calc Stats for fixture : " + fixtureToCalculateFor.getUniqueCompositeKey());

        SeasonDivisionCache seasonDivisionCache = seasonCache.getCacheForSeasonDivision(seasonDivisionId);

        logger.debug("Last fixture date with stats : " + seasonDivisionCache.getLastFixtureDateWithStats());
        logger.debug("Last played fixture date : " + seasonDivisionCache.getLastPlayedFixtureDate());

        Set<String> fixtureDates = seasonDivisionCache.getFixtureDates();

        FixtureDateCache lastFixtureDateCache = null;
        for (String fixtureDate : fixtureDates) {
            FixtureDateCache fixtureDateCache = seasonDivisionCache.getFixtureDateCacheForFixtureDate(fixtureDate);

            logger.debug("Next fixture date : " + fixtureDate);

            if (fixtureDate.compareTo(incomingFixtureDate) > -1) {
                logger.debug("  This date is on or after the incoming date");

                if (!fixtureDateCache.areAllFixturesPlayed() && !fixtureDateCache.hasStatisticis()) {
                    logger.debug("  Nothing left to do, so exiting...");
                    break;
                }

                if (fixtureDateCache.hasStatisticis()) {
                    logger.debug("  It has existing stats, so delete them first");
                    fixtureDateCache.deleteTeamStatisticsForThisDate();
                }

                if (fixtureDateCache.areAllFixturesPlayed()) {
                    logger.debug("  Calculate stats");

                    Set<Fixture> fixtures = fixtureDateCache.getFixtures();

                    for (Fixture fixture : fixtures) {
                        TeamStatistics teamStatistics = new TeamStatistics();
                        teamStatistics.setId(fixture.getAttributes().getSeasonNumber() + "_" +
                                fixture.getAttributes().getDivisionId() + "_" +
                                fixture.getAttributes().getHomeTeamId() + "_" +
                                fixture.getAttributes().getFixtureDate());

                        List<TeamStatistics.Statistic> statistics = new ArrayList<>();
                        TeamStatistics.Statistic statistic = new TeamStatistics.Statistic("a",45);
                        TeamStatistics.Statistic statistic2 = new TeamStatistics.Statistic("c",70);
                        statistics.add(statistic);
                        statistics.add(statistic2);

//                    Map<String,String> statistics = new HashMap<>();
//                    statistics.put("fixId", fixture.getId());
                        teamStatistics.getAttributes().setStatistics(statistics);

                        fixtureDateCache.saveTeamStatistics(teamStatistics);
                    }
                }
            }

            lastFixtureDateCache = fixtureDateCache;
        }
    }

    public void calculateStatisticsForFixtureOLD(Fixture fixtureToCalculateFor) throws ParseException {

        String seasonDivisionId = fixtureToCalculateFor.getAttributes().getSeasonNumber() + "_" + fixtureToCalculateFor.getAttributes().getDivisionId();

        //List<FixtureDate> fixtureDates =  fixtureDateDataService.getFixtureDatesGreaterThanOrEqualToDate(seasonDivisionId, fixtureToCalculateFor.getAttributes().getFixtureDate());
        List<FixtureDate> fixtureDates =  fixtureDateDataService.getFixtureDates(seasonDivisionId);

        logger.debug("fixtureDate count = "  + fixtureDates.size());

        FixtureDate previousFixtureDate = null;

        for (FixtureDate fixtureDate : fixtureDates) {

            if (fixtureDate.getAttributes().getFixtureDate().compareTo(fixtureToCalculateFor.getAttributes().getFixtureDate()) >= 0) {
                continue;
            }

            logger.debug(".... " + fixtureDate.getAttributes().getFixtureDate());


            final String fixturesUrl = fixtureDate.getRelationships().getFixtures().getLinks().getRelated();
            List<Fixture> fixtures = fixtureDataService.getFixturesFromHyperlink(fixturesUrl);
            logger.debug(".... .... fixtures...." + fixtures.size());

            final String teamStatisticsUrl = fixtureDate.getRelationships().getTeamStatistics().getLinks().getRelated();
            List<TeamStatistics> teamStatisticsList = teamStatisticsDataService.getTeamStatisticsFromHyperlink(teamStatisticsUrl);
            logger.debug(".... .... teamStatisticsList...." + teamStatisticsList.size());


            boolean played = true;
            for (Fixture fixture : fixtures) {
                if (fixture.getAttributes().getHomeGoals() == null) {
                    played = false;
                    break;
                }
            }

            boolean hasStats = false;
            for (TeamStatistics teamStatistics : teamStatisticsList) {
                if (teamStatistics.getAttributes().getStatistics().size() > 0) {
                    hasStats = true;
                    break;
                }
            }

            //TODO Cater for a fixture out of sequence - find the latest played fixture date
            //TODO A

            if (hasStats) {
                logger.debug("Remove the old statistics");

                for (TeamStatistics teamStatistics : teamStatisticsList) {
                    String selfUrl = teamStatistics.getLinks().getSelf();
                    teamStatisticsDataService.deleteTeamStatistics(selfUrl);
                }
            }

            if (played) {
                logger.debug("Calculate new statisitics");

                // http://localhost:1972/dataapi/seasonDivisions/2017_2/fixtureDates?filter[fixtureDate][GT]=2017-12-31
                //List<FixtureDate> fixtureDates = fixtureDateDataService.getFixtureDates(seasonDivisionId);


                /* Get the statistics for the previous fixture date
                    Get fixture dates
                    Loop through fixture dates in order
                    When we get to fixture date, go back one

                    Get the teams for the season division

                    If exists, get the statistics for each of the teams in the division on the date
                    Convert the statistics into a table

                    If not exists, then create an empty starting table



                */

                //TODO Initially just add a dummy statistic, one for each team on the date
                for (Fixture fixture : fixtures) {
                    TeamStatistics teamStatistics = new TeamStatistics();
                    teamStatistics.setId(fixture.getAttributes().getSeasonNumber() + "_" +
                            fixture.getAttributes().getDivisionId() + "_" +
                            fixture.getAttributes().getHomeTeamId() + "_" +
                            fixture.getAttributes().getFixtureDate());

                    List<TeamStatistics.Statistic> statistics = new ArrayList<>();
                    TeamStatistics.Statistic statistic = new TeamStatistics.Statistic("a",45);
                    TeamStatistics.Statistic statistic2 = new TeamStatistics.Statistic("c",70);
                    statistics.add(statistic);
                    statistics.add(statistic2);

//                    Map<String,String> statistics = new HashMap<>();
//                    statistics.put("fixId", fixture.getId());
                    teamStatistics.getAttributes().setStatistics(statistics);

                    teamStatisticsDataService.saveTeamStatistics(teamStatistics);
                }

                previousFixtureDate = fixtureDate;
            }

            if (!hasStats && !played) {
                logger.debug("WE'RE DONE!!!");
                break;
            }
        }

        /*


        Get FIRST un-played fixture date for division
        Choose the EARLIEST of the date above of the date of the fixture
        Get the last played fixture date before this
        Get the team statistics from this date into LAST_STATS

        Get all fixture dates >= the date chosen above

        NO_STATS = FALSE
        CALCULATE_TABLE = TRUE
        Loop if NO_STATS = FALSE and CALCULATE_TABLE = TRUE
            Get statistic for the new date
            Do any exist?
                N : NO_STATS = TRUE
                Y : delete the statistics
            Is CALCULATE_TABLE?
                Y : Get Fixtures on the new date
                    Are they all played?
                        N: CALCULATE_TABLE = FALSE
                        Y: Calculate new stats, based upon LAST_STATS and save them
                            Save the calculated stats in LAST_STATS
         End Loop






http://localhost:1972/dataapi/seasonDivisions/2017_2/fixtureDates?filter[fixtureDate][GE]=2019-08-31


        Get all fixtures for division - put into a map, keyed on fixture date, containing a list of fixtures on that date
        Add this map to another map keyed on ssn_div:
        Map<String,Map<String,List<Fixture>>>

        Are there any unplayed fixtures for this division on an earlier date? If so, return
        Get all fixtures on the fixture date for the division. If any fixtures other than this one are unplayed, return

        Delete all statistics calculated for division after and including this fixture date

        Get last date on which statistics have been calculated

        Loop through each fixture date later than the last calculated date and up to and including
        the last date on which fixtures are all played

         For each fixture date
            Retrieve the statistics for the previous date
            Get fixtures on date (all should be played)
            Calculate the next set of stats (using decorators)
            Save the statistics to the DB
         */
    }
}
