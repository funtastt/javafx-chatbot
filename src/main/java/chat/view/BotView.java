package chat.view;

import chat.model.UserConfig;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import static javafx.scene.input.KeyEvent.KEY_PRESSED;

public class BotView extends BaseView {
    private TextArea input;
    private TextArea conversation;
    private AnchorPane pane;

    private boolean isLaunched = false;

    private final EventHandler<KeyEvent> eventHandler = new EventHandler<>() {
        @Override
        public void handle(KeyEvent event) {
            if (event.getCode() == KeyCode.ENTER) {
                String message = input.getText();
                getChatApplication().getChatClient().sendMessage(message);


                handleUserMessage(message.trim());

                input.clear();
                event.consume();
            }
        }
    };

    private void handleUserMessage(String message) {
        if ("/start".equalsIgnoreCase(message)) {
            if (isLaunched) {
                conversation.appendText("Bot is already launched!\n");
            } else {
                conversation.appendText("Bot is ready to use :)\n");
                isLaunched = true;
            }
        } else if (isLaunched) {
            if (!message.startsWith("/")) {
                conversation.appendText("Chat bot command must start with /\n");
            } else {
                switch (message) {
                    case "/stop" -> System.exit(0);
                    case "/help" -> conversation.appendText("Help command\n");
                    case "/weather" -> conversation.appendText("Weather command\n");
                    case "/currency" -> conversation.appendText("Currency command\n");
                    case "/chat" -> redirectToChat();
                    default -> conversation.appendText("Can't handle such command\n");
                }
            }
        } else {
            conversation.appendText("You should use \"/start\" firstly!\n");
        }


    }

    private void redirectToChat() {
        getChatApplication().setView(getChatApplication().getConfigView());
    }

    public void appendMessage(String message) {
        if (message != null) {
            conversation.appendText(message);
        }
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

        conversation = new TextArea();
        conversation.setEditable(false);
        conversation.setWrapText(true);

        AnchorPane.setTopAnchor(conversation, 10.0);
        AnchorPane.setLeftAnchor(conversation, 10.0);
        AnchorPane.setRightAnchor(conversation, 10.0);

        input = new TextArea();
        input.setMaxHeight(50);

        AnchorPane.setBottomAnchor(input, 10.0);
        AnchorPane.setLeftAnchor(input, 10.0);
        AnchorPane.setRightAnchor(input, 10.0);

        input.addEventHandler(KEY_PRESSED, eventHandler);
        pane.getChildren().addAll(input, conversation);

        // Default settings
        UserConfig userConfig = new UserConfig();
        userConfig.setHost("127.0.0.1");
        userConfig.setUsername("admin");
        userConfig.setPort(5555);

        getChatApplication().setUserConfig(userConfig);

        getChatApplication().startChat();
    }
}
