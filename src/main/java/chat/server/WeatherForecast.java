package chat.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class WeatherForecast {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String input;

        while (true) {
            System.out.print("Enter locality to find out the weather forecast: ");
            try {
                input = reader.readLine();
                if (input.isEmpty()) {
                    break;
                }
                input = input.trim().replaceAll(" ", "+");
                String geocodingURL = "https://geocoding-api.open-meteo.com/v1/search?name=" + input + "&count=1&language=en&format=json";
                double[] coordinates = getCoordinatesByLocality(geocodingURL, input);

                String weatherURL = "https://api.open-meteo.com/v1/forecast?latitude=" + coordinates[0] + "&longitude=" + coordinates[1] + "&hourly=temperature_2m&forecast_days=16";

                sendInfo(weatherURL);
            } catch (NullPointerException e) {
                System.out.println("Such locality doesn't exist! Please try again...");
            }
            catch (IOException e) {
                System.out.println("Something went wrong! Please try again...");
            }
        }
    }

    public static double[] getCoordinatesByLocality(String geocodingURL, String input) throws IOException {
        String rawData = readRawData(geocodingURL);

        JsonObject results = new JsonParser().parse(rawData).getAsJsonObject().getAsJsonArray("results").get(0).getAsJsonObject();
        double longitude = results.get("longitude").getAsDouble();
        double latitude = results.get("latitude").getAsDouble();
        String name = results.get("name").getAsString();

        if (!input.equalsIgnoreCase(name.trim().replaceAll(" ", "+").replaceAll("’", ""))) System.err.println("WARNING! Your input may not match the city you are looking for...");

        double[] coordinates = new double[2];

        coordinates[0] = latitude;
        coordinates[1] = longitude;

        return coordinates;
    }

    public static String sendInfo(String weatherURL) throws IOException {
        StringBuilder result = new StringBuilder();
        String rawData = readRawData(weatherURL);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd, MMM, yyyy", Locale.ENGLISH);

        JsonObject hourly = new JsonParser().parse(rawData).getAsJsonObject().get("hourly").getAsJsonObject();

        String[] dates = new String[384];
        Double[] temperatures = new Double[384];
        JsonArray time = hourly.getAsJsonArray("time").getAsJsonArray();
        JsonArray temperature_2m = hourly.getAsJsonArray("temperature_2m").getAsJsonArray();

        for (int i = 0; i < 384; i++) {
            dates[i] = time.get(i).getAsString();
            temperatures[i] = temperature_2m.get(i).getAsDouble();
        }

        result.append(String.format("%-27s", "Date"));
        result.append("avg\n");

        double sum = 0.0;
        for (int i = 0; i < 384; i++) {
            if (i % 24 == 0) {
                result.append(String.format("%-20s", formatter.format(LocalDate.parse(dates[i].substring(0, 10)))));
                sum = 0.0;
            }
            sum += temperatures[i];

            if (i % 24 == 23) {
                result.append(Math.round(sum / 24 * 100) / 100.0).append("\n");
            }
        }
        return result.toString();
    }

    private static String readRawData(String geocodingURL) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new URL(geocodingURL)
                                .openConnection()
                                .getInputStream()));

        StringBuilder rawData = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            rawData.append(line);
        }

        return rawData.toString();
    }
}
