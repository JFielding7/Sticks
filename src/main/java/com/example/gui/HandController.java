package com.example.gui;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class HandController {
    @FXML
    ImageView playerRight;
    @FXML
    ImageView playerLeft;
    @FXML
    ImageView comLeft;
    @FXML
    ImageView comRight;
    @FXML
    HBox selectedIndicator;
    @FXML
    VBox splitOptions;
    @FXML
    HBox turnSelection;
    @FXML
    Button goFirstButton;
    @FXML
    Button goSecondButton;
    @FXML
    Button randomButton;
    TranslateTransition move = new TranslateTransition();
    int playerHandSelected = -1;
    boolean selectable = false;
    int playerLeftFingers = 1;
    int playerRightFingers = 1;
    int comLeftFingers = 1;
    int comRightFingers = 1;

    Image[] comRightHands = compileHandImages("ComRight");
    Image[] comLeftHands = compileHandImages("ComLeft");
    Image[] playerRightHands = compileHandImages("PlayerRight");
    Image[] playerLeftHands = compileHandImages("PlayerLeft");

     private Image[] compileHandImages(String hand){
        Image[] hands = new Image[5];
        for(int i = 0; i < 5; i++)
            hands[i] = new Image(Objects.requireNonNull(getClass().getResourceAsStream(i + "Fingers" + hand + ".png")));
        return hands;
    }

    public void setupPlayerTurn(){
        turnSelection.setVisible(false);
        selectable = true;
        updateSplits();
    }

    public void setupComTurn(){
        turnSelection.setVisible(false);
        selectable = false;
        makeComputerMove();
    }

    public void setupRandomTurn(){
        int random = new Random().nextInt(2);
        if(random == 1) setupPlayerTurn();
        else setupComTurn();
    }

    synchronized void updatePlayerHands(){
        playerLeft.setImage(playerLeftHands[playerLeftFingers]);
        playerRight.setImage(playerRightHands[playerRightFingers]);
    }

    synchronized void updateComHands(){
        comLeft.setImage(comLeftHands[comLeftFingers]);
        comRight.setImage(comRightHands[comRightFingers]);
    }

    public void clickedRight(){
        if(!selectable || playerRightFingers == 0) return;
        playerHandSelected = 1;
        selectedIndicator.setLayoutX(448);
        selectedIndicator.setVisible(true);
    }

    public void clickedLeft(){
        if(!selectable || playerLeftFingers == 0) return;
        playerHandSelected = 0;
        selectedIndicator.setLayoutX(232);
        selectedIndicator.setVisible(true);
    }

    public void clickedComRight(){
        if(playerHandSelected == -1 || comRightFingers == 0) return;
        comRightFingers = (comRightFingers + (playerHandSelected == 1 ? playerRightFingers : playerLeftFingers)) % 5;
        changeComHand(comRight, comRightHands, comRightFingers, 0, -150);
    }

    public void clickedComLeft(){
        if(playerHandSelected == -1 || comLeftFingers == 0) return;
        comLeftFingers = (comLeftFingers + (playerHandSelected == 1 ? playerRightFingers : playerLeftFingers)) % 5;
        changeComHand(comLeft, comLeftHands, comLeftFingers, 150, 0);
    }

    public void changeComHand(ImageView comHand, Image[] handImages, int numFingers, int dx1, int dx2){
        if(playerHandSelected == 0) moveHand(playerLeft, dx1, -100, 1);
        else moveHand(playerRight, dx2, -100, 1);
        playerHandSelected = -1;
        selectable = false;
        Thread handUpdater = new Thread(() -> {
            try { Thread.sleep(250);
            } catch (InterruptedException ex) { throw new RuntimeException(ex);}
            comHand.setImage(handImages[numFingers]);
        });
        handUpdater.start();
        splitOptions.setVisible(false);
    }

    public void moveHand(ImageView hand, int dx, int dy, int player){
        selectedIndicator.setVisible(false);
        move = new TranslateTransition();
        move.setNode(hand);
        move.setCycleCount(2);
        move.setByY(dy);
        move.setByX(dx);
        move.setDuration(Duration.seconds(0.5));
        move.setAutoReverse(true);
        if(player == 1) move.setOnFinished(e -> makeComputerMove());
        if(player == 0) move.setOnFinished(e -> {
            String winner = getWinner();
            if(winner != null){
                try {
                    setWinScreen("You Lose!");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            else setupPlayerTurn();
        });
        move.play();
    }

    public void performSplit(ActionEvent e) {
        if(move.currentTimeProperty().getValue().toSeconds() % 1 != 0) return;
        splitOptions.setVisible(false);
        String splitCombo = ((Button) e.getSource()).getText();
        String[] fingerCounts = splitCombo.split(" - ");
        playerLeftFingers = Integer.parseInt(fingerCounts[0]);
        playerRightFingers = Integer.parseInt(fingerCounts[1]);
        moveHand(playerRight, -60, 0, -1);
        moveHand(playerLeft, 60, 0, 1);
        Thread playerHandUpdater = new Thread(() -> {
                try { Thread.sleep(500);
                } catch (InterruptedException ex) { throw new RuntimeException(ex);}
                updatePlayerHands();
        });
        playerHandUpdater.start();
    }

    static ArrayList<String> getSplits(int hand1, int hand2){
        int sum = hand1 + hand2;
        int minHand = Math.min(hand1, hand2);
        ArrayList<String> splits = new ArrayList<>();
        for(int i = sum > 4 ? sum - 4 : 0; i <= sum >> 1; i++){
            if(i != minHand)
                splits.add(String.format("%d - %d", sum - i, i));
        }
        return splits;
    }

    public void updateSplits(){
        ArrayList<String> splitCombos = getSplits(playerLeftFingers, playerRightFingers);
        int i = 0;
        for(Node node : splitOptions.getChildren()){
            if(i >= splitCombos.size()) node.setVisible(false);
            else{
                Button button = (Button) node;
                button.setText(splitCombos.get(i++));
                button.setVisible(true);
            }
        }
        splitOptions.setVisible(true);
    }

    public void makeComputerMove(){
        State comMove = Move.bestMove(new State(comLeftFingers, comRightFingers, playerLeftFingers, playerRightFingers, 0));
        if((comMove.max1 ^ comMove.min1 ^ playerRightFingers ^ playerLeftFingers) == 0){
            moveHand(comLeft, -60, 0, -1);
            moveHand(comRight, 60, 0, 0);
            comRightFingers = comMove.max2;
            comLeftFingers = comMove.min2;
            updateComHands();
        }
        else{
            int diff = (comMove.max1 + comMove.min1 - playerLeftFingers - playerRightFingers + 5) % 5;
            ImageView attacker = diff == comRightFingers ? comRight : comLeft;
            int dx, newLeftCount = (diff + playerLeftFingers) % 5;
            if(Math.max(newLeftCount, playerRightFingers) == comMove.max1 && Math.min(newLeftCount, playerRightFingers) == comMove.min1){
                playerLeftFingers = newLeftCount;
                dx = attacker == comLeft ? -150 : 0;
            }
            else{
                playerRightFingers = (playerRightFingers + diff) % 5;
                dx = attacker == comLeft ? 0 : 150;
            }
            moveHand(attacker, dx, 100, 0);
            Thread playerHandUpdater = new Thread(() -> {
                try { Thread.sleep(500);
                } catch (InterruptedException ex) { throw new RuntimeException(ex);}
                updatePlayerHands();
            });
            playerHandUpdater.start();
        }
    }

    String getWinner(){
        if(playerRightFingers == 0 && playerLeftFingers == 0){
            return "You Lose!";
        }
        else if(comRightFingers == 0 && comLeftFingers == 0){
            return "You Win!";
        }
        return null;
    }

    public void clickedBackground(MouseEvent e){
        double x = e.getX(), y = e.getY();
        if((!playerLeft.contains(new Point2D(x - playerLeft.getLayoutX(), y - playerLeft.getLayoutY())) || playerLeftFingers == 0) &&
                (!playerRight.contains(new Point2D(x - playerRight.getLayoutX(), y - playerRight.getLayoutY())) || playerRightFingers == 0)){
            selectedIndicator.setVisible(false);
            playerHandSelected = -1;
        }
    }

    public void setTitleScene(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Title.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(parent));
        stage.show();
    }

    public void setWinScreen(String message) throws IOException {
        WinScreenController.winMessage = message;
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("WinScreen.fxml")));
        Stage stage = (Stage) selectedIndicator.getScene().getWindow();
        stage.setScene(new Scene(parent));
        stage.show();
    }
}
