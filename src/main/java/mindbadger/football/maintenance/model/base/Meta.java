package mindbadger.football.maintenance.model.base;

public class Meta {
    private int totalResourceCount;

    public Meta () {
    }

    public Meta (int totalResourceCount) {
        this.totalResourceCount = totalResourceCount;
    }

    public int getTotalResourceCount() {
        return totalResourceCount;
    }

    public void setTotalResourceCount(int totalResourceCount) {
        this.totalResourceCount = totalResourceCount;
    }
}
