package cn.edu.sustech.cs110.snake.control;
import  java.io.File;
import cn.edu.sustech.cs110.snake.Context;
import cn.edu.sustech.cs110.snake.enums.Direction;
import cn.edu.sustech.cs110.snake.events.*;
import cn.edu.sustech.cs110.snake.model.Game;
import cn.edu.sustech.cs110.snake.model.LeaderboardUsers;
import cn.edu.sustech.cs110.snake.model.Snake;
import cn.edu.sustech.cs110.snake.model.Position;
import cn.edu.sustech.cs110.snake.view.components.GameBoard;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
//import javafx.beans.value.ChangeListener;
//import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
//import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.SneakyThrows;
import cn.edu.sustech.cs110.snake.view.AdvancedStage;
import java.io.FileNotFoundException;
import java.io.IOException;
import  java.util.Scanner;
import java.io.PrintWriter;

import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

//import javafx.beans.value.ChangeListener;
//import  javafx.beans.value.ObservableValue;

public class GameController implements Initializable {
    @FXML
    private RadioMenuItem diffEasy;
    @FXML
    private RadioMenuItem diffMedium;
    @FXML
    private RadioMenuItem diffHard;
    private MediaPlayer gameMediaPlayer;

    private MediaPlayer gameOverMediaPlayer;

    private  Media gameOverMedia;

    @FXML
    private Parent root;

    @FXML
    private MenuItem menuPause;

    @FXML
    private Button btnPause;

    @FXML
    private Text textCurrentScore;

    @FXML
    private Text textPlayerHighest;

    @FXML
    private Text textTimeAlive;
    @FXML
    private static int time=0;
    @FXML
    private GameBoard board;

    @FXML
    private CheckMenuItem menuMusic;
    @FXML
    private Label player;

    @FXML
    public File IDS=new File("用户.txt");
    private String playerID ;
    @FXML
    private Label first;
    @FXML
    private Label second;
    @FXML
    private Label third;

    private static long MOVE_DURATION=500;

    @SuppressWarnings("AlibabaThreadPoolCreation")
    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    @SuppressWarnings("java:S3077")
    volatile ScheduledFuture<?> gameDaemonTask;

    public GameController() {
    }

    //public GameController(Text textPlayerName) {
    //    this.textPlayerName = textPlayerName;
    //}

    //计时器
    //@SneakyThrows

    @SneakyThrows
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        player.setText(getPlayerID());
        File file1=new File("Leaderboard.txt");
        Scanner input1=new Scanner(file1);
        if(input1.hasNextLine()){
            String[] no1Info=input1.nextLine().split(" ");
            first.setText("No.1:  "+no1Info[0]+"   "+no1Info[1]+"   "+no1Info[2]+"s");
        }
        if(input1.hasNextLine()){
            String[] no2Info=input1.nextLine().split(" ");
            second.setText("No.2:  "+no2Info[0]+"   "+no2Info[1]+"   "+no2Info[2]+"s");
        }
        if(input1.hasNextLine()){
            String[] no3Info=input1.nextLine().split(" ");
            third.setText("No.3:  "+no3Info[0]+"   "+no3Info[1]+"   "+no3Info[2]+"s");
        }
        input1.close();
        scheduler.scheduleAtFixedRate(() -> {
            final Game game = Context.INSTANCE.currentGame();
            if (!game.isPlaying()) {
                return;
            }
            time=time+1;
            this.textTimeAlive.setText("Time alive: "+String.valueOf(time)+"s");
        }, 0, 1000, TimeUnit.MILLISECONDS);

        setupDaemonScheduler();

        initMusic();

