package mindbadger.football.maintenance.model.base;

import mindbadger.football.maintenance.model.Fixture.Fixture;

public class JsonApiSingle<T> extends JsonApiResponseBase {
    T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
