package org.globsframework.xml.structured;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.model.Glob;
import org.globsframework.xml.custom.ValueIsXml;
import org.globsframework.xml.custom.ValueIsXml_;
import org.globsframework.xml.custom.XmlValue;

public class DataWithInnerXml {
    public static GlobType TYPE;

    public static StringField name;

    @ValueIsXml_("SUB_XML")
    public static StringField subXml;

    static {
        GlobTypeBuilder typeBuilder =   GlobTypeBuilderFactory.create("DataWithInnerXml");
        name = typeBuilder.declareStringField("name");
        subXml = typeBuilder.declareStringField("subXml", ValueIsXml.create("SUB_XML"));
        TYPE = typeBuilder.build();
    }

    public static Glob create(String name, String xml) {
        return TYPE.instantiate()
                .set(DataWithInnerXml.name, name)
                .set(DataWithInnerXml.subXml, xml);
    }
}
