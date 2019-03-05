package mindbadger.football.maintenance.model.Fixture;

import mindbadger.football.maintenance.api.MappingCache;
import mindbadger.football.maintenance.model.base.JsonApiBase;
import mindbadger.football.maintenance.model.base.Links;
import mindbadger.football.maintenance.model.base.Relationship;
import mindbadger.football.maintenance.model.webreaderfixture.WebReaderFixture;

import java.util.Objects;

public class Fixture extends JsonApiBase {
    private Fixture.Attributes attributes;
    private Fixture.Relationships relationships;

    private boolean modified;

    public Fixture () {
        this.attributes = new Fixture.Attributes();
        this.relationships = new Fixture.Relationships();
        this.links = new Links();
        this.type = "fixtures";
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public Relationships getRelationships() {
        return relationships;
    }

    public void setRelationships(Relationships relationships) {
        this.relationships = relationships;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public String getUniqueCompositeKey () {
        return String.format("%d-%s-%s-%s",
                attributes.getSeasonNumber(),
                attributes.divisionId,
                attributes.getHomeTeamId(),
                attributes.getAwayTeamId());
    }

    public void updateFixtureFromWebFixture (WebReaderFixture webReaderFixture, MappingCache mappingCache) {
        String divisionId = mappingCache.getMappedDivisions().get(webReaderFixture.getDivisionId());
        String homeTeamId = mappingCache.getMappedTeams().get(webReaderFixture.getHomeTeamId());
        String awayTeamId = mappingCache.getMappedTeams().get(webReaderFixture.getAwayTeamId());

        if (divisionId == null || homeTeamId == null | awayTeamId == null) {
            throw new IllegalStateException("Cannot update Fixture from Web Fixture until division and teams are mapped");
        }

        if ((attributes.getSeasonNumber() != 0 && webReaderFixture.getSeasonId()!=attributes.getSeasonNumber()) ||
                (attributes.getDivisionId() != null && !divisionId.equals(attributes.getDivisionId())) ||
                (attributes.getHomeTeamId() != null && !homeTeamId.equals(attributes.getHomeTeamId())) ||
                (attributes.getAwayTeamId() != null && !awayTeamId.equals(attributes.getAwayTeamId()))
        ) {
            throw new IllegalArgumentException("Cannot update this fixture, as it doesn't match the WebReaderFixture");
        }

        if (attributes.homeTeamId == null) {
            modified = true;
        }

        attributes.setSeasonNumber(webReaderFixture.getSeasonId());
        attributes.setDivisionId(divisionId);
        attributes.setHomeTeamId(homeTeamId);
        attributes.setAwayTeamId(awayTeamId);

        if (!Objects.equals(webReaderFixture.getFixtureDate(), attributes.getFixtureDate())) {
            modified = true;
            attributes.setFixtureDate(webReaderFixture.getFixtureDate());
        }

        if (!Objects.equals(webReaderFixture.getHomeGoals(), attributes.getHomeGoals())) {
            modified = true;
            attributes.setHomeGoals(webReaderFixture.getHomeGoals());
        }

        if (!Objects.equals(webReaderFixture.getAwayGoals(), attributes.getAwayGoals())) {
            modified = true;
            attributes.setAwayGoals(webReaderFixture.getAwayGoals());
        }
    }

    @Override
    public String toString() {
        return String.format("[Fixture id = %s, seasonNumber = %d, fixtureDate = %s, divId = %s, homeTeamId = %s, awayTeamId = %s, score = %d - %d, modified = %b]",
                id ,attributes.getSeasonNumber(), attributes.getFixtureDate(), attributes.getDivisionId(),
                attributes.getHomeTeamId(), attributes.getAwayTeamId(), attributes.getHomeGoals(), attributes.getAwayGoals(), modified);
    }

    public class Attributes {
        int seasonNumber;
        String fixtureDate;
        String divisionId;
        String homeTeamId;
        String awayTeamId;
        Integer homeGoals;
        Integer awayGoals;

        public int getSeasonNumber() {
            return seasonNumber;
        }

        public void setSeasonNumber(int seasonNumber) {
            this.seasonNumber = seasonNumber;
        }

        public String getFixtureDate() {
            return fixtureDate;
        }

        public void setFixtureDate(String fixtureDate) {
            this.fixtureDate = fixtureDate;
        }

        public String getDivisionId() {
            return divisionId;
        }

        public void setDivisionId(String divisionId) {
            this.divisionId = divisionId;
        }

        public String getHomeTeamId() {
            return homeTeamId;
        }

        public void setHomeTeamId(String homeTeamId) {
            this.homeTeamId = homeTeamId;
        }

        public String getAwayTeamId() {
            return awayTeamId;
        }

        public void setAwayTeamId(String awayTeamId) {
            this.awayTeamId = awayTeamId;
        }

        public Integer getHomeGoals() {
            return homeGoals;
        }

        public void setHomeGoals(Integer homeGoals) {
            this.homeGoals = homeGoals;
        }

        public Integer getAwayGoals() {
            return awayGoals;
        }

        public void setAwayGoals(Integer awayGoals) {
            this.awayGoals = awayGoals;
        }
    }

    public class Relationships {
        private Relationship seasonDivision;
        private Relationship homeTeam;
        private Relationship awayTeam;

        public Relationship getSeasonDivision() {
            return seasonDivision;
        }

        public void setSeasonDivision(Relationship seasonDivision) {
            this.seasonDivision = seasonDivision;
        }

        public Relationship getHomeTeam() {
            return homeTeam;
        }

        public void setHomeTeam(Relationship homeTeam) {
            this.homeTeam = homeTeam;
        }

        public Relationship getAwayTeam() {
            return awayTeam;
        }

        public void setAwayTeam(Relationship awayTeam) {
            this.awayTeam = awayTeam;
        }
    }
}
