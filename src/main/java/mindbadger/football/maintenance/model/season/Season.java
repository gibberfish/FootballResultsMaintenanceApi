package mindbadger.football.maintenance.model.season;

import mindbadger.football.maintenance.model.base.JsonApiBase;
import mindbadger.football.maintenance.model.base.Links;
import mindbadger.football.maintenance.model.base.Relationship;

public class Season extends JsonApiBase {
    private Season.Attributes attributes;
    private Season.Relationships relationships;

    public Season () {
        this.attributes = new Season.Attributes();
        this.relationships = new Season.Relationships();
        this.links = new Links();
        this.type = "seasons";
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

    public class Attributes {
        private Integer seasonNumber;

        public Integer getSeasonNumber() {
            return seasonNumber;
        }

        public void setSeasonNumber(Integer seasonNumber) {
            this.seasonNumber = seasonNumber;
        }
    }

    public class Relationships {
        private Relationship seasonDivisions;

        public Relationship getSeasonDivisions() {
            return seasonDivisions;
        }

        public void setSeasonDivisions(Relationship seasonDivisions) {
            this.seasonDivisions = seasonDivisions;
        }
    }
}
