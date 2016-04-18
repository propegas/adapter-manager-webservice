
package templates;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the templates package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _AdapterTemplate_QNAME = new QName("", "adapterTemplate");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: templates
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Template }
     * 
     */
    public Template createAdapterTemplate() {
        return new Template();
    }

    /**
     * Create an instance of {@link Configs }
     * 
     */
    public Configs createConfigs() {
        return new Configs();
    }

    /**
     * Create an instance of {@link ConfigFile }
     * 
     */
    public ConfigFile createConfigFile() {
        return new ConfigFile();
    }

    /**
     * Create an instance of {@link Source }
     * 
     */
    public Source createSource() {
        return new Source();
    }

    /**
     * Create an instance of {@link ConfProperty }
     * 
     */
    public ConfProperty createConfProperty() {
        return new ConfProperty();
    }

    /**
     * Create an instance of {@link ConfProperties }
     * 
     */
    public ConfProperties createConfProperties() {
        return new ConfProperties();
    }

    /**
     * Create an instance of {@link ConfigFiles }
     * 
     */
    public ConfigFiles createConfigFiles() {
        return new ConfigFiles();
    }

    /**
     * Create an instance of {@link Config }
     * 
     */
    public Config createConfig() {
        return new Config();
    }

    /**
     * Create an instance of {@link Property }
     * 
     */
    public Property createProperty() {
        return new Property();
    }

    /**
     * Create an instance of {@link Properties }
     * 
     */
    public Properties createProperties() {
        return new Properties();
    }

    /**
     * Create an instance of {@link Sources }
     * 
     */
    public Sources createSources() {
        return new Sources();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Template }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "adapterTemplate")
    public JAXBElement<Template> createAdapterTemplate(Template value) {
        return new JAXBElement<Template>(_AdapterTemplate_QNAME, Template.class, null, value);
    }

}
