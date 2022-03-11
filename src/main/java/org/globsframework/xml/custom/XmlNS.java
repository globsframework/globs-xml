package org.globsframework.xml.custom;

import org.globsframework.metamodel.GlobType;
import org.globsframework.metamodel.GlobTypeLoaderFactory;
import org.globsframework.metamodel.annotations.GlobCreateFromAnnotation;
import org.globsframework.metamodel.annotations.InitUniqueKey;
import org.globsframework.metamodel.fields.BooleanField;
import org.globsframework.metamodel.fields.StringField;
import org.globsframework.model.Key;
import org.globsframework.model.MutableGlob;
import org.globsframework.utils.Strings;

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
