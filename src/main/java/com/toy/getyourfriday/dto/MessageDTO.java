package com.toy.getyourfriday.dto;

public class MessageDTO {

    private UserDTO from;
    private String text;

    public UserDTO getFrom() {
        return from;
    }

    public void setFrom(UserDTO from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "MessageDTO{" +
                "from=" + from +
                ", text='" + text + '\'' +
                '}';
    }
}
