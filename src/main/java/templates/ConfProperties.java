
package templates;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element ref="{}confProperty"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "confProperty"
})
@XmlRootElement(name = "confProperties")
public class ConfProperties {

    //private String name;

    @XmlJavaTypeAdapter(MapAdapter.class)
    private Map<String, ConfProperty> confProperty = new HashMap<>();

    public Map<String, ConfProperty> getConfProperty() {
        return confProperty;
    }

    public void setConfProperty(Map<String, ConfProperty> confProperty) {
        this.confProperty = confProperty;
    }

    /**
     * Gets the value of the confProperty property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the confProperty property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConfProperty().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConfProperty }
     * 
     * 
     */





}
