package com.demo.commons.error.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDto implements Serializable  {

    @JsonProperty("origin")
    private ErrorOrigin origin;

    private String code;

    private String message;
}