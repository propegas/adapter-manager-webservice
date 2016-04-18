
package templates;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for adapterTemplate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="adapterTemplate">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="properties" type="{}properties"/>
 *         &lt;element name="sources" type="{}sources"/>
 *         &lt;element name="configFiles" type="{}configFiles"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "template", propOrder = {
    "properties",
    "sources",
    "configFiles"
})

@XmlRootElement(
        name = "template"
)

public class Template {

    @XmlElement(required = true)
    protected Properties properties;
    @XmlElement(required = true)
    protected Sources sources;
    @XmlElement(required = true)
    protected ConfigFiles configFiles;
    @XmlAttribute(name = "name")
    protected String name;

    /**
     * Gets the value of the properties property.
     * 
     * @return
     *     possible object is
     *     {@link Properties }
     *     
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Sets the value of the properties property.
     * 
     * @param value
     *     allowed object is
     *     {@link Properties }
     *     
     */
    public void setProperties(Properties value) {
        this.properties = value;
    }

    /**
     * Gets the value of the sources property.
     * 
     * @return
     *     possible object is
     *     {@link Sources }
     *     
     */
    public Sources getSources() {
        return sources;
    }

    /**
     * Sets the value of the sources property.
     * 
     * @param value
     *     allowed object is
     *     {@link Sources }
     *     
     */
    public void setSources(Sources value) {
        this.sources = value;
    }

    /**
     * Gets the value of the configFiles property.
     * 
     * @return
     *     possible object is
     *     {@link ConfigFiles }
     *     
     */
    public ConfigFiles getConfigFiles() {
        return configFiles;
    }

    /**
     * Sets the value of the configFiles property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConfigFiles }
     *     
     */
    public void setConfigFiles(ConfigFiles value) {
        this.configFiles = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

}
