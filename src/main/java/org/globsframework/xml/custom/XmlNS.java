package org.globsframework.xml.custom;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.annotations.GlobCreateFromAnnotation;
import org.globsframework.core.metamodel.annotations.InitUniqueKey;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;
import org.globsframework.core.model.MutableGlob;
import org.globsframework.core.utils.Strings;

public class XmlNS {
    public static final GlobType TYPE;

    public static final StringField name;

    public static final StringField url;

    @InitUniqueKey
    public static final Key UNIQUE_KEY;

    static {
        GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("XmlNS");
        TYPE = typeBuilder.unCompleteType();
        name = typeBuilder.declareStringField("name");
        url = typeBuilder.declareStringField("url");
        typeBuilder.complete();
        UNIQUE_KEY = KeyBuilder.newEmptyKey(TYPE);
        typeBuilder.register(GlobCreateFromAnnotation.class, annotation -> create((XmlNS_) annotation));
//        GlobTypeLoaderFactory.create(XmlNS.class, "XmlNS")
//                .register(GlobCreateFromAnnotation.class, annotation -> create((XmlNS_) annotation))
//                .load();
    }

    private static MutableGlob create(XmlNS_ annotation) {
        MutableGlob instantiate = TYPE.instantiate();
        if (Strings.isNotEmpty(annotation.name())) {
            instantiate.set(name, annotation.name());
        }
        if (Strings.isNotEmpty(annotation.url())) {
            instantiate.set(url, annotation.url());
        }
        return instantiate;
    }
}
