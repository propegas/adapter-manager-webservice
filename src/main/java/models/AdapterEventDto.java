package models;

import javax.validation.constraints.NotNull;

public class AdapterEventDto {

    @NotNull
    public Long adapterId;

    @NotNull
    private Long timestamp;

    @NotNull
    private String message;

    @NotNull
    private int repeatCounter;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getRepeatCounter() {
        return repeatCounter;
    }

    public void setRepeatCounter(int repeatCounter) {
        this.repeatCounter = repeatCounter;
    }
}
