package org.globsframework.xml.custom;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;

public class XmlNS {
    public static final GlobType TYPE;

    public static final StringField name;

    public static final StringField url;

    public static final Key UNIQUE_KEY;

    static {
        GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("XmlNS");
        name = typeBuilder.declareStringField("name");
        url = typeBuilder.declareStringField("url");
        TYPE = typeBuilder.build();
        UNIQUE_KEY = KeyBuilder.newEmptyKey(TYPE);
    }

    public static Glob create(String name, String url) {
        return TYPE.instantiate()
                .set(XmlNS.name, name)
                .set(XmlNS.url, url);
    }
}
