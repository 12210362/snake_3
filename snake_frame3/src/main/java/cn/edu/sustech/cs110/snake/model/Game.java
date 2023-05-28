package cn.edu.sustech.cs110.snake.model;

import cn.edu.sustech.cs110.snake.Context;
import cn.edu.sustech.cs110.snake.enums.Direction;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private final int row;
    private final int col;
    private final Snake snake;
    private Position bean;
    private int duration;
    private boolean playing;
    /** 开始时间 */
    private Long startTime;
    public Game(int row, int col,Snake snake,Position bean){
        this.row=row;
        this.col=col;
        this.snake=snake;
        this.bean=bean;

    }
    public Game(int row, int col) {
        this.row = row;
        this.col = col;
        this.snake = new Snake(Direction.random());
        this.snake.getBody().add(new Position(row / 2, col / 2));
        createAndGetBean();
    }
    /**
     * 在白板部分随机选择一个位置，生成食物
     * @return
     */
    public Position createAndGetBean(){
        List<Position> blackList = new ArrayList<>();
        for(int rowIndex = 0; rowIndex < row; rowIndex++){
            for(int colIndex = 0; colIndex < col; colIndex++){
                final Position position = new Position(rowIndex, colIndex);
                if(!snake.getBody().contains(position)){
                    blackList.add(position);
                }
            }
        }
        setBean(blackList.get(Context.INSTANCE.random().nextInt(blackList.size() - 1)));
        return getBean();
    }

    public void setBean(final Position bean) {
        this.bean = bean;
    }

    public void setDuration(final int duration) {
        this.duration = duration;
    }

    public void setPlaying(final boolean playing) {
        this.playing = playing;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public Snake getSnake() {
        return this.snake;
    }

    public Position getBean() {
        return this.bean;
    }

    public int getDuration() {
        return this.duration;
    }

    public boolean isPlaying() {
        return this.playing;
    }

    @Override
    public java.lang.String toString() {
        return "Game(row=" + this.getRow() + ", col=" + this.getCol() + ", snake=" + this.getSnake() + ", bean=" + this.getBean() + ", duration=" + this.getDuration() + ", playing=" + this.isPlaying() + ")";
    }
}
