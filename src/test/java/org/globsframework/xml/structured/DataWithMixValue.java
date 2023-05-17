package org.globsframework.xml.structured;

import org.globsframework.metamodel.GlobType;
import org.globsframework.metamodel.GlobTypeLoaderFactory;
import org.globsframework.metamodel.fields.DateTimeField;
import org.globsframework.metamodel.fields.DoubleField;
import org.globsframework.metamodel.fields.StringArrayField;
import org.globsframework.metamodel.fields.StringField;
import org.globsframework.xml.custom.XmlExportDateFormat_;
import org.globsframework.xml.custom.XmlNode_;
import org.globsframework.xml.custom.XmlValue_;

public class DataWithMixValue {
    public static GlobType TYPE;

    public static StringField valueAsAttr;

    public static DoubleField valueAsAttrDouble;

    @XmlExportDateFormat_(value = "yyyy-MM-dd'T'HH:mm:ss", zoneId = "Europe/Paris")
    public static DateTimeField valueAsAttrDateTime;

    public static DateTimeField correctDate;

    @XmlNode_
    public static StringArrayField array;

    @XmlValue_
    public static StringField valueAsValue;

    static {
        GlobTypeLoaderFactory.create(DataWithMixValue.class).load();
    }

}
