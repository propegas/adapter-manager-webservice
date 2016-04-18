
package templates;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for configFile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configFile">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="confProperties" type="{}confProperties"/>
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
@XmlType(name = "configFile", propOrder = {
    "confProperties"
})
public class ConfigFile {

    @XmlElement(required = true)
    protected ConfProperties confProperties;
    @XmlAttribute(name = "id")
    protected String id;
    @XmlAttribute(name = "configFile")
    protected String configFile;
    @XmlAttribute(name = "targetEncoding")
    protected String targetEncoding;
    @XmlAttribute(name = "description")
    protected String description;

    /**
     * Gets the value of the confProperties property.
     * 
     * @return
     *     possible object is
     *     {@link ConfProperties }
     *     
     */
    public ConfProperties getConfProperties() {
        return confProperties;
    }

    /**
     * Sets the value of the confProperties property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConfProperties }
     *     
     */
    public void setConfProperties(ConfProperties value) {
        this.confProperties = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
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

}
