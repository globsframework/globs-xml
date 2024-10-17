package org.globsframework.xml.structured;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeLoaderFactory;
import org.globsframework.core.metamodel.annotations.Target;
import org.globsframework.core.metamodel.fields.GlobField;

public class RootWithValue {
    public static GlobType TYPE;

    @Target(DataWithMixValue.class)
    public static GlobField dataWithValue;

    static {
        GlobTypeLoaderFactory.create(RootWithValue.class, true).load();
    }
}
