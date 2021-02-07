package com.toy.getyourfriday.domain.scraping;

import com.toy.getyourfriday.domain.product.Products;

import java.util.Optional;

public interface WebScraper extends Runnable {

    Optional<Products> scrape();
    void quit();
}
