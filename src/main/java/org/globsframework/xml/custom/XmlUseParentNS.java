package org.globsframework.xml.custom;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.fields.BooleanField;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;

public class XmlUseParentNS {
    public static final GlobType TYPE;

    public static final BooleanField useParent;

    public static final Key UNIQUE_KEY;

    public static final Glob useParentNS;

    public static final Glob doNotUseParentNS;

    static {
        GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("XmlUseParentNS");
        useParent = typeBuilder.declareBooleanField("useParent");
        TYPE = typeBuilder.build();
        UNIQUE_KEY = KeyBuilder.newEmptyKey(TYPE);
        useParentNS = TYPE.instantiate().set(useParent, true);
        doNotUseParentNS = TYPE.instantiate().set(useParent, false);
    }
}
