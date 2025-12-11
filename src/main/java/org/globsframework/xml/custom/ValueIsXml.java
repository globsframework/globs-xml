package org.globsframework.xml.custom;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.annotations.GlobCreateFromAnnotation;
import org.globsframework.core.metamodel.annotations.InitUniqueGlob;
import org.globsframework.core.metamodel.annotations.InitUniqueKey;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;
import org.globsframework.core.model.MutableGlob;
import org.globsframework.core.utils.Strings;

public class ValueIsXml {
    public static final GlobType TYPE;

    public static final StringField NAME;

    @InitUniqueKey
    public static final Key UNIQUE_KEY;

    static {
        GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("ValueIsXml");
        TYPE = typeBuilder.unCompleteType();
        NAME = typeBuilder.declareStringField("name");
        typeBuilder.complete();
        UNIQUE_KEY = KeyBuilder.newEmptyKey(TYPE);
        typeBuilder.register(GlobCreateFromAnnotation.class, annotation -> {
            final MutableGlob instantiate = TYPE.instantiate();
            if (Strings.isNotEmpty(((ValueIsXml_) annotation).value())) {
                instantiate.set(NAME, ((ValueIsXml_) annotation).value());
            }
            return instantiate;
        });
//        GlobTypeLoaderFactory.create(XmlValue.class, "_XmlValue")
//                .register(GlobCreateFromAnnotation.class, annotation -> UNIQUE_GLOB)
//                .load();
    }

}
