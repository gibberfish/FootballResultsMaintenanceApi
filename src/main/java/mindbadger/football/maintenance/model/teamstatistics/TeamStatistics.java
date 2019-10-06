package mindbadger.football.maintenance.model.teamstatistics;

import mindbadger.football.maintenance.model.base.JsonApiBase;
import mindbadger.football.maintenance.model.base.Links;
import mindbadger.football.maintenance.model.base.Relationship;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TeamStatistics extends JsonApiBase {
    private TeamStatistics.Attributes attributes;
    private TeamStatistics.Relationships relationships;

    public TeamStatistics() {
        this.attributes = new TeamStatistics.Attributes();
        this.relationships = new TeamStatistics.Relationships();
        this.links = new Links();
        this.type = "teamStatistics";
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

    @Override
    public String toString() {
        return String.format("[TeamStatisitcs id = %s]", id);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TeamStatistics)) return false;

        TeamStatistics objectToCompareTo = (TeamStatistics) o;

        return (objectToCompareTo.getId() == this.getId());
    }

    @Override
    public int hashCode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.getId());
        return Objects.hash(stringBuilder.toString());
    }

    public class Attributes {
        private List<Statistic> statistics;

        public List<Statistic> getStatistics() {
            return statistics;
        }

        public void setStatistics(List<Statistic> statistics) {
            this.statistics = statistics;
        }

    }

    public static class Statistic {
        private String statistic;
        private Integer value;

        public Statistic (String statistic, Integer value) {
            this.statistic = statistic;
            this.value = value;
        }

        public String getStatistic() {
            return statistic;
        }

        public void setStatistic(String statistic) {
            this.statistic = statistic;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }

    public class Relationships {
        private Relationship division;
        private Relationship season;
        private Relationship team;

        public Relationship getDivision() {
            return division;
        }

        public void setDivision(Relationship division) {
            this.division = division;
        }

        public Relationship getSeason() {
            return season;
        }

        public void setSeason(Relationship season) {
            this.season = season;
        }

        public Relationship getTeam() {
            return team;
        }

        public void setTeam(Relationship team) {
            this.team = team;
        }
    }
}
