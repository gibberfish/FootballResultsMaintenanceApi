package mindbadger.football.maintenance.api.dataservice;

import mindbadger.football.maintenance.api.rest.HttpListWrapper;
import mindbadger.football.maintenance.api.rest.ServiceInvoker;
import mindbadger.football.maintenance.model.fixturedate.FixtureDate;
import mindbadger.football.maintenance.model.fixturedate.FixtureDatesList;
import mindbadger.football.maintenance.model.teamstatistics.TeamStatistics;
import mindbadger.football.maintenance.model.teamstatistics.TeamStatisticsList;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class FixtureDateDataService {
    Logger logger = Logger.getLogger(FixtureDateDataService.class);

    @Value("${data.api.target}")
    private String dataApiTarget;

    @Autowired
    private ServiceInvoker serviceInvoker;

    public List<FixtureDate> getFixtureDatesFromHyperlink (String url) {
        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();

        HttpListWrapper<FixtureDatesList, FixtureDate> get = new HttpListWrapper<FixtureDatesList, FixtureDate>();
        try {
            return get.getList(url, ServiceInvoker.APPLICATION_VND_API_JSON, params, FixtureDatesList.class);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public List<FixtureDate> getFixtureDates(String seasonDivisionId) {
        String url = dataApiTarget + "/seasonDivisions/" + seasonDivisionId + "/fixtureDates";

        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();
        params.put("page[limit]", "10000");

        HttpListWrapper<FixtureDatesList, FixtureDate> get = new HttpListWrapper<FixtureDatesList, FixtureDate>();
        try {
            return get.getList(url, ServiceInvoker.APPLICATION_VND_API_JSON, params, FixtureDatesList.class);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public List<FixtureDate> getFixtureDatesGreaterThanOrEqualToDate(String seasonDivisionId, String fixtureDate) {
        String url = dataApiTarget + "/seasonDivisions/" + seasonDivisionId + "/fixtureDates";

        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();
        params.put("page[limit]", "10000");
        params.put("filter[fixtureDate][GE]",fixtureDate);

        HttpListWrapper<FixtureDatesList, FixtureDate> get = new HttpListWrapper<FixtureDatesList, FixtureDate>();
        try {
            return get.getList(url, ServiceInvoker.APPLICATION_VND_API_JSON, params, FixtureDatesList.class);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
