package com.toy.getyourfriday.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UpdateDTO {

    private Integer updateId;
    private MessageDTO message;
}
