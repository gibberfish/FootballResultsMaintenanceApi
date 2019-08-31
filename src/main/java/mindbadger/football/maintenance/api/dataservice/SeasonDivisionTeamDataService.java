package mindbadger.football.maintenance.api.dataservice;

import mindbadger.football.maintenance.api.rest.ExternalServiceInvocationException;
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

import java.util.List;

@Component
public class SeasonDivisionTeamDataService {
    Logger logger = Logger.getLogger(SeasonDivisionTeamDataService.class);

    @Value("${data.api.target}")
    private String dataApiTarget;

    @Autowired
    private ServiceInvoker serviceInvoker;

    public List<SeasonDivision> getSeasonDivisions(int seasonNumber) throws ExternalServiceInvocationException {
        String url = dataApiTarget + "/seasons/" + seasonNumber + "/seasonDivisions";
        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();

        HttpListWrapper<SeasonDivisionsList, SeasonDivision> get = new HttpListWrapper<SeasonDivisionsList, SeasonDivision>();
        return get.getList(url, ServiceInvoker.APPLICATION_VND_API_JSON, params, SeasonDivisionsList.class);
    }

    public SeasonDivision getSeasonDivision(SeasonDivision seasonDivisionId) throws ExternalServiceInvocationException {
        String findUrl = dataApiTarget + "/seasonDivisions" + "/" +
                seasonDivisionId.getAttributes().getSeasonNumber() + "_" +
                seasonDivisionId.getAttributes().getDivisionId();

        HttpSingleWrapper<SingleSeasonDivision, SeasonDivision> get = new HttpSingleWrapper<>();
        SeasonDivision seasonDivision = get.getSingle(findUrl, ServiceInvoker.APPLICATION_VND_API_JSON, SingleSeasonDivision.class);
        return seasonDivision;
    }

    public void createSeasonDivision(SeasonDivision seasonDivisionId) throws ExternalServiceInvocationException {
        String createUrl = dataApiTarget + "/seasonDivisions";

        SingleSeasonDivision singleSeasonDivision = new SingleSeasonDivision();
        singleSeasonDivision.setData(seasonDivisionId);

        HttpSingleWrapper<SingleSeasonDivision, SeasonDivision> save = new HttpSingleWrapper<SingleSeasonDivision, SeasonDivision>();
        SingleSeasonDivision seasonDivision = save.createOrUpdate(createUrl, singleSeasonDivision, ServiceInvoker.APPLICATION_VND_API_JSON, SingleSeasonDivision.class);
    }

    public SeasonDivisionTeam getSeasonDivisionTeam(SeasonDivisionTeam seasonDivisionTeamId) throws ExternalServiceInvocationException {
        String findUrl = dataApiTarget + "/seasonDivisionTeams" + "/" +
                seasonDivisionTeamId.getAttributes().getSeasonNumber() + "_" +
                seasonDivisionTeamId.getAttributes().getDivisionId() + "_" +
                seasonDivisionTeamId.getAttributes().getTeamId();

        HttpSingleWrapper<SingleSeasonDivisionTeam, SeasonDivisionTeam> get = new HttpSingleWrapper<>();
        SeasonDivisionTeam seasonDivisionTeam = get.getSingle(findUrl, ServiceInvoker.APPLICATION_VND_API_JSON, SingleSeasonDivisionTeam.class);
        return seasonDivisionTeam;
    }

    public void createSeasonDivisionTeam(SeasonDivisionTeam seasonDivisionTeam) throws ExternalServiceInvocationException {
        String createUrl = dataApiTarget + "/seasonDivisionTeams";
        SingleSeasonDivisionTeam singleSeasonDivisionTeam = new SingleSeasonDivisionTeam();
        singleSeasonDivisionTeam.setData(seasonDivisionTeam);

        HttpSingleWrapper<SingleSeasonDivisionTeam, SeasonDivisionTeam> save = new HttpSingleWrapper<>();
        SingleSeasonDivisionTeam savedFixture = save.createOrUpdate(createUrl, singleSeasonDivisionTeam, ServiceInvoker.APPLICATION_VND_API_JSON, SingleSeasonDivisionTeam.class);
    }

    public void createSeason (String seasonNumber) throws ExternalServiceInvocationException {
        String createUrl = dataApiTarget + "/seasons";

        Season season = new Season();
        //season.setId(seasonNumber); - need to leave the id blank, as this will force a create
        season.getAttributes().setSeasonNumber(Integer.parseInt(seasonNumber));
        SingleSeason singleSeason = new SingleSeason();
        singleSeason.setData(season);

        HttpSingleWrapper<SingleSeason, Season> save = new HttpSingleWrapper<>();
        SingleSeason savedSeason = save.createOrUpdate(createUrl, singleSeason, ServiceInvoker.APPLICATION_VND_API_JSON, SingleSeason.class);
    }

    public Season getSeason (String seasonNumber) throws ExternalServiceInvocationException {
        String findUrl = dataApiTarget + "/seasons/" + seasonNumber;

        HttpSingleWrapper<SingleSeason, Season> get = new HttpSingleWrapper<>();
        Season season = get.getSingle(findUrl, ServiceInvoker.APPLICATION_VND_API_JSON, SingleSeason.class);
        logger.debug("Season " + seasonNumber + " already exists");
        return season;
    }

    public Team getTeamWithName (String teamName) throws ExternalServiceInvocationException {
        String findUrl = dataApiTarget + "/teams";

        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();
        params.put("page[limit]", "10000");
        params.put("filter[teamName][EQ]",teamName);

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
    }

    public Team createTeam (String teamName) throws ExternalServiceInvocationException {
        String url = dataApiTarget + "/teams";
        Team team = new Team();
        team.getAttributes().setTeamName(teamName);
        SingleTeam singleTeam = new SingleTeam();
        singleTeam.setData(team);

        HttpSingleWrapper<SingleTeam, Team> save = new HttpSingleWrapper<>();
        SingleTeam savedTeam = save.createOrUpdate(url, singleTeam, ServiceInvoker.APPLICATION_VND_API_JSON, SingleTeam.class);
        return savedTeam.getData();
    }
}
