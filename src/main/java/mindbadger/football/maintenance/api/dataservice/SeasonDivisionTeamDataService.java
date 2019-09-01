package mindbadger.football.maintenance.api.dataservice;

import mindbadger.football.maintenance.api.rest.HttpListWrapper;
import mindbadger.football.maintenance.api.rest.HttpSingleWrapper;
import mindbadger.football.maintenance.api.rest.ServiceInvoker;
import mindbadger.football.maintenance.model.season.Season;
import mindbadger.football.maintenance.model.season.SingleSeason;
import mindbadger.football.maintenance.model.seasondivision.SeasonDivision;
import mindbadger.football.maintenance.model.seasondivision.SeasonDivisionsList;
import mindbadger.football.maintenance.model.seasondivision.SingleSeasonDivision;
import mindbadger.football.maintenance.model.seasondivisionteam.SeasonDivisionTeam;
import mindbadger.football.maintenance.model.seasondivisionteam.SingleSeasonDivisionTeam;
import mindbadger.football.maintenance.model.team.SingleTeam;
import mindbadger.football.maintenance.model.team.Team;
import mindbadger.football.maintenance.model.team.TeamsList;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

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
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public SeasonDivision getSeasonDivision(SeasonDivision seasonDivisionId) {
        String findUrl = dataApiTarget + "/seasonDivisions" + "/" +
                seasonDivisionId.getAttributes().getSeasonNumber() + "_" +
                seasonDivisionId.getAttributes().getDivisionId();

        HttpSingleWrapper<SingleSeasonDivision, SeasonDivision> get = new HttpSingleWrapper<>();
        try {
            SeasonDivision seasonDivision = get.getSingle(findUrl, ServiceInvoker.APPLICATION_VND_API_JSON, SingleSeasonDivision.class);
            return seasonDivision;
        } catch (IOException e) {
            logger.debug("Can't find Season Division " + seasonDivisionId);
            logger.debug(e);
            return null;
        }
    }

    public void createSeasonDivision(SeasonDivision seasonDivisionId) {
        String createUrl = dataApiTarget + "/seasonDivisions";

        SingleSeasonDivision singleSeasonDivision = new SingleSeasonDivision();
        singleSeasonDivision.setData(seasonDivisionId);

        HttpSingleWrapper<SingleSeasonDivision, SeasonDivision> save = new HttpSingleWrapper<SingleSeasonDivision, SeasonDivision>();
        try {
            SingleSeasonDivision seasonDivision = save.createOrUpdate(createUrl, singleSeasonDivision, ServiceInvoker.APPLICATION_VND_API_JSON, SingleSeasonDivision.class);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public SeasonDivisionTeam getSeasonDivisionTeam(SeasonDivisionTeam seasonDivisionTeamId) throws IOException {
        String findUrl = dataApiTarget + "/seasonDivisionTeams" + "/" +
                seasonDivisionTeamId.getAttributes().getSeasonNumber() + "_" +
                seasonDivisionTeamId.getAttributes().getDivisionId() + "_" +
                seasonDivisionTeamId.getAttributes().getTeamId();

        HttpSingleWrapper<SingleSeasonDivisionTeam, SeasonDivisionTeam> get = new HttpSingleWrapper<>();
        SeasonDivisionTeam seasonDivisionTeam = get.getSingle(findUrl, ServiceInvoker.APPLICATION_VND_API_JSON, SingleSeasonDivisionTeam.class);
        return seasonDivisionTeam;
    }

    public void createSeasonDivisionTeam(SeasonDivisionTeam seasonDivisionTeam) {
        String createUrl = dataApiTarget + "/seasonDivisionTeams";
        SingleSeasonDivisionTeam singleSeasonDivisionTeam = new SingleSeasonDivisionTeam();
        singleSeasonDivisionTeam.setData(seasonDivisionTeam);

        HttpSingleWrapper<SingleSeasonDivisionTeam, SeasonDivisionTeam> save = new HttpSingleWrapper<>();
        try {
            SingleSeasonDivisionTeam savedFixture = save.createOrUpdate(createUrl, singleSeasonDivisionTeam, ServiceInvoker.APPLICATION_VND_API_JSON, SingleSeasonDivisionTeam.class);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void createSeason (String seasonNumber) {
        String createUrl = dataApiTarget + "/seasons";

        Season season = new Season();
        //season.setId(seasonNumber); - need to leave the id blank, as this will force a create
        season.getAttributes().setSeasonNumber(Integer.parseInt(seasonNumber));
        SingleSeason singleSeason = new SingleSeason();
        singleSeason.setData(season);

        HttpSingleWrapper<SingleSeason, Season> save = new HttpSingleWrapper<>();
        try {
            SingleSeason savedSeason = save.createOrUpdate(createUrl, singleSeason, ServiceInvoker.APPLICATION_VND_API_JSON, SingleSeason.class);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Season getSeason (String seasonNumber) {
        String findUrl = dataApiTarget + "/seasons/" + seasonNumber;

        HttpSingleWrapper<SingleSeason, Season> get = new HttpSingleWrapper<>();
        try {
            Season season = get.getSingle(findUrl, ServiceInvoker.APPLICATION_VND_API_JSON, SingleSeason.class);
            logger.debug("Season " + seasonNumber + " already exists");
            return season;
        } catch (IOException e) {
            logger.debug("Can't find season " + seasonNumber);
            logger.debug(e.getMessage());
            return null;
        }
    }

    public Team getTeamWithName (String teamName) {
        String findUrl = dataApiTarget + "/teams";

        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();
        params.put("page[limit]", "10000");
        params.put("filter[teamName][EQ]",teamName);

        try {
            HttpListWrapper<TeamsList, Team> get = new HttpListWrapper<>();
            List<Team> teams = null;
            teams = get.getList(findUrl, ServiceInvoker.APPLICATION_VND_API_JSON, params, TeamsList.class);
            if (teams.size() >= 1) {
                logger.debug("Team with name " + teamName + " exists");
                return teams.get(0);
            } else {
                logger.debug("Team with name " + teamName + " not found");
                return null;
            }
        } catch (IOException e) {
            logger.debug("Can't get team with name " + teamName);
            logger.debug(e.getMessage());
            return null;
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
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
