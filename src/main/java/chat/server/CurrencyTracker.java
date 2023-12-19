package dslite.chat.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

public class CurrencyTracker {
    public static String sendCurrencyInfo(String json) {
        StringBuilder result = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd, MMM, yyyy", Locale.ENGLISH);
        JsonObject quotes = new JsonParser().parse(json).getAsJsonObject().get("quotes").getAsJsonObject();

        Set<String> keys = quotes.keySet();

        for (String key : keys) {
            if (quotes.get(key) instanceof JsonObject day) {
                result.append(formatter.format(LocalDate.parse(key)));
                Set<String> dayKeySet = day.keySet();
                for (String currencyAbbr : dayKeySet) {
                    Double currency = day.get(currencyAbbr).getAsDouble();
                    result.append(":   ").append(currency).append(" RUB\n");
                }
            }
        }

        return result.toString();
    }
}
