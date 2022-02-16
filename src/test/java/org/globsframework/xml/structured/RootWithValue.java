package org.globsframework.xml.structured;

import org.globsframework.metamodel.GlobType;
import org.globsframework.metamodel.GlobTypeLoaderFactory;
import org.globsframework.metamodel.annotations.Target;
import org.globsframework.metamodel.fields.GlobField;

public class RootWithValue {
    public static GlobType TYPE;

    @Target(DataWithMixValue.class)
    public static GlobField dataWithValue;

    static {
        GlobTypeLoaderFactory.create(RootWithValue.class).load();
    }
}
