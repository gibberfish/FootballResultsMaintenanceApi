package mindbadger.football.maintenance.api.dataservice;

import com.google.gson.Gson;
import mindbadger.football.maintenance.api.rest.ExternalServiceInvocationException;
import mindbadger.football.maintenance.api.rest.HttpListWrapper;
import mindbadger.football.maintenance.api.rest.HttpSingleWrapper;
import mindbadger.football.maintenance.api.rest.ServiceInvoker;
import mindbadger.football.maintenance.model.Fixture.Fixture;
import mindbadger.football.maintenance.model.Fixture.FixturesList;
import mindbadger.football.maintenance.model.Fixture.SingleFixture;
import mindbadger.football.maintenance.model.mapping.Mapping;
import mindbadger.football.maintenance.model.mapping.MappingsList;
import mindbadger.football.maintenance.model.mapping.SingleMapping;
import mindbadger.football.maintenance.model.season.Season;
import mindbadger.football.maintenance.model.season.SingleSeason;
import mindbadger.football.maintenance.model.seasondivision.SeasonDivision;
import mindbadger.football.maintenance.model.seasondivision.SeasonDivisionsList;
import mindbadger.football.maintenance.model.seasondivision.SingleSeasonDivision;
import mindbadger.football.maintenance.model.seasondivisionteam.SeasonDivisionTeam;
import mindbadger.football.maintenance.model.seasondivisionteam.SingleSeasonDivisionTeam;
import mindbadger.football.maintenance.model.team.SingleTeam;
import mindbadger.football.maintenance.model.team.Team;
import mindbadger.football.maintenance.model.trackeddivision.TrackedDivision;
import mindbadger.football.maintenance.model.trackeddivision.TrackedDivisionsList;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class FixtureDataService {
    Logger logger = Logger.getLogger(FixtureDataService.class);

    @Value("${data.api.target}")
    private String dataApiTarget;

    @Autowired
    private ServiceInvoker serviceInvoker;

    public List<Fixture> getFixturesOnDate(String fixtureDate) throws ExternalServiceInvocationException {
        String url = dataApiTarget + "/fixtures";

        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();
        params.put("page[limit]", "10000");
        params.put("filter[fixtureDate][EQ]",fixtureDate);

        HttpListWrapper<FixturesList, Fixture> get = new HttpListWrapper<FixturesList, Fixture>();
        return get.getList(url, ServiceInvoker.APPLICATION_VND_API_JSON, params, FixturesList.class);
    }

    public List<Fixture> getFixturesForTeam(String seasonNumber, String teamId) throws ExternalServiceInvocationException {
        String url = dataApiTarget + "/fixtures";

        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();
        params.put("page[limit]", "10000");
        params.put("filter[homeTeamId][EQ]",teamId);

        HttpListWrapper<FixturesList, Fixture> get = new HttpListWrapper<FixturesList, Fixture>();
        List<Fixture> homeFixtures = get.getList(url, ServiceInvoker.APPLICATION_VND_API_JSON, params, FixturesList.class);
        params.remove("filter[homeTeamId][EQ]");
        params.put("filter[awayTeamId][EQ]", teamId);
        List<Fixture> awayFixtures = get.getList(url, ServiceInvoker.APPLICATION_VND_API_JSON, params, FixturesList.class);

        return Stream.concat(homeFixtures.stream(), awayFixtures.stream()).collect(Collectors.toList());
    }

    public List<Fixture> getUnplayedFixturesForDivisionBeforeToday (String seasonDivisionId) throws ExternalServiceInvocationException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.now();
        String dateString = date.format(formatter);

        String url = dataApiTarget + "/seasonDivisions/" + seasonDivisionId + "/fixtures";

        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();
        params.put("page[limit]", "10000");
        params.put("filter[fixtures][homeGoals][EQ]","null");
        params.put("filter[fixtureDate][LE]", dateString);

        HttpListWrapper<FixturesList, Fixture> get = new HttpListWrapper<FixturesList, Fixture>();
        return get.getList(url, ServiceInvoker.APPLICATION_VND_API_JSON, params, FixturesList.class);
    }

    public List<Fixture> getAllFixturesForSeason (String seasonNumber) throws ExternalServiceInvocationException {
        String url = dataApiTarget + "/fixtures";

        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();
        params.put("page[limit]", "10000");
        params.put("filter[seasonNumber][EQ]",seasonNumber);

        HttpListWrapper<FixturesList, Fixture> get = new HttpListWrapper<FixturesList, Fixture>();
        return get.getList(url, ServiceInvoker.APPLICATION_VND_API_JSON, params, FixturesList.class);
    }

    private Fixture findExistingFixture (Fixture fixture) throws ExternalServiceInvocationException {
        String url = dataApiTarget + "/fixtures";

        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();
        params.put("page[limit]", "10000");
        params.put("filter[seasonNumber][EQ]",Integer.toString(
                fixture.getAttributes().getSeasonNumber()));
        params.put("filter[divisionId][EQ]", fixture.getAttributes().getDivisionId());
        params.put("filter[homeTeamId][EQ]", fixture.getAttributes().getHomeTeamId());
        params.put("filter[awayTeamId][EQ]", fixture.getAttributes().getAwayTeamId());

        HttpListWrapper<FixturesList, Fixture> get = new HttpListWrapper<FixturesList, Fixture>();
        List<Fixture> fixtures = null;
        fixtures = get.getList(url, ServiceInvoker.APPLICATION_VND_API_JSON, params, FixturesList.class);
        if  (fixtures.size() == 1)
            return fixtures.get(0);
        else
            return fixture;
    }

    public Fixture saveFixture (Fixture fixture) throws ExternalServiceInvocationException {
        logger.debug("About to save " + fixture.getUniqueCompositeKey());
//        Gson gson = new Gson();

        if (fixture.getId() == null) {
            fixture = findExistingFixture(fixture);
        }

        SingleFixture singleFixture = new SingleFixture();
        singleFixture.setData(fixture);

        String url = dataApiTarget + "/fixtures";

        HttpSingleWrapper<SingleFixture, Fixture> save = new HttpSingleWrapper<SingleFixture, Fixture>();
        SingleFixture savedFixture = null;
        savedFixture = save.createOrUpdate(url, singleFixture, ServiceInvoker.APPLICATION_VND_API_JSON, SingleFixture.class);
        return savedFixture.getData();
    }

    //TODO change void to returned values
    public void saveFixtures (List<Fixture> fixtures) throws ExternalServiceInvocationException {
        logger.debug("About to save " + fixtures.size() + " fixtures");

        String url = dataApiTarget + "/bulksave/fixtures";

        FixturesList fixturesList = new FixturesList();
        fixturesList.setData(fixtures);

        HttpListWrapper<FixturesList, Fixture> put = new HttpListWrapper<FixturesList, Fixture>();
        put.saveList(url, fixturesList, ServiceInvoker.APPLICATION_VND_API_JSON, FixturesList.class);
    }
}
