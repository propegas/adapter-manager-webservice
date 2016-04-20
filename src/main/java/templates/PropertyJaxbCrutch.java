package templates;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by vgoryachev on 19.04.2016.
 * Package: templates.
 */
public class PropertyJaxbCrutch {
    private ConfProperty[] courses;

    @XmlElement(name = "confProperty")
    public ConfProperty[] getCourses() {
        return courses;
    }

    public void setCourses(ConfProperty[] courses) {
        this.courses = courses;
    }
}
