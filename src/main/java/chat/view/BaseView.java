package chat.view;

import chat.ChatApplication;
import javafx.scene.Parent;

public abstract class BaseView {

    private static ChatApplication chatApplication;

    public static ChatApplication getChatApplication() {
        if (chatApplication != null) {
            return chatApplication;
        }
        throw new RuntimeException("No app in base view");
    }

    public static void setChatApplication(ChatApplication chatApplication) {
        BaseView.chatApplication =chatApplication;
    }

    public abstract Parent getView();
}
