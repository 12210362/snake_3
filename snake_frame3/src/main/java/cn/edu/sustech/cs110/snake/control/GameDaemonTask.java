package cn.edu.sustech.cs110.snake.control;

import cn.edu.sustech.cs110.snake.Context;
import cn.edu.sustech.cs110.snake.enums.GridState;
import cn.edu.sustech.cs110.snake.events.BeanAteEvent;
import cn.edu.sustech.cs110.snake.events.BoardRerenderEvent;
import cn.edu.sustech.cs110.snake.events.GameOverEvent;
import cn.edu.sustech.cs110.snake.model.Game;
import cn.edu.sustech.cs110.snake.model.Position;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameDaemonTask implements Runnable {

    @Override
    public void run() {
        Game game = Context.INSTANCE.currentGame();
        if (!game.isPlaying()) {
            return;
        }
        System.out.println(game);

        Position headFwd = game.getSnake().getBody().get(0).toward(game.getSnake().getDirection());
        //这里初始化3的原因，大概是因为有前进的点，删除的尾巴的点，随机生成的食物的点，所以最多有三个
        Map<Position, GridState> diffs = new HashMap<>(3);

        // TODO: manage the `diffs` map, you should add the correct changes into it
        final List<Position> snakeBodyList = game.getSnake().getBody();
        //游戏结果判断
        if(checkIsGameOver(headFwd, snakeBodyList, game.getRow(), game.getCol())){
            Context.INSTANCE.eventBus().post(new GameOverEvent());
            return;
        }
        //吃掉食物处理
        if(checkIsBeanAte(game.getBean(), headFwd)){
            Context.INSTANCE.eventBus().post(new BeanAteEvent());
            move(headFwd, true, game.getSnake().getBody(), diffs);
            createBean(game, diffs);
            Context.INSTANCE.eventBus().post(new BoardRerenderEvent(diffs));
            return;
        }
        //正常移动处理
        move(headFwd, false, game.getSnake().getBody(), diffs);
        Context.INSTANCE.eventBus().post(new BoardRerenderEvent(diffs));
    }

    /**
     * 创建食物
     * @param game
     * @param diffs
     */
    private void createBean(Game game, Map<Position, GridState> diffs){
        final Position bean = game.createAndGetBean();
        diffs.put(bean, GridState.BEAN_ON);
    }

    /**
     * 进行移动
     * @param headFwd 前进的位置
     * tailRemove 需要删除的尾巴
     * @param snakeBodyList 身体
     */
    private void move(Position headFwd, boolean isBeanAte, List<Position> snakeBodyList, Map<Position, GridState> diffs){
        diffs.put(headFwd, GridState.SNAKE_ON);
        if(isBeanAte){
            snakeBodyList.add(0, headFwd);
        }
        snakeBodyList.add(0, headFwd);
        final Position tailRemove = snakeBodyList.get(snakeBodyList.size() - 1);
        snakeBodyList.remove(tailRemove);
        if(!snakeBodyList.contains(tailRemove)){
            diffs.put(tailRemove, GridState.EMPTY);
        }
    }

    /**
     * 检查是否游戏结束
     * 1、触碰到墙壁
     * 2、触碰到自己的身体
     * @param headFwd 前进位置
     * @param snakeBodyList 身体部分
     */
    private boolean checkIsGameOver(Position headFwd, List<Position> snakeBodyList, int row, int col){
        return headFwd.getX() > row || headFwd.getX() < 0 || headFwd.getY() > col || headFwd.getY() < 0 || snakeBodyList.contains(headFwd);
    }

    /**
     * 检查是否吃到食物
     * @param bean 食物位置
     * @param headFwd 前进位置
     */
    private boolean checkIsBeanAte(Position bean, Position headFwd){
        return bean.equals(headFwd);
    }
}
