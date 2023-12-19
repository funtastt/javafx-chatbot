package dslite.chat.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameView extends BaseView {

    private AnchorPane pane;
    private Label wolf;
    private Label egg;
    private Label scoreLabel;
    private Rectangle ground;
    private int score;

    private Timeline eggTimeline;

    private static final double WOLF_SPEED = 10.0;
    private static final double EGG_FALL_SPEED = 1.5;

    public GameView() {
        score = 0;
    }

    @Override
    public Parent getView() {
        if (pane == null) {
            createView();
        }
        return pane;
    }

    private void createView() {
        pane = new AnchorPane();
        wolf = new Label("🐺");
        egg = new Label("🥚");
        scoreLabel = new Label("Score: 0");
        ground = new Rectangle(0, 0, Color.BURLYWOOD);

        egg.setLayoutX(Math.random() * (pane.getWidth() - egg.getWidth()));
        egg.setLayoutY(0);

        egg.setScaleX(2.0);
        egg.setScaleY(2.0);

        wolf.setScaleX(6.0);
        wolf.setScaleY(6.0);

        pane.getChildren().addAll(ground, wolf, egg, scoreLabel);

        pane.setFocusTraversable(true);

        pane.widthProperty().addListener((obs, oldVal, newVal) -> updateWolfPosition());
        pane.heightProperty().addListener((obs, oldVal, newVal) -> updateWolfPosition());

        eggTimeline = new Timeline(new KeyFrame(Duration.millis(16), event -> {
            double newY = egg.getLayoutY() + EGG_FALL_SPEED;
            egg.setLayoutY(newY);

            if (newY > pane.getHeight()) {
                resetEggPosition();
            }

            checkCollision();
        }));
        eggTimeline.setCycleCount(Timeline.INDEFINITE);
        eggTimeline.play();

        pane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT) {
                moveWolfLeft();
            } else if (event.getCode() == KeyCode.RIGHT) {
                moveWolfRight();
            }
        });
    }

    private void moveWolfLeft() {
        wolf.setLayoutX(Math.max(wolf.getLayoutX() - WOLF_SPEED, 0));
        checkCollision();
    }

    private void moveWolfRight() {
        wolf.setLayoutX(Math.min(wolf.getLayoutX() + WOLF_SPEED, pane.getWidth() - wolf.getWidth()));
        checkCollision();
    }

    private void checkCollision() {
        if (egg.getBoundsInParent().intersects(wolf.getBoundsInParent())) {
            score++;
            scoreLabel.setText("Score: " + score);
            resetEggPosition();
        }

        if (egg.getLayoutY() + egg.getHeight() >= pane.getHeight()) {
            showGameOverAlert();
        }
    }

    private void resetEggPosition() {
        egg.setLayoutX(Math.random() * (pane.getWidth() - egg.getWidth()));
        egg.setLayoutY(0);
    }

    private void updateWolfPosition() {
        wolf.setLayoutX((pane.getWidth() - wolf.getBoundsInParent().getWidth()) / 2);
        wolf.setLayoutY(pane.getHeight() - wolf.getBoundsInParent().getHeight());

        ground.setWidth(pane.getWidth());
        ground.setHeight(pane.getHeight());
    }

    private void showGameOverAlert() {
        eggTimeline.stop();
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("Game Over");
        alert.setContentText("Final Score: " + score);

        Stage stage = (Stage) pane.getScene().getWindow();
        alert.initOwner(stage);

        alert.setOnHidden(evt -> {
            getChatApplication().setView(getChatApplication().getBotView());
            pane = null;
        });
        alert.show();
    }
}