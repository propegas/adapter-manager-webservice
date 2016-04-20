package templates;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vgoryachev on 19.04.2016.
 * Package: templates.
 */
public class MapAdapter extends XmlAdapter<PropertyJaxbCrutch, Map<String, ConfProperty>> {

    @Override
    public Map<String, ConfProperty> unmarshal(PropertyJaxbCrutch confProperties) throws Exception {
        Map<String, ConfProperty> map = new HashMap<>();
        for(ConfProperty confProperty : confProperties.getCourses()) {
            map.put(confProperty.getName(), confProperty);
        }
        return map;
    }

    @Override
    public PropertyJaxbCrutch marshal(Map<String, ConfProperty> map) throws Exception {
        List<ConfProperty> list = new ArrayList<>();
        for(Map.Entry<String, ConfProperty> mapEntry : map.entrySet()) {
            list.add(mapEntry.getValue());
        }
        PropertyJaxbCrutch propertyJaxbCrutch = new PropertyJaxbCrutch();
        propertyJaxbCrutch.setCourses(list.toArray(new ConfProperty[list.size()]));

        return propertyJaxbCrutch;
    }

}