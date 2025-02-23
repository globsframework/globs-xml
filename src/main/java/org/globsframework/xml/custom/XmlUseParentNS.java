package org.globsframework.xml.custom;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.annotations.GlobCreateFromAnnotation;
import org.globsframework.core.metamodel.annotations.InitUniqueKey;
import org.globsframework.core.metamodel.fields.BooleanField;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;
import org.globsframework.core.model.MutableGlob;

public class XmlUseParentNS {
    public static final GlobType TYPE;

    public static final BooleanField useParent;

    @InitUniqueKey
    public static final Key UNIQUE_KEY;

    static {
        GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("XmlUseParentNS");
        TYPE = typeBuilder.unCompleteType();
        useParent = typeBuilder.declareBooleanField("useParent");
        typeBuilder.complete();
        UNIQUE_KEY = KeyBuilder.newEmptyKey(TYPE);
        typeBuilder.register(GlobCreateFromAnnotation.class, annotation -> create((XmlUseParentNS_) annotation));

//        GlobTypeLoaderFactory.create(XmlUseParentNS.class, "XmlUseParentNS")
//                .register(GlobCreateFromAnnotation.class, annotation -> create((XmlUseParentNS_) annotation))
//                .load();
    }

    private static MutableGlob create(XmlUseParentNS_ annotation) {
        MutableGlob instantiate = TYPE.instantiate();
        instantiate.set(useParent, annotation.useParent());
        return instantiate;
    }
}
