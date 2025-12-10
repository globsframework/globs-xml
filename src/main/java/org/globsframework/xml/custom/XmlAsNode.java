package org.globsframework.xml.custom;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.annotations.GlobCreateFromAnnotation;
import org.globsframework.core.metamodel.fields.BooleanField;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;
import org.globsframework.core.model.MutableGlob;
import org.globsframework.core.utils.Strings;

public class XmlAsNode {
    public static final GlobType TYPE;

    public static final StringField NAME;

    public static final BooleanField MANDATORY;

    public static final Key UNIQUE_KEY;

    public static final Glob UNIQUE_INSTANCE;

    public static Glob create(String name, boolean mandatory) {
        MutableGlob glob = TYPE.instantiate();
        glob.set(NAME, name);
        glob.set(MANDATORY, mandatory);
        return glob;
    }

    public static Glob create(String name) {
        MutableGlob glob = TYPE.instantiate();
        glob.set(NAME, name);
        return glob;
    }

    static {
        GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("XmlAsNode");
        TYPE = typeBuilder.unCompleteType();
        NAME = typeBuilder.declareStringField("name");
        MANDATORY = typeBuilder.declareBooleanField("mandatory");
        typeBuilder.complete();
        typeBuilder.register(GlobCreateFromAnnotation.class, annotation -> create((XmlNode_) annotation));
        UNIQUE_KEY = KeyBuilder.newEmptyKey(TYPE);
        UNIQUE_INSTANCE = TYPE.instantiate();
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
