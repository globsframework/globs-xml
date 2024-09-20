package org.globsframework.xml.custom;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeLoaderFactory;
import org.globsframework.core.metamodel.annotations.GlobCreateFromAnnotation;
import org.globsframework.core.metamodel.annotations.InitUniqueKey;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.MutableGlob;
import org.globsframework.core.utils.Strings;

public class XmlNS {
    public static GlobType TYPE;

    public static StringField name;

    public static StringField url;

    @InitUniqueKey
    public static Key UNIQUE_KEY;

    static {
        GlobTypeLoaderFactory.create(XmlNS.class, "XmlNS")
                .register(GlobCreateFromAnnotation.class, annotation -> create((XmlNS_) annotation))
                .load();
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
