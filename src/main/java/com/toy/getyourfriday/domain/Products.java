package com.toy.getyourfriday.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class Products {

    private final List<Product> products;
}
