package camel;

/**
 * Created by vgoryachev on 03.06.2016.
 * Package: camel.
 */

public class ErrorMessage {

    private Long timestamp;
    private String message;
    private int repeatCounter;

    public ErrorMessage() {

    }

    public ErrorMessage(long timestamp, String message) {
        this.timestamp = timestamp;
        this.message = message;
        this.repeatCounter = 0;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", this.getTimestamp(), this.message);
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
