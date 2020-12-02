package com.toy.getyourfriday.dto;

public class UpdateDTO {

    private Integer updateId;
    private MessageDTO message;

    public Integer getUpdateId() {
        return updateId;
    }

    public void setUpdateId(Integer updateId) {
        this.updateId = updateId;
    }

    public MessageDTO getMessage() {
        return message;
    }

    public void setMessage(MessageDTO message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "UpdateDTO{" +
                "updateId=" + updateId +
                ", message=" + message +
                '}';
    }
}
