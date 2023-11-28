package chat.view;

import chat.model.UserConfig;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.io.IOException;

import static chat.server.CurrencyTracker.sendCurrencyInfo;
import static chat.server.WeatherForecast.*;
import static javafx.scene.input.KeyEvent.KEY_PRESSED;

public class BotView extends BaseView {
    private TextArea input;
    private TextArea conversation;
    private AnchorPane pane;
    private boolean isLaunched = false;
    private static final String ACCESS_KEY = "334138957e68d9b368dae65192431b4f";

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
            } else if (message.startsWith("/weather")) {
                showWeatherInfo(message.substring(8));
            } else if (message.startsWith("/currency")) {
                showCurrencyInfo(message.substring(9).trim());
            } else {
                switch (message) {
                    case "/stop" -> System.exit(0);
                    case "/help" -> showHelp();
                    case "/chat" -> redirectToChat();
                    default -> conversation.appendText("Can't handle such command. Please use \"/help\" command.\n");
                }
            }
        } else {
            conversation.appendText("You should use \"/start\" firstly!\n");
        }
    }

    private void showHelp() {
        conversation.appendText("\nHere are commands that can be used in this bot\n");

        conversation.appendText("/start - the first command required to launch the bot\n");
        conversation.appendText("/stop - use this command to stop the bot and close application\n");
        conversation.appendText("/help - use this command to learn about all the features of the bot\n");
        conversation.appendText("/weather [city] - enter city after space symbol to get average temperature for the next 16 days\n");
        conversation.appendText("/currency [currency] - enter currency after space symbol to get 2 weeks history of currency changes\n");
    }

    private void showCurrencyInfo(String currency) {
        try {
            String url = "http://api.currencylayer.com/timeframe?access_key=" + ACCESS_KEY + "&source=" + currency + "&currencies=RUB&start_date=2023-11-13&end_date=2023-11-27";
            String rawData = readRawData(url);
            String response = sendCurrencyInfo(rawData);
            System.out.println(url);
            System.out.println(rawData);
            conversation.appendText("\nShowing " + currency.toUpperCase() + " currency change history for the last 2 weeks:\n");
            conversation.appendText(response + "\n");
        } catch (Exception e) {
            conversation.appendText("Something went wrong! Please try again...");
        }
    }

    private void showWeatherInfo(String city) {
        try {
            if (city.isEmpty()) {
                return;
            }
            String cityFormatted = city.trim().replaceAll(" ", "+");
            String geocodingURL = "https://geocoding-api.open-meteo.com/v1/search?name=" + cityFormatted + "&count=1&language=en&format=json";
            double[] coordinates = getCoordinatesByLocality(geocodingURL, cityFormatted);

            String weatherURL = "https://api.open-meteo.com/v1/forecast?latitude=" + coordinates[0] + "&longitude=" + coordinates[1] + "&hourly=temperature_2m&forecast_days=16";

            String response = sendInfo(weatherURL);
            conversation.appendText("\nShowing weather forecast for your input: " + city + "\n");
            conversation.appendText(response);
        } catch (NullPointerException e) {
            conversation.appendText("Such locality doesn't exist! Please try again...");
        }
        catch (IOException e) {
            conversation.appendText("Something went wrong! Please try again...");
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
