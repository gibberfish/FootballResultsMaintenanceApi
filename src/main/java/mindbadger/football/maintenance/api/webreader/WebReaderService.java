package mindbadger.football.maintenance.api.webreader;

import com.google.gson.Gson;
import mindbadger.football.maintenance.api.MappingCache;
import mindbadger.football.maintenance.api.dataservice.SeasonDivisionTeamDataService;
import mindbadger.football.maintenance.api.rest.ServiceInvoker;
import mindbadger.football.maintenance.api.rest.SimpleResponse;
import mindbadger.football.maintenance.model.webreaderfixture.WebReaderFixture;
import mindbadger.football.maintenance.util.Pauser;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class WebReaderService {
    Logger logger = Logger.getLogger(WebReaderService.class);

    @Value("${webreader.api.target}")
    private String webReaderApiTarget;

    @Autowired
    private ServiceInvoker serviceInvoker;

    @Autowired
    private Pauser pauser;

    @Autowired
    private SeasonDivisionTeamDataService seasonDivisionTeamDataService;

    @Autowired
    private MappingCache mappingCache;

    public List<WebReaderFixture> getFixturesForDate(String fixtureDate) {
        return getFixturesForDate(fixtureDate,0);
    }

    public List<WebReaderFixture> getFixturesForTeam(String seasonNumber, String teamId) {
        return getFixturesForTeam(seasonNumber, teamId, 0);
    }

    private List<WebReaderFixture> getFixturesForTeam(String seasonNumber, String teamId, int retryCount) {
        String url = webReaderApiTarget + "/getFixturesForTeam";

        // Bake in a pause before we invoke so that any users of the reader service can't invoke a DoS attack
        pauser.pause(4, 11);

        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();
        params.put("ssnNum", seasonNumber);
        params.put("teamId", teamId);
        for (Integer trackedDivision : mappingCache.getTrackedDivisions()) {
            params.put("trackedDiv", trackedDivision.toString());
        }

        SimpleResponse response = null;
        try {
            response = serviceInvoker.get(url, MediaType.APPLICATION_JSON, params);

//            logger.debug("RESPONSE FROM get = " + response);

            Gson gson = new Gson();
            WebReaderFixture[] fixtures = gson.fromJson(response.getBody(), WebReaderFixture[].class);
            return Arrays.stream(fixtures).collect(Collectors.toList());
        } catch (IOException | URISyntaxException e) {
            if (retryCount < 5) {
                retryCount++;
                logger.debug("... retrying ... (" + e.getMessage() + ")");
                return getFixturesForTeam(seasonNumber, teamId, retryCount);
            }
            System.err.println("Failed to get fixtures from the web for season " + seasonNumber + " and team " + teamId);
            return null;
        }
    }

    private List<WebReaderFixture> getFixturesForDate(String fixtureDate, int retryCount) {
        String url = webReaderApiTarget + "/getFixturesForDate";

        // Bake in a pause before we invoke so that any users of the reader service can't invoke a DoS attack
        pauser.pause(4, 11);

        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();
        params.put("date", fixtureDate);
        for (Integer trackedDivision : mappingCache.getTrackedDivisions()) {
            params.put("trackedDiv", trackedDivision.toString());
        }

        SimpleResponse response = null;
        try {
            response = serviceInvoker.get(url, MediaType.APPLICATION_JSON, params);

            logger.debug("RESPONSE FROM get = " + response);

            Gson gson = new Gson();
            WebReaderFixture[] fixtures = gson.fromJson(response.getBody(), WebReaderFixture[].class);
            return Arrays.stream(fixtures).collect(Collectors.toList());
        } catch (IOException | URISyntaxException e) {
            if (retryCount < 5) {
                retryCount++;
                logger.debug("... retrying ...");
                return getFixturesForDate(fixtureDate, retryCount);
            }
            System.err.println("Failed to get fixtures from the web for " + fixtureDate);
            return null;
        }
    }
}
