package mindbadger.football.maintenance.api.calculatestatistics;

import mindbadger.football.maintenance.api.dataservice.FixtureDataService;
import mindbadger.football.maintenance.api.dataservice.FixtureDateDataService;
import mindbadger.football.maintenance.api.dataservice.TeamStatisticsDataService;
import mindbadger.football.maintenance.api.rest.ServiceInvoker;
import mindbadger.football.maintenance.api.rest.SimpleResponse;
import mindbadger.football.maintenance.model.Fixture.Fixture;
import mindbadger.football.maintenance.model.fixturedate.FixtureDate;
import mindbadger.football.maintenance.model.teamstatistics.TeamStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;


@Component
public class CalculateStatisticsService {

    @Autowired
    private FixtureDataService fixtureDataService;

    @Autowired
    private FixtureDateDataService fixtureDateDataService;

    @Autowired
    private TeamStatisticsDataService teamStatisticsDataService;

    public void calculateStatisticsForFixture(Fixture fixtureToCalculateFor) {

        String seasonDivisionId = fixtureToCalculateFor.getAttributes().getSeasonNumber() + "_" + fixtureToCalculateFor.getAttributes().getDivisionId();
        List<FixtureDate> fixtureDates =  fixtureDateDataService.getFixtureDatesGreaterThanOrEqualToDate(seasonDivisionId, fixtureToCalculateFor.getAttributes().getFixtureDate());

        System.out.println("fixtureDate count = "  + fixtureDates.size());

        for (FixtureDate fixtureDate : fixtureDates) {
            System.out.println(".... " + fixtureDate.getAttributes().getFixtureDate());


            final String fixturesUrl = fixtureDate.getRelationships().getFixtures().getLinks().getRelated();
            List<Fixture> fixtures = fixtureDataService.getFixturesFromHyperlink(fixturesUrl);
            System.out.println(".... .... fixtures...." + fixtures.size());

            final String teamStatisticsUrl = fixtureDate.getRelationships().getTeamStatistics().getLinks().getRelated();
            List<TeamStatistics> teamStatisticsList = teamStatisticsDataService.getTeamStatisticsFromHyperlink(teamStatisticsUrl);
            System.out.println(".... .... teamStatisticsList...." + teamStatisticsList.size());


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

            if (hasStats) {
                System.out.println("Remove the old statistics");
            }

            if (played) {
                System.out.println("Calculate new statisitics");
            }
        }

        /*


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
