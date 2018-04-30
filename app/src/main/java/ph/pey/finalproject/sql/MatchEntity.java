package ph.pey.finalproject.sql;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


@Entity
public class MatchEntity {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    private Double latitude, longitude;

    private int durationInMinutes;

    private String score;

    private String winner;

    private String loser;

    public MatchEntity(int uid, Double latitude, Double longitude, int durationInMinutes, String score, String winner, String loser) {
        this.uid = uid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.durationInMinutes = durationInMinutes;
        this.score = score;
        this.winner = winner;
        this.loser = loser;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(int durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getLoser() {
        return loser;
    }

    public void setLoser(String loser) {
        this.loser = loser;
    }

}
