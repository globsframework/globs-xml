package org.globsframework.xml.structured;

import org.globsframework.metamodel.GlobType;
import org.globsframework.metamodel.GlobTypeLoaderFactory;
import org.globsframework.metamodel.fields.StringField;
import org.globsframework.xml.custom.XmlValue_;

public class DataWithMixValue {
    public static GlobType TYPE;

    public static StringField valueAsAttr;

    @XmlValue_
    public static StringField valueAsValue;

    static {
        GlobTypeLoaderFactory.create(DataWithMixValue.class).load();
    }

}
