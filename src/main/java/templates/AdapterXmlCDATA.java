package templates;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Created by vgoryachev on 20.04.2016.
 * Package: templates.
 */
public class AdapterXmlCDATA extends XmlAdapter<String, String>  {
    @Override
    public String marshal(String value) throws Exception {
        return "<![CDATA[" + value + "]]>";
    }
    @Override
    public String unmarshal(String value) throws Exception {
        return value;
    }
}
