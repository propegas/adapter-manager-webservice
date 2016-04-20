
package templates;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *       &lt;sequence>
 *         &lt;element ref="{}confProperties"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="configFile" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="targetEncoding" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="description" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "confProperties"
})
@XmlRootElement(name = "configFile")
public class ConfigFile {

    //@XmlElement(required = true)
    //protected ConfProperties confProperties;

    @XmlJavaTypeAdapter(MapAdapter.class)
    private Map<String, ConfProperty> confProperties = new HashMap<>();
    @XmlAttribute(name = "id")
    protected String id;
    @XmlAttribute(name = "configFile")
    protected String configFile;
    @XmlAttribute(name = "targetEncoding")
    protected String targetEncoding;
    @XmlAttribute(name = "description")
    protected String description;

    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the configFile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConfigFile() {
        return configFile;
    }

    /**
     * Sets the value of the configFile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConfigFile(String value) {
        this.configFile = value;
    }

    /**
     * Gets the value of the targetEncoding property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetEncoding() {
        return targetEncoding;
    }

    /**
     * Sets the value of the targetEncoding property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetEncoding(String value) {
        this.targetEncoding = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }


    public Map<String, ConfProperty> getConfProperties() {
        return confProperties;
    }

    public void setConfProperties(Map<String, ConfProperty> confProperties) {
        this.confProperties = confProperties;
    }
}
