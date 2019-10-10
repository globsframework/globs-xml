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

public class _XmlAsNode {
    public static GlobType TYPE;

    public static StringField NAME;

    public static BooleanField MANDATORY;

    @InitUniqueKey
    public static Key UNIQUE_KEY;
    static {
        GlobTypeLoaderFactory.create(_XmlAsNode.class, "_XmlAsNode")
        .register(GlobCreateFromAnnotation.class, annotation -> create((XmlNode_) annotation))
        .load();
    }

    private static MutableGlob create(XmlNode_ annotation) {
        MutableGlob instantiate = TYPE.instantiate();
        if (Strings.isNotEmpty(annotation.name())) {
            instantiate.set(NAME, annotation.name());
        }
        instantiate.set(MANDATORY, annotation.mandatory());
        return instantiate;
    }
}
