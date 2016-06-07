package camel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vgoryachev on 06.06.2016.
 * Package: camel.
 */

public class AdapterErrors {

    private List<ErrorMessage> eventMessagesList = new ArrayList<>();

    public List<ErrorMessage> getEventMessagesList() {
        return eventMessagesList;
    }

    public void setEventMessagesList(List<ErrorMessage> eventMessagesList) {
        this.eventMessagesList = eventMessagesList;
    }
}
