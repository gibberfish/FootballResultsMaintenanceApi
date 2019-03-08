package mindbadger.football.maintenance.api.dataservice;

import com.google.gson.Gson;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class SeasonDivisionTeamDataService {
    Logger logger = Logger.getLogger(SeasonDivisionTeamDataService.class);

    @Value("${data.api.target}")
    private String dataApiTarget;

    @Autowired
    private ServiceInvoker serviceInvoker;

    public List<SeasonDivision> getSeasonDivisions(int seasonNumber) {
        String url = dataApiTarget + "/seasons/" + seasonNumber + "/seasonDivisions";
        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();

        HttpListWrapper<SeasonDivisionsList, SeasonDivision> get = new HttpListWrapper<SeasonDivisionsList, SeasonDivision>();
        try {
            return get.getList(url, ServiceInvoker.APPLICATION_VND_API_JSON, params, SeasonDivisionsList.class);
        } catch (ClientProtocolException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public void createSeasonDivisionIfNotExists(SeasonDivision seasonDivision) {
        String createUrl = dataApiTarget + "/seasonDivisions";
        String findUrl = createUrl + "/" + seasonDivision.getAttributes().getSeasonNumber() + "_" +
                seasonDivision.getAttributes().getDivisionId();

        HttpSingleWrapper<SingleSeasonDivision, SeasonDivision> get = new HttpSingleWrapper<>();
        try {
            get.getSingle(findUrl, ServiceInvoker.APPLICATION_VND_API_JSON, SingleSeasonDivision.class);
        } catch (ClientProtocolException e) {
            SingleSeasonDivision singleSeasonDivision = new SingleSeasonDivision();
            singleSeasonDivision.setData(seasonDivision);

            HttpSingleWrapper<SingleSeasonDivision, SeasonDivision> save = new HttpSingleWrapper<SingleSeasonDivision, SeasonDivision>();
            try {
                SingleSeasonDivision savedFixture = save.createOrUpdate(createUrl, singleSeasonDivision, ServiceInvoker.APPLICATION_VND_API_JSON, SingleSeasonDivision.class);
            } catch (ClientProtocolException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void createSeasonDivisionTeamIfNotExists(SeasonDivisionTeam seasonDivisionTeam) {
        String createUrl = dataApiTarget + "/seasonDivisionTeams";
        String findUrl = createUrl + "/" + seasonDivisionTeam.getAttributes().getSeasonNumber() + "_" +
                seasonDivisionTeam.getAttributes().getDivisionId() + "_" +
                seasonDivisionTeam.getAttributes().getTeamId();

        HttpSingleWrapper<SingleSeasonDivisionTeam, SeasonDivisionTeam> get = new HttpSingleWrapper<>();
        try {
            get.getSingle(findUrl, ServiceInvoker.APPLICATION_VND_API_JSON, SingleSeasonDivisionTeam.class);
        } catch (ClientProtocolException e) {
            SingleSeasonDivisionTeam singleSeasonDivisionTeam = new SingleSeasonDivisionTeam();
            singleSeasonDivisionTeam.setData(seasonDivisionTeam);

            HttpSingleWrapper<SingleSeasonDivisionTeam, SeasonDivisionTeam> save = new HttpSingleWrapper<>();
            try {
                SingleSeasonDivisionTeam savedFixture = save.createOrUpdate(createUrl, singleSeasonDivisionTeam, ServiceInvoker.APPLICATION_VND_API_JSON, SingleSeasonDivisionTeam.class);
            } catch (ClientProtocolException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void createSeasonIfNotExists(String seasonNumber) {
        String createUrl = dataApiTarget + "/seasons";
        String findUrl = createUrl + "/" + seasonNumber;

        HttpSingleWrapper<SingleSeason, Season> get = new HttpSingleWrapper<>();
        try {
            Season season = get.getSingle(findUrl, ServiceInvoker.APPLICATION_VND_API_JSON, SingleSeason.class);
            System.out.println("Season " + seasonNumber + " already exists");
        } catch (ClientProtocolException e) {
            System.out.println("Can't find season " + seasonNumber + ", so try to create it...");
            Season season = new Season();
            season.setId(seasonNumber);
            season.getAttributes().setSeasonNumber(Integer.parseInt(seasonNumber));
            SingleSeason singleSeason = new SingleSeason();
            singleSeason.setData(season);

            HttpSingleWrapper<SingleSeason, Season> save = new HttpSingleWrapper<>();
            try {
                SingleSeason savedSeason = save.createOrUpdate(createUrl, singleSeason, ServiceInvoker.APPLICATION_VND_API_JSON, SingleSeason.class);
            } catch (ClientProtocolException ex) {
                ex.printStackTrace();
            }
        }
    }

    public Team createTeam (String teamName) {
        String url = dataApiTarget + "/teams";
        Team team = new Team();
        team.getAttributes().setTeamName(teamName);
        SingleTeam singleTeam = new SingleTeam();
        singleTeam.setData(team);

        HttpSingleWrapper<SingleTeam, Team> save = new HttpSingleWrapper<>();
        try {
            SingleTeam savedTeam = save.createOrUpdate(url, singleTeam, ServiceInvoker.APPLICATION_VND_API_JSON, SingleTeam.class);
            return savedTeam.getData();
        } catch (ClientProtocolException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
