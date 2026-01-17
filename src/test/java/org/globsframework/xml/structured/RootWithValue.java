package org.globsframework.xml.structured;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.annotations.Target;
import org.globsframework.core.metamodel.fields.GlobField;

public class RootWithValue {
    public static GlobType TYPE;

    @Target(DataWithMixValue.class)
    public static GlobField dataWithValue;

    static {
        GlobTypeBuilder typeBuilder =  GlobTypeBuilderFactory.create("rootWithValue");
        dataWithValue = typeBuilder.declareGlobField("dataWithValue", () -> DataWithMixValue.TYPE);
        TYPE = typeBuilder.build();
    }
}
