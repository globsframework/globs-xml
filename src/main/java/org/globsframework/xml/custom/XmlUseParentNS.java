package org.globsframework.xml.custom;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeLoaderFactory;
import org.globsframework.core.metamodel.annotations.GlobCreateFromAnnotation;
import org.globsframework.core.metamodel.annotations.InitUniqueKey;
import org.globsframework.core.metamodel.fields.BooleanField;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.MutableGlob;

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
