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

public class XmlUseParentNS {
    public static GlobType TYPE;

    public static BooleanField useParent;

    @InitUniqueKey
    public static Key UNIQUE_KEY;

    static {
        GlobTypeLoaderFactory.create(XmlUseParentNS.class, "XmlUseParentNS")
        .register(GlobCreateFromAnnotation.class, annotation -> create((XmlUseParentNS_) annotation))
        .load();
    }

    private static MutableGlob create(XmlUseParentNS_ annotation) {
        MutableGlob instantiate = TYPE.instantiate();
        instantiate.set(useParent, annotation.useParent());
        return instantiate;
    }
}
