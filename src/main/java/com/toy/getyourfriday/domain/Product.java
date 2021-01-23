package com.toy.getyourfriday.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Product {

    private final String link;
    private final String thumbnail;
}
