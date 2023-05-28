package cn.edu.sustech.cs110.snake.control;

import cn.edu.sustech.cs110.snake.Context;
import cn.edu.sustech.cs110.snake.model.Game;
import cn.edu.sustech.cs110.snake.view.AdvancedStage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Scene;

import  java.io.File;
import java.util.Scanner;
import  java.util.ArrayList;
import java.io.FileNotFoundException;
import  java.io.PrintWriter;
public class LogIn  {
    public File storage = new File("贪吃蛇的登录界面.txt");
    public File IDS = new File("用户.txt");
    @FXML
    private Parent root;
    @FXML
    Scanner input = new Scanner(System.in);
    @FXML
    String name;
    @FXML
    TextField ID;
    @FXML
    TextField PASSWORD;
    @FXML
    String id;
    @FXML
    String password;
    @FXML
    private Button LogIn;
    @FXML
    private Button Register;
    //@FXML
    //private final Text textPlayerName;
    @FXML
    private Label wrong;

    //public LogIn(Text textPlayerName) {
     //   this.textPlayerName = textPlayerName;
    //}


    @FXML
    protected void toCheckPassword() throws FileNotFoundException{
        PrintWriter out = new PrintWriter(IDS);
        Context.INSTANCE.currentGame(new Game(40,40));
            id = ID.getText();
            password = PASSWORD.getText();
        if (doLogin(id, password)){
                ((Stage)LogIn.getScene().getWindow()).close();
               out.print(id);
               out.close();
                //textPlayerName.setText("Player："+id);
                new AdvancedStage("game.fxml").withTitle("Snake").shows();

            }
            else{
                wrong.setText("wrong cord, please input again.");
                ID.clear();
                PASSWORD.clear();

            }
    }
    //public LogIn(Text textPlayerName) {
       // this.textPlayerName = textPlayerName;
    //}
    @FXML
    public void toRegister(ActionEvent event) throws FileNotFoundException{
        wrong.setText("please add id and password");
        id = ID.getText();
        password = PASSWORD.getText();
        if (doLogin(id,password)){
            wrong.setText("registered");
        }
        else {
            ArrayList<String> list = loadUserInfo();
            list.add(ID.getText());
            list.add(PASSWORD.getText());
            writeFile(list);
        }
    }
    private boolean doLogin(String username, String password) throws FileNotFoundException {
        ArrayList<String> userInfos = loadUserInfo();
        int usernameIndex = userInfos.indexOf(username);
        if (usernameIndex == -1) {
            return false;
        }
        if (usernameIndex % 2 == 0 && usernameIndex + 1 < userInfos.size() &&
                String.valueOf(password).equals(userInfos.get(usernameIndex + 1))) {
            return true;
        }
        return false;
    }
    private ArrayList<String> loadUserInfo() throws FileNotFoundException {
        ArrayList<String> list = new ArrayList<>();
        Scanner in = new Scanner(storage);
        while (in.hasNext()) {
            String line = in.nextLine();
            list.add(line);
        }
        in.close();
        return list;
    }
    private void writeFile(ArrayList<String> list) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(storage);
        for (int i = 0; i < list.size(); i++) {
            out.println(list.get(i));
        }
        out.flush();
        out.close();
    }
}
