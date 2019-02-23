package mindbadger.football.maintenance.model;

import java.util.List;

public class SeasonDivisionsList extends JsonApiResponseBase {
    public SeasonDivisionsList () { }
    public SeasonDivisionsList (List<SeasonDivision> data, Links links, Meta meta) {
        this.data = data;
        this.links = links;
        this.meta = meta;
    }

    List<SeasonDivision> data;

    public List<SeasonDivision> getData() {
        return data;
    }

    public void setData(List<SeasonDivision> data) {
        this.data = data;
    }
}
