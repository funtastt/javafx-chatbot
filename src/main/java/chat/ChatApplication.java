package dslite.chat;

import dslite.chat.client.ChatClient;
import dslite.chat.model.UserConfig;
import dslite.chat.view.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ChatApplication extends Application {

    private UserConfig userConfig;
    private UserConfigView configView;
    private ChatView chatView;
    private BotView botView;
    private GameView gameView;
    private BorderPane root;
    private ChatClient chatClient;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Chat");
        primaryStage.setOnCloseRequest(e -> System.exit(0));
        BaseView.setChatApplication(this);
        configView = new UserConfigView();
        chatView = new ChatView();
        botView = new BotView();
        gameView = new GameView();

        chatClient = new ChatClient(this);
        root = new BorderPane();
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
        setView(botView);
    }

    public void appendMessage(String message) {
        chatView.appendMessage(message);
    }

    public void startChat() {
        chatClient.start();
    }

    public void setUserConfig(UserConfig userConfig) {
        this.userConfig = userConfig;
    }

    public UserConfig getUserConfig() {
        return userConfig;
    }

    public UserConfigView getConfigView() {
        return configView;
    }

    public ChatView getChatView() {
        return chatView;
    }

    public BotView getBotView() {
        return botView;
    }

    public void setView(BaseView view) {
        root.setCenter(view.getView());
    }

    public ChatClient getChatClient() {
        return chatClient;
    }

    public void setChatClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public BaseView getGameView() {
        return gameView;
    }
}
