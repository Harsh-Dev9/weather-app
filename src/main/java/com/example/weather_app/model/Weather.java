package com.example.weather_app.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Weather implements Serializable {

    private String address;
    private String timezone;
    private String description;
    private String sunset;
    private String sunrise;
    private String dateTime;
    private double temp;
}
