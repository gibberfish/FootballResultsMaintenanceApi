package mindbadger.football.maintenance.api.dataservice;

import mindbadger.football.maintenance.api.rest.HttpListWrapper;
import mindbadger.football.maintenance.api.rest.ServiceInvoker;
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
public class TeamStatisticsDataService {
    Logger logger = Logger.getLogger(TeamStatisticsDataService.class);

    @Value("${data.api.target}")
    private String dataApiTarget;

    @Autowired
    private ServiceInvoker serviceInvoker;

    public List<TeamStatistics> getTeamStatisticsFromHyperlink (String url) {
        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();

        HttpListWrapper<TeamStatisticsList, TeamStatistics> get = new HttpListWrapper<TeamStatisticsList, TeamStatistics>();
        try {
            return get.getList(url, ServiceInvoker.APPLICATION_VND_API_JSON, params, TeamStatisticsList.class);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
