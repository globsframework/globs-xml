package org.globsframework.xml.structured;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeLoaderFactory;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.model.Glob;
import org.globsframework.xml.custom.ValueIsXml_;

public class DataWithInnerXml {
    public static GlobType TYPE;

    public static StringField name;

    @ValueIsXml_("SUB_XML")
    public static StringField subXml;

    static {
        GlobTypeLoaderFactory.create(DataWithInnerXml.class).load();
    }

    public static Glob create(String name, String xml) {
        return TYPE.instantiate()
                .set(DataWithInnerXml.name, name)
                .set(DataWithInnerXml.subXml, xml);
    }
}
