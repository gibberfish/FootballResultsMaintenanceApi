package mindbadger.football.maintenance.api.dataservice;

import mindbadger.football.maintenance.api.rest.ExternalServiceInvocationException;
import mindbadger.football.maintenance.api.rest.HttpListWrapper;
import mindbadger.football.maintenance.api.rest.HttpSingleWrapper;
import mindbadger.football.maintenance.api.rest.ServiceInvoker;
import mindbadger.football.maintenance.model.mapping.Mapping;
import mindbadger.football.maintenance.model.mapping.MappingsList;
import mindbadger.football.maintenance.model.mapping.SingleMapping;
import mindbadger.football.maintenance.model.trackeddivision.TrackedDivision;
import mindbadger.football.maintenance.model.trackeddivision.TrackedDivisionsList;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MappingDataService {
    Logger logger = Logger.getLogger(MappingDataService.class);

    @Value("${data.api.target}")
    private String dataApiTarget;

    @Autowired
    private ServiceInvoker serviceInvoker;

    public List<TrackedDivision> getTrackedDivisions() throws ExternalServiceInvocationException  {
        String url = dataApiTarget + "/tracked_division";
        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();
        params.put("page[limit]", "10000");

        HttpListWrapper<TrackedDivisionsList, TrackedDivision> get = new HttpListWrapper<TrackedDivisionsList, TrackedDivision>();
        return get.getList(url, ServiceInvoker.APPLICATION_VND_API_JSON, params, TrackedDivisionsList.class);
    }

    public List<Mapping> getDivisionMappings () throws ExternalServiceInvocationException {
        String url = dataApiTarget + "/division_mapping";
        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();
        params.put("page[limit]", "10000");

        HttpListWrapper<MappingsList, Mapping> get = new HttpListWrapper<MappingsList, Mapping>();
        return get.getList(url, ServiceInvoker.APPLICATION_VND_API_JSON, params, MappingsList.class);
    }

    public List<Mapping> getTeamMappings () throws ExternalServiceInvocationException {
        String url = dataApiTarget + "/team_mapping";
        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();
        params.put("page[limit]", "10000");

        HttpListWrapper<MappingsList, Mapping> get = new HttpListWrapper<MappingsList, Mapping>();
        return get.getList(url, ServiceInvoker.APPLICATION_VND_API_JSON, params, MappingsList.class);
    }

    public Mapping createTeamMapping(Integer webReaderTeamId, String teamId) throws ExternalServiceInvocationException {
        String url = dataApiTarget + "/team_mapping";
        Mapping mapping = new Mapping();
        logger.debug("Creating team mapping between web id " + webReaderTeamId + " and " + teamId);
        mapping.getAttributes().setSourceId(webReaderTeamId);
        mapping.getAttributes().setFraId(teamId);
        SingleMapping singleMapping = new SingleMapping();
        singleMapping.setData(mapping);

        HttpSingleWrapper<SingleMapping, Mapping> save = new HttpSingleWrapper<>();
        SingleMapping savedMapping = save.createOrUpdate(url, singleMapping, ServiceInvoker.APPLICATION_VND_API_JSON, SingleMapping.class);
        return savedMapping.getData();
    }
}
