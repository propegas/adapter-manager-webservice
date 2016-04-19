
package templates;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element ref="{}properties"/>
 *         &lt;element ref="{}sources"/>
 *         &lt;element ref="{}mainConfigProperties"/>
 *         &lt;element ref="{}configFiles"/>
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
@XmlType(name = "", propOrder = {
    "properties",
    "sources",
    "mainConfigProperties",
    "configFiles"
})
@XmlRootElement(name = "template")
public class Template {

    @XmlElement(required = true)
    protected Properties properties;
    @XmlElement(required = true)
    protected Sources sources;
    @XmlElement(required = true)
    protected MainConfigProperties mainConfigProperties;
    @XmlElement(required = true)
    protected ConfigFiles configFiles;
    @XmlAttribute(name = "name")
    protected String name;

    /**
     * <status><![CDATA[/^.*$/gs]]></status>
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
     * Gets the value of the mainConfigProperties property.
     * 
     * @return
     *     possible object is
     *     {@link MainConfigProperties }
     *     
     */
    public MainConfigProperties getMainConfigProperties() {
        return mainConfigProperties;
    }

    /**
     * Sets the value of the mainConfigProperties property.
     * 
     * @param value
     *     allowed object is
     *     {@link MainConfigProperties }
     *     
     */
    public void setMainConfigProperties(MainConfigProperties value) {
        this.mainConfigProperties = value;
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
