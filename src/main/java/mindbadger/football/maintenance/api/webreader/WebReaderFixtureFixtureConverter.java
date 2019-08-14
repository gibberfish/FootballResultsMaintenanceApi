package mindbadger.football.maintenance.api.webreader;

import mindbadger.football.maintenance.api.MappingCache;
import mindbadger.football.maintenance.model.Fixture.Fixture;
import mindbadger.football.maintenance.model.webreaderfixture.WebReaderFixture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WebReaderFixtureFixtureConverter {

    @Autowired
    private MappingCache mappingCache;

    public List<Fixture> convertWebReaderFixturesToFixtures (List<WebReaderFixture> webReaderFixtures) {
        List<Fixture> fixtures = new ArrayList<>();
        for (WebReaderFixture webReaderFixture : webReaderFixtures) {
            fixtures.add (convertWebFixtureToFixture(webReaderFixture));
        }
        return fixtures;
    }

    private Fixture convertWebFixtureToFixture (WebReaderFixture webReaderFixture) {
        String divisionId = mappingCache.getMappedDivisions().get(webReaderFixture.getDivisionId());
        String homeTeamId = mappingCache.getMappedTeams().get(webReaderFixture.getHomeTeamId());
        String awayTeamId = mappingCache.getMappedTeams().get(webReaderFixture.getAwayTeamId());

        Fixture fixture = new Fixture();

        fixture.getAttributes().setSeasonNumber(webReaderFixture.getSeasonId());
        fixture.getAttributes().setDivisionId(divisionId);
        fixture.getAttributes().setHomeTeamId(homeTeamId);
        fixture.getAttributes().setAwayTeamId(awayTeamId);
        fixture.getAttributes().setHomeGoals(webReaderFixture.getHomeGoals());
        fixture.getAttributes().setAwayGoals(webReaderFixture.getAwayGoals());
        fixture.getAttributes().setFixtureDate(webReaderFixture.getFixtureDate());

        //logger.debug("Converted to : " + fixture);

        return fixture;
    }
}
