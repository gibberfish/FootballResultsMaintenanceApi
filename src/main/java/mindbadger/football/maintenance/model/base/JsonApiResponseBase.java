package mindbadger.football.maintenance.model.base;

import mindbadger.football.maintenance.model.base.Links;
import mindbadger.football.maintenance.model.base.Meta;

public class JsonApiResponseBase {
    protected Links links;
    protected Meta meta;

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}
