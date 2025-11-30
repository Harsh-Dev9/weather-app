package com.example.weather_app.controller;

import com.example.weather_app.model.Weather;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Value("${API_KEY}")
    private String API_KEY;

    @GetMapping("/{city}")
    public Weather getWeatherByCity(@PathVariable String city) throws JsonProcessingException {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q="
                + city + "&appid=" + API_KEY + "&units=metric";

        RestTemplate restTemplate = new RestTemplate();
        String data = restTemplate.getForObject(apiUrl, String.class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(data);

            // OpenWeather fields
            String address = rootNode.path("name").asText(null); // city name
            String timezone = String.valueOf(rootNode.path("timezone").asInt()); // seconds offset

            String dayDescription = rootNode.path("weather")
                    .get(0)
                    .path("description")
                    .asText(null);

            // sunrise & sunset as strings (UNIX timestamps)
            String sunrise = String.valueOf(rootNode.path("sys").path("sunrise").asLong());
            String sunset = String.valueOf(rootNode.path("sys").path("sunset").asLong());

            // date/time as UNIX timestamp string
            String todayDate = String.valueOf(rootNode.path("dt").asLong());

            // temperature already in Celsius because of units=metric
            double temperature = rootNode.path("main").path("temp").asDouble();

            return new Weather(
                    address,
                    timezone,
                    dayDescription,
                    sunset,
                    sunrise,
                    todayDate,
                    temperature
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse weather data", e);
        }
    }

    // You actually don't need this anymore if you are using units=metric
    public static double fahrenheitToCelsius(double fahrenheit) {
        return (fahrenheit - 32) / 1.8;
    }
}