        Platform.runLater(this::bindAccelerators);
        board.paint(Context.INSTANCE.currentGame());
        playMusic();
    }

    private void initMusic() throws MediaException{
        String url1 = "music2.wav";
        Media media = new Media(new File(url1).toURI().toString());
        gameMediaPlayer = new MediaPlayer(media);

        //String gameOverMediaUrl = "game over.wav";
        //Media gameOverMedia = new Media(new File(gameOverMediaUrl).toURI().toString());
        // gameOverMediaPlayer = new MediaPlayer(gameOverMedia);

    }
    public String getPlayerID()throws FileNotFoundException{
        Scanner in = new Scanner(IDS);
        playerID=in.next();
        String a =("Player: "+playerID);
        return a;
    }
    private void bindAccelerators() {
        Scene scene = root.getScene();

        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.P), this::togglePause);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.W), this::turnUp);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.S), this::turnDown);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.A), this::turnLeft);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.D), this::turnRight);
    }
    public void  playMusic(){
        if(!Context.INSTANCE.currentGame().isPlaying()){
            System.out.println("music");
            //gameMediaPlayer.play();
            gameMediaPlayer.setVolume(0.5);
            gameMediaPlayer.setAutoPlay(true);
            gameMediaPlayer.setStartTime(Duration.seconds(0));
            gameMediaPlayer.setStopTime(Duration.seconds(96));
            gameMediaPlayer.setOnEndOfMedia(new Runnable() {
                public void run() {
                    gameMediaPlayer.seek(Duration.ZERO);
                    gameMediaPlayer.play();
                }
            });


        }
    }
    public void togglePause() {
        // TODO: change the text in menu's pause item and button
        if(Context.INSTANCE.currentGame().isPlaying()) {
            this.btnPause.setText("Play");
        }else{
            this.btnPause.setText("Pause");
        }



        Context.INSTANCE.currentGame().setPlaying(!Context.INSTANCE.currentGame().isPlaying());
    }

    public void doRestart() {
        textTimeAlive.setText("Time alive: 0s");
        time=0;
        final Game game = Context.INSTANCE.currentGame();
        Context.INSTANCE.currentGame(new Game(game.getRow(), game.getCol()));
        textCurrentScore.setText("Current score: 0");
        board.paint(Context.INSTANCE.currentGame());
        this.btnPause.setText("Play");
        gameMediaPlayer.play();
    }

    public void doRecover() throws FileNotFoundException {
        // TODO: add some code here
        File file=new File(playerID+".txt");
        Scanner input=new Scanner(file);
        String[] positionInformation=input.nextLine().split(" ");
        List<Position> newSnakePosition=new ArrayList<>();
        for(int i=0;i<positionInformation.length;i++){
            String[] positions=positionInformation[i].split(",");
            newSnakePosition.add(new Position(Integer.parseInt(positions[0]),Integer.parseInt(positions[1])));
        }
        Snake newSnake=new Snake(newSnakePosition);

        String newSnakeDirection=input.nextLine();

        switch(newSnakeDirection) {
            case"UP":
                newSnake.setDirection(Direction.UP);
                break;
            case"DOWN":
                newSnake.setDirection(Direction.DOWN);
                break;
            case"LEFT":
                newSnake.setDirection(Direction.LEFT);
                break;
            case"RIGHT":
                newSnake.setDirection(Direction.RIGHT);
                break;
        }

        String[]newBeanPositionInformation=input.nextLine().split(",");

        Position newBeanPosition=new Position(Integer.parseInt(newBeanPositionInformation[0]),Integer.parseInt(newBeanPositionInformation[1]));
        final Game game=Context.INSTANCE.currentGame();
        Context.INSTANCE.currentGame(new Game(game.getRow(), game.getCol(),newSnake,newBeanPosition));
        textCurrentScore.setText(input.nextLine());
        textPlayerHighest.setText(input.nextLine());
        textTimeAlive.setText(input.nextLine());
        input.close();
        File file1=new File("Leaderboard.txt");
        Scanner input1=new Scanner(file1);
        if(input1.hasNextLine()){
            String[] no1Info=input1.nextLine().split(" ");
            first.setText("No.1:  "+no1Info[0]+"   "+no1Info[1]+"   "+no1Info[2]+"s");
        }
        if(input1.hasNextLine()){
            String[] no2Info=input1.nextLine().split(" ");
            second.setText("No.2:  "+no2Info[0]+"   "+no2Info[1]+"   "+no2Info[2]+"s");
        }
        if(input1.hasNextLine()){
            String[] no3Info=input1.nextLine().split(" ");
            third.setText("No.3:  "+no3Info[0]+"   "+no3Info[1]+"   "+no3Info[2]+"s");
        }
        input1.close();
        board.paint(Context.INSTANCE.currentGame());
        this.btnPause.setText("Play");

        // TODO: add some code here
    }

    public void doSave() throws Exception{
        int SnakeLong=Context.INSTANCE.currentGame().getSnake().getBody().size();
        PrintWriter output=new PrintWriter(playerID+".txt");
        for(int i=0;i<SnakeLong;i++){
            output.print(Context.INSTANCE.currentGame().getSnake().getBody().get(i).getX());
            output.print(",");
            output.print(Context.INSTANCE.currentGame().getSnake().getBody().get(i).getY());
            output.print(" ");
        }
        output.println();
        output.print(Context.INSTANCE.currentGame().getSnake().getDirection());
        output.println();
        output.print(Context.INSTANCE.currentGame().getBean().getX());
        output.print(",");
        output.println(Context.INSTANCE.currentGame().getBean().getY());
        output.println(textCurrentScore.getText());
        output.println(textPlayerHighest.getText());
        output.println(textTimeAlive.getText());
        output.close();
        // TODO: add some code here
    }

    public void doQuit() {
        //退出JVM
        System.exit(0);
    }

    public void toggleMusic() throws MediaException {
        if(gameMediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)){
            gameMediaPlayer.pause();
        }
        if(gameMediaPlayer.getStatus().equals(MediaPlayer.Status.PAUSED)){
            gameMediaPlayer.play();
        }
        /*gameMediaPlayer.setOnEndOfMedia(new Runnable() {
            public void run() {
                gameMediaPlayer.pause();
            }
        });
        gameMediaPlayer.setOnReady(new Runnable() {
                public void run() {
                    gameMediaPlayer.play();
                }
        });*/

    }
    //gameMediaPlayer.pause();
    //gameMediaPlayer.play();
    //URL url1=this.getClass().getClassLoader().getResource("view/music.mp3");
    //System.out.println(url1.toExternalForm());
    //String url1=Text.class.getResource("src/main/resources/cn/edu/sustech/cs110/snake/view/music2.wav").toString();
    //String url1=Text.class.getResource("src/main/resources/cn/edu/sustech/cs110/snake/view/music2.wav").toString();
    //MediaPlayer mediaPlayer=new MediaPlayer(media);

    //if(MouseEvent.MOUSE_PRESSED)

    //暂停




    public void turnLeft() {
        if (Context.INSTANCE.currentGame().getSnake().getDirection() != Direction.RIGHT) {
            Context.INSTANCE.currentGame().getSnake().setDirection(Direction.LEFT);
        }
    }

    public void turnUp() {
        if (Context.INSTANCE.currentGame().getSnake().getDirection() != Direction.DOWN) {
            Context.INSTANCE.currentGame().getSnake().setDirection(Direction.UP);
        }
    }

    public void turnRight() {
        if (Context.INSTANCE.currentGame().getSnake().getDirection() != Direction.LEFT) {
            Context.INSTANCE.currentGame().getSnake().setDirection(Direction.RIGHT);
        }
    }

    public void turnDown() {
        if (Context.INSTANCE.currentGame().getSnake().getDirection() != Direction.UP) {
            Context.INSTANCE.currentGame().getSnake().setDirection(Direction.DOWN);
        }
    }

    /*public void changeDifficulty() {
        setupDaemonScheduler();
        ToggleGroup toggleGroup=new ToggleGroup();
        RadioButton diffEasy =new RadioButton("Easy");
        RadioButton diffMedium =new RadioButton("Medium");
        RadioButton diffHard =new RadioButton("Hard");
        diffEasy.setToggleGroup(toggleGroup);
        diffMedium.setToggleGroup(toggleGroup);
        diffHard.setToggleGroup(toggleGroup);

        /*toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle toggle, Toggle t1) {
                RadioButton diff=(RadioButton)newValue;
            }diffEasy.setSelected(true);
        });

        if(diffEasy.isSelected()){

            MOVE_DURATION=500;

        }
        if(diffMedium.isSelected()){

            MOVE_DURATION=200;
        }
        if(diffHard.isSelected()){

            MOVE_DURATION=50;
        }


    }*/
    public void changeEasy() {



        MOVE_DURATION=500;
        setupDaemonScheduler();


    }
    public void changeMedium() {


        MOVE_DURATION=200;
        setupDaemonScheduler();

    }
    public void changeHard() {

        MOVE_DURATION=50;
        setupDaemonScheduler();

    }


    private void setupDaemonScheduler() {
        if (Objects.nonNull(gameDaemonTask)) {
            gameDaemonTask.cancel(true);
        }
        gameDaemonTask = scheduler.scheduleAtFixedRate(
                new GameDaemonTask(),
                0, MOVE_DURATION,
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * 画面修改，传入修改的点就会按照要修改的颜色进行修改
     * 也就是说，这里的移动全部都是通过修改点的颜色进行的
     * @param event
     */
    @Subscribe
    public void rerenderChanges(BoardRerenderEvent event) {
        board.repaint(event.getDiff());
    }

    /**
     * 吃到食物之后的操作
     * 1、当前分数升高
     * 2、如果高于历史最高分，需要修改历史最高分
     * @param event
     */
    @Subscribe
    public void beanAte(BeanAteEvent event) {
        final String scoreText = textCurrentScore.getText();
        final String[] currentScoreSplit = scoreText.split(":");
        int currentScore = Integer.parseInt(currentScoreSplit[1].replaceAll("\\s", ""));
        currentScore = currentScore + 1;
        textCurrentScore.setText(currentScoreSplit[0] + ": " + currentScore);

        final String highestScoreText = textPlayerHighest.getText();
        final String[] highestScoreSplit = highestScoreText.split(":");
        final int highestScore = Integer.parseInt(highestScoreSplit[1].replaceAll("\\s", ""));
        //**
        if(highestScore < currentScore){
            textPlayerHighest.setText(highestScoreSplit[0] + ": " + currentScore);
        }
    }

    /**
     * 游戏结束后的操作
     * @param event
     */
    @Subscribe
    public void gameOver(GameOverEvent event) throws IOException {
        generateLeaderboard();
        String gameOverMediaUrl = "game over.wav";
        Media gameOverMedia = new Media(new File(gameOverMediaUrl).toURI().toString());
        gameOverMediaPlayer = new MediaPlayer(gameOverMedia);
        Context.INSTANCE.currentGame().setPlaying(false);
        // TODO: add some code here
        System.out.println("Game over");
        //String url1="game over.wav";
        //Media media=new Media(new File(url1).toURI().toString());
        //MediaPlayer mediaPlayer=new MediaPlayer(media);
        gameMediaPlayer.pause();


        //this.btnPause.setText("Game over");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                btnPause.setText("GameEnd!");
            }
        });
    }
    @FXML
    private void toGoBack(){
        Context.INSTANCE.currentGame().setPlaying(false);
        ((Stage)btnPause.getScene().getWindow()).close();
        new AdvancedStage("logIn.fxml").withTitle("Log").shows();
        gameMediaPlayer.pause();
    }
    public void generateLeaderboard() throws FileNotFoundException {
        ArrayList<LeaderboardUsers> leaderboardUsers=new ArrayList<>();
        File file=new File("Leaderboard.txt");
        Scanner input=new Scanner(file);
        while(input.hasNextLine()){
            String[]leaderboardUsersInfo=input.nextLine().split(" ");

            leaderboardUsers.add(new LeaderboardUsers(leaderboardUsersInfo[0],Integer.parseInt(leaderboardUsersInfo[1]),
                    Integer.parseInt(leaderboardUsersInfo[2])));
        }
        input.close();
        leaderboardUsers.add(new LeaderboardUsers(playerID,Integer.parseInt((textCurrentScore.getText().split(" "))[2]),
                Integer.parseInt((textTimeAlive.getText().split(" "))[2].substring
                        (0,(textTimeAlive.getText().split(" "))[2].length()-1))));
        leaderboardUsers.sort(new Comparator<LeaderboardUsers>() {
            @Override
            public int compare(LeaderboardUsers o1, LeaderboardUsers o2) {
                if(o1.getScore()> o2.getScore()){
                    return -1;
                }
                else if(o1.getScore()<o2.getScore()){
                    return 1;
                }
                else if(o1.getAliveTime()>o2.getAliveTime()){
                    return -1;
                }
                else{
                    return 1;
                }
            }
        });

        PrintWriter output=new PrintWriter("Leaderboard.txt");
        if(leaderboardUsers.size()==1){
            for(int i=0;i<leaderboardUsers.size();i++){
                output.print(leaderboardUsers.get(i).getId());
                output.print(" ");
                output.print(leaderboardUsers.get(i).getScore());
                output.print(" ");
                output.println(leaderboardUsers.get(i).getAliveTime());
            }
            output.close();
            //first.setText("No.1:  "+leaderboardUsers.get(0).getId()+"   "+leaderboardUsers.get(0).getScore()+"   "+leaderboardUsers.get(0).getAliveTime()+"s");
        }
        if(leaderboardUsers.size()==2){
            for(int i=0;i<leaderboardUsers.size();i++){
                output.print(leaderboardUsers.get(i).getId());
                output.print(" ");
                output.print(leaderboardUsers.get(i).getScore());
                output.print(" ");
                output.println(leaderboardUsers.get(i).getAliveTime());
            }
            output.close();
            //first.setText("No.1:  "+leaderboardUsers.get(0).getId()+"   "+leaderboardUsers.get(0).getScore()+"   "+leaderboardUsers.get(0).getAliveTime()+"s");
            //second.setText("No.2:  "+leaderboardUsers.get(1).getId()+"   "+leaderboardUsers.get(1).getScore()+"   "+leaderboardUsers.get(1).getAliveTime()+"s");
        }
        if(leaderboardUsers.size()>=3){
            for(int i=0;i<3;i++){
                output.print(leaderboardUsers.get(i).getId());
                output.print(" ");
                output.print(leaderboardUsers.get(i).getScore());
                output.print(" ");
                output.println(leaderboardUsers.get(i).getAliveTime());
            }
            output.close();
            //first.setText("No.1:  "+leaderboardUsers.get(0).getId()+"   "+leaderboardUsers.get(0).getScore()+"   "+leaderboardUsers.get(0).getAliveTime()+"s");
            //second.setText("No.2:  "+leaderboardUsers.get(1).getId()+"   "+leaderboardUsers.get(1).getScore()+"   "+leaderboardUsers.get(1).getAliveTime()+"s");
            //third.setText("No.3:  "+leaderboardUsers.get(2).getId()+"   "+leaderboardUsers.get(2).getScore()+"   "+leaderboardUsers.get(2).getAliveTime()+"s");
        }
        //output.close();
    }
}
