package com.toy.getyourfriday.domain;

import java.util.Objects;

public class ModelUrl {

    private final String url;

    public ModelUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelUrl modelUrl = (ModelUrl) o;
        return url.equals(modelUrl.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
