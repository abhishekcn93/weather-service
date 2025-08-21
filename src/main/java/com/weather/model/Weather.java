package com.weather.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Entity
@Table(name = "weather")
public class Weather implements Serializable {

    @JsonUnwrapped
    @EmbeddedId
    private WeatherId weatherId;
    private String weatherDetails;
}
