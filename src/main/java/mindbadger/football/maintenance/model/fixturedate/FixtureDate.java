package mindbadger.football.maintenance.model.fixturedate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import mindbadger.football.maintenance.api.MappingCache;
import mindbadger.football.maintenance.model.base.JsonApiBase;
import mindbadger.football.maintenance.model.base.Links;
import mindbadger.football.maintenance.model.base.Relationship;
import mindbadger.football.maintenance.model.webreaderfixture.WebReaderFixture;
import mindbadger.football.maintenance.util.FixtureCompositeKeyGenerator;
import org.apache.log4j.Logger;

import java.util.Objects;

public class FixtureDate extends JsonApiBase {
    private Logger logger = Logger.getLogger(FixtureDate.class);

    private FixtureDate.Attributes attributes;
    private FixtureDate.Relationships relationships;

    private boolean modified;

    public FixtureDate() {
        this.attributes = new FixtureDate.Attributes();
        this.relationships = new FixtureDate.Relationships();
        this.links = new Links();
        this.type = "fixtureDates";
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    @JsonIgnore
    public Relationships getRelationships() {
        return relationships;
    }

    public void setRelationships(Relationships relationships) {
        this.relationships = relationships;
    }

    @Override
    public String toString() {
        return String.format("[FixtureDate fixtureDate = %s",
                attributes.getFixtureDate());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FixtureDate)) return false;

        FixtureDate objectToCompareTo = (FixtureDate) o;

        return (objectToCompareTo.getAttributes().getFixtureDate() == this.getAttributes().getFixtureDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.attributes.fixtureDate);
    }

    public class Attributes {
        String fixtureDate;

        public String getFixtureDate() {
            return fixtureDate;
        }
        public void setFixtureDate(String fixtureDate) {
            this.fixtureDate = fixtureDate;
        }
    }

    public class Relationships {
        private Relationship fixtures;
        private Relationship teamStatistics;

        public Relationship getFixtures() {
            return fixtures;
        }

        public void setFixtures(Relationship fixtures) {
            this.fixtures = fixtures;
        }

        public Relationship getTeamStatistics() {
            return teamStatistics;
        }

        public void setTeamStatistics(Relationship teamStatistics) {
            this.teamStatistics = teamStatistics;
        }
    }
}
