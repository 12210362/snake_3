package cn.edu.sustech.cs110.snake.model;

public class LeaderboardUsers {
    private String id;

    public String getId() {
        return id;
    }

    private Integer score;

    public Integer getScore() {
        return score;
    }

    private Integer aliveTime;

    public Integer getAliveTime() {
        return aliveTime;
    }

    public LeaderboardUsers(String id, Integer score, Integer aliveTime){
        this.id=id;
        this.score=score;
        this.aliveTime=aliveTime;
    }



}
