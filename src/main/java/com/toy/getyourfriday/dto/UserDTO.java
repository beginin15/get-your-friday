package com.toy.getyourfriday.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserDTO {

    @JsonProperty("id")
    private Integer chatId;
    private boolean isBot;
    private String firstName;
    private String lastName;
}
