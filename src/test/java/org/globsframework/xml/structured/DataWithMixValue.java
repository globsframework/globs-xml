package org.globsframework.xml.structured;

import org.globsframework.metamodel.GlobType;
import org.globsframework.metamodel.GlobTypeLoaderFactory;
import org.globsframework.metamodel.fields.DateTimeField;
import org.globsframework.metamodel.fields.DoubleField;
import org.globsframework.metamodel.fields.StringField;
import org.globsframework.xml.custom.XmlValue_;

import java.time.format.DateTimeFormatter;

public class DataWithMixValue {
    public static GlobType TYPE;

    public static StringField valueAsAttr;

    public static DoubleField valueAsAttrDouble;

    public static DateTimeField valueAsAttrDateTime;

    @XmlValue_
    public static StringField valueAsValue;

    static {
        GlobTypeLoaderFactory.create(DataWithMixValue.class).load();
    }

}
