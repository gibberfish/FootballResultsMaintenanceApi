package mindbadger.football.maintenance.api;

import com.google.gson.Gson;
import mindbadger.football.maintenance.model.Fixture.Fixture;
import mindbadger.football.maintenance.model.Fixture.FixturesList;
import mindbadger.football.maintenance.model.Fixture.SingleFixture;
import mindbadger.football.maintenance.model.base.JsonApiList;
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

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class DataService {
    Logger logger = Logger.getLogger(DataService.class);

    @Value("${data.api.target}")
    private String dataApiTarget;

    @Autowired
    private ServiceInvoker serviceInvoker;

    public List<SeasonDivision> getSeasonDivisions(int seasonNumber) {
        String url = dataApiTarget + "/seasons/" + seasonNumber + "/seasonDivisions";
        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();

        GenericHttpGet<SeasonDivisionsList, SeasonDivision> get = new GenericHttpGet<SeasonDivisionsList, SeasonDivision>();
        return get.getList(url, ServiceInvoker.APPLICATION_VND_API_JSON, params, SeasonDivisionsList.class);
    }

    public List<TrackedDivision> getTrackedDivisions () {
        String url = dataApiTarget + "/tracked_division";
        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();
        params.put("page[limit]", "10000");

        GenericHttpGet<TrackedDivisionsList, TrackedDivision> get = new GenericHttpGet<TrackedDivisionsList, TrackedDivision>();
        return get.getList(url, ServiceInvoker.APPLICATION_VND_API_JSON, params, TrackedDivisionsList.class);
    }

    public List<Mapping> getDivisionMappings () {
        String url = dataApiTarget + "/division_mapping";
        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();
        params.put("page[limit]", "10000");

        GenericHttpGet<MappingsList, Mapping> get = new GenericHttpGet<MappingsList, Mapping>();
        return get.getList(url, ServiceInvoker.APPLICATION_VND_API_JSON, params, MappingsList.class);
    }

    public List<Mapping> getTeamMappings () {
        String url = dataApiTarget + "/team_mapping";
        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();
        params.put("page[limit]", "10000");

        GenericHttpGet<MappingsList, Mapping> get = new GenericHttpGet<MappingsList, Mapping>();
        return get.getList(url, ServiceInvoker.APPLICATION_VND_API_JSON, params, MappingsList.class);
    }

    public List<Fixture> getUnplayedFixturesForDivisionBeforeToday (String seasonDivisionId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.now();
        String dateString = date.format(formatter);

        String url = dataApiTarget + "/seasonDivisions/" + seasonDivisionId + "/fixtures";

        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();
        params.put("page[limit]", "10000");
        params.put("filter[fixtures][homeGoals][EQ]","null");
        params.put("filter[fixtureDate][LE]", dateString);

        GenericHttpGet<FixturesList, Fixture> get = new GenericHttpGet<FixturesList, Fixture>();
        return get.getList(url, ServiceInvoker.APPLICATION_VND_API_JSON, params, FixturesList.class);
    }

    public List<Fixture> getAllFixturesForSeason (String seasonNumber) {
        String url = dataApiTarget + "/fixtures";

        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();
        params.put("page[limit]", "10000");
        params.put("filter[seasonNumber][EQ]",seasonNumber);

        GenericHttpGet<FixturesList, Fixture> get = new GenericHttpGet<FixturesList, Fixture>();
        return get.getList(url, ServiceInvoker.APPLICATION_VND_API_JSON, params, FixturesList.class);
    }

    private Fixture findExistingFixture (Fixture fixture) {
        String url = dataApiTarget + "/fixtures";

        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();
        params.put("page[limit]", "10000");
        params.put("filter[seasonNumber][EQ]",Integer.toString(
                fixture.getAttributes().getSeasonNumber()));
        params.put("filter[divisionId][EQ]", fixture.getAttributes().getDivisionId());
        params.put("filter[homeTeamId][EQ]", fixture.getAttributes().getHomeTeamId());
        params.put("filter[awayTeamId][EQ]", fixture.getAttributes().getAwayTeamId());

        String response = null;
        try {
            response = serviceInvoker.get(url, ServiceInvoker.APPLICATION_VND_API_JSON, params);
            Gson gson = new Gson();
            FixturesList fixturesList = gson.fromJson(response, FixturesList.class);
            List<Fixture> data = fixturesList.getData();
            if (data.size() == 1)
               return data.get(0);
            else
                return fixture;
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return fixture;
        }
    }

    public Fixture saveFixture (Fixture fixture) {
        System.out.println("About to save " + fixture);
        Gson gson = new Gson();

        if (fixture.getId() == null) {
            fixture = findExistingFixture(fixture);
        }

        SingleFixture singleFixture = new SingleFixture();
        singleFixture.setData(fixture);

        String payload = gson.toJson(singleFixture);
        System.out.println("... payload: " + payload);

        String url = dataApiTarget + "/fixtures";

        String response = null;
        try {
            if (fixture.getId() == null) {
                response = serviceInvoker.post(url, ServiceInvoker.APPLICATION_VND_API_JSON, payload);
            } else {
                url = url + "/" + fixture.getId();
                response = serviceInvoker.patch(url, ServiceInvoker.APPLICATION_VND_API_JSON, payload);
            }

            System.out.println(".... Response from Save  = " + response);

            singleFixture = gson.fromJson(response, SingleFixture.class);
            return singleFixture.getData();
        } catch (IOException e) {
//            e.printStackTrace();
            System.out.println(".... Unable to Save  = " + e.getMessage());
            return null;
        }
    }

    public void createSeasonDivisionIfNotExists(SeasonDivision seasonDivision) {
        String createUrl = dataApiTarget + "/seasonDivisions";
        String findUrl = createUrl + "/" + seasonDivision.getAttributes().getSeasonNumber() + "_" +
                seasonDivision.getAttributes().getDivisionId();

        try {
            String response = serviceInvoker.get(findUrl, ServiceInvoker.APPLICATION_VND_API_JSON);
        } catch (ClientProtocolException e) {
            SingleSeasonDivision singleSeasonDivision = new SingleSeasonDivision();
            singleSeasonDivision.setData(seasonDivision);

            Gson gson = new Gson();
            String payload = gson.toJson(singleSeasonDivision);

            try {
                String response = serviceInvoker.post(createUrl, ServiceInvoker.APPLICATION_VND_API_JSON, payload);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void createSeasonDivisionTeamIfNotExists(SeasonDivisionTeam seasonDivisionTeam) {
        String createUrl = dataApiTarget + "/seasonDivisionTeams";
        String findUrl = createUrl + "/" + seasonDivisionTeam.getAttributes().getSeasonNumber() + "_" +
                seasonDivisionTeam.getAttributes().getDivisionId() + "_" +
                seasonDivisionTeam.getAttributes().getTeamId();

        try {
            String response = serviceInvoker.get(findUrl, ServiceInvoker.APPLICATION_VND_API_JSON);
        } catch (ClientProtocolException e) {
            SingleSeasonDivisionTeam singleSeasonDivisionTeam = new SingleSeasonDivisionTeam();
            singleSeasonDivisionTeam.setData(seasonDivisionTeam);

            Gson gson = new Gson();
            String payload = gson.toJson(singleSeasonDivisionTeam);

            try {
                String response = serviceInvoker.post(createUrl, ServiceInvoker.APPLICATION_VND_API_JSON, payload);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void createFixtureIfNotExists(String seasonNumber) {
        String url = dataApiTarget + "/seasons/" + seasonNumber;
        try {
            String response = serviceInvoker.get(url, ServiceInvoker.APPLICATION_VND_API_JSON);
            System.out.println("Team " + seasonNumber + " already exists");
        } catch (ClientProtocolException e) {
            System.out.println("Can't find season " + seasonNumber + ", so try to create it...");
            Season season = new Season();
            season.setId(seasonNumber);
            season.getAttributes().setSeasonNumber(Integer.parseInt(seasonNumber));
            SingleSeason singleSeason = new SingleSeason();
            singleSeason.setData(season);

            Gson gson = new Gson();
            String payload = gson.toJson(singleSeason);
            System.out.println("... payload: " + payload);

            try {
                url = dataApiTarget + "/seasons";
                String response = serviceInvoker.post(url, ServiceInvoker.APPLICATION_VND_API_JSON, payload);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Team createTeam (String teamName) {
        String url = dataApiTarget + "/teams";
        Team team = new Team();
        team.getAttributes().setTeamName(teamName);
        SingleTeam singleTeam = new SingleTeam();
        singleTeam.setData(team);

        Gson gson = new Gson();
        String payload = gson.toJson(singleTeam);

        try {
            String response = serviceInvoker.post(url, ServiceInvoker.APPLICATION_VND_API_JSON, payload);
            return gson.fromJson(response, Team.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Mapping createTeamMapping(Integer webReaderTeamId, String teamId) {
        String url = dataApiTarget + "/team_mapping";
        Mapping mapping = new Mapping();
        mapping.getAttributes().setSourceId(webReaderTeamId);
        mapping.getAttributes().setFraId(teamId);
        SingleMapping singleMapping = new SingleMapping();
        singleMapping.setData(mapping);

        Gson gson = new Gson();
        String payload = gson.toJson(singleMapping);

        try {
            String response = serviceInvoker.post(url, ServiceInvoker.APPLICATION_VND_API_JSON, payload);
            return gson.fromJson(response, Mapping.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
