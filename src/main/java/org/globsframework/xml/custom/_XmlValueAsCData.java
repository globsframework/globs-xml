package org.globsframework.xml.custom;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeLoaderFactory;
import org.globsframework.core.metamodel.annotations.GlobCreateFromAnnotation;
import org.globsframework.core.metamodel.annotations.InitUniqueGlob;
import org.globsframework.core.metamodel.annotations.InitUniqueKey;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;

public class _XmlValueAsCData {
    public static GlobType TYPE;

    @InitUniqueKey
    public static Key UNIQUE_KEY;

    @InitUniqueGlob
    public static Glob UNIQUE_GLOB;

    static {
        GlobTypeLoaderFactory.create(_XmlValueAsCData.class, "_XmlAsCData")
                .register(GlobCreateFromAnnotation.class, annotation -> UNIQUE_GLOB)
                .load();
    }
}
