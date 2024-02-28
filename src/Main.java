import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    private static final String API_BASE_URL = "https://prices.runescape.wiki/api/v1/osrs/latest?id=";

    public static void main(String args[]) {
        try {
            GEPrice priceInfo = Main.fetchGEPrice(563); // Replace with your desired Item ID
            System.out.println("High price: " + priceInfo.getHigh());
        } catch (Exception e) {
            System.err.println("Error fetching price data: " + e.getMessage());
        }
    }

    public static GEPrice fetchGEPrice(int itemId) throws Exception {
        String urlString = API_BASE_URL + itemId;
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", "GEPriceFetcher/1.0");
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return parseGEPriceData(response.toString());
        } else {
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream())); // Read the error stream
            String errorLine;
            StringBuilder errorResponse = new StringBuilder();
            while ((errorLine = errorReader.readLine()) != null) {
                errorResponse.append(errorLine);
            }
            errorReader.close();
            throw new Exception("API request failed with status code: " + responseCode + ". Error Response: " + errorResponse.toString());
        }
    }

    public static class GEPrice {
        private int high;
        private long highTime;
        private int low;
        private long lowTime;

        // Constructor
        public GEPrice(int high, long highTime, int low, long lowTime) {
            this.high = high;
            this.highTime = highTime;
            this.low = low;
            this.lowTime = lowTime;
        }

        // Getters
        public int getHigh() {
            return high;
        }

        public long getHighTime() {
            return highTime;
        }

        public int getLow() {
            return low;
        }

        public long getLowTime() {
            return lowTime;
        }

    }

    public static GEPrice parseGEPriceData(String jsonString) {
        Gson gson = new Gson();
        JsonObject data = gson.fromJson(jsonString, JsonObject.class).getAsJsonObject("data");

        // Assumes the top-level key in "data" represents the Item ID
        String itemId = data.keySet().iterator().next();
        JsonObject priceData = data.getAsJsonObject(itemId);

        int high = priceData.get("high").getAsInt();
        long highTime = priceData.get("highTime").getAsLong();
        int low = priceData.get("low").getAsInt();
        long lowTime = priceData.get("lowTime").getAsLong();

        return new GEPrice(high, highTime, low, lowTime);
    }
}
