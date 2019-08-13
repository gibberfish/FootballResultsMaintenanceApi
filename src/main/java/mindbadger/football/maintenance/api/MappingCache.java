package mindbadger.football.maintenance.api;

import mindbadger.football.maintenance.api.dataservice.MappingDataService;
import mindbadger.football.maintenance.model.mapping.Mapping;
import mindbadger.football.maintenance.model.trackeddivision.TrackedDivision;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class MappingCache {
    Logger logger = Logger.getLogger(MappingCache.class);

    @Autowired
    private MappingDataService mappingDataService;

    private List<Integer> trackedDivisions = new ArrayList<>();
    private Map<Integer, String> mappedDivisions = new HashMap<>();
    private Map<Integer, String> mappedTeams = new HashMap<>();

    public void refreshCache() {
        List<TrackedDivision> trackedDivisions = mappingDataService.getTrackedDivisions();
        for (TrackedDivision trackedDivision : trackedDivisions) {
            this.trackedDivisions.add(trackedDivision.getAttributes().getSourceId());
        }

        List<Mapping> divisionMappings = mappingDataService.getDivisionMappings();
        for (Mapping mapping : divisionMappings) {
            this.mappedDivisions.put(mapping.getAttributes().getSourceId(), mapping.getAttributes().getFraId());
        }

        List<Mapping> teamMappings = mappingDataService.getTeamMappings();
        for (Mapping mapping : teamMappings) {
            this.mappedTeams.put(mapping.getAttributes().getSourceId(), mapping.getAttributes().getFraId());
        }

        mappedDivisions.forEach((k,v) -> {
            logger.debug(String.format("*********** MAPPED DIV: %s = %s", k, v));
        });

        mappedTeams.forEach((k,v) -> {
            logger.debug(String.format("*********** MAPPED TEAM: %s = %s", k, v));
        });


    }

    public List<Integer> getTrackedDivisions() {
        return trackedDivisions;
    }

    public Map<Integer, String> getMappedDivisions() {
        return mappedDivisions;
    }

    public Map<Integer, String> getMappedTeams() {
        return mappedTeams;
    }
}
