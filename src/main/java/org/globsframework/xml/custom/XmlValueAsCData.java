package org.globsframework.xml.custom;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.annotations.GlobCreateFromAnnotation;
import org.globsframework.core.metamodel.annotations.InitUniqueGlob;
import org.globsframework.core.metamodel.annotations.InitUniqueKey;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;

public class XmlValueAsCData {
    public static final GlobType TYPE;

    @InitUniqueKey
    public static final Key UNIQUE_KEY;

    @InitUniqueGlob
    public static final Glob UNIQUE_GLOB;

    static {
        GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("XmlAsCData");
        TYPE = typeBuilder.unCompleteType();
        typeBuilder.complete();
        UNIQUE_KEY = KeyBuilder.newEmptyKey(TYPE);
        UNIQUE_GLOB = TYPE.instantiate();
        typeBuilder.register(GlobCreateFromAnnotation.class, annotation -> UNIQUE_GLOB);

//        GlobTypeLoaderFactory.create(XmlValueAsCData.class, "_XmlAsCData")
//                .register(GlobCreateFromAnnotation.class, annotation -> UNIQUE_GLOB)
//                .load();
    }
}
