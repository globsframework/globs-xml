package org.globsframework.xml.structured;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.fields.DateTimeField;
import org.globsframework.core.metamodel.fields.DoubleField;
import org.globsframework.core.metamodel.fields.StringArrayField;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.xml.custom.*;

public class DataWithMixValue {
    public static GlobType TYPE;

    public static StringField valueAsAttr;

    public static DoubleField valueAsAttrDouble;

    public static DateTimeField valueAsAttrDateTime;

    public static DateTimeField correctDate;

    public static StringArrayField array;

    public static StringField valueAsValue;

    static {
        GlobTypeBuilder typeBuilder =  GlobTypeBuilderFactory.create("dataWithMixValue");
        valueAsAttr = typeBuilder.declareStringField("valueAsAttr");
        valueAsAttrDouble = typeBuilder.declareDoubleField("valueAsAttrDouble");
        valueAsAttrDateTime = typeBuilder.declareDateTimeField("valueAsAttrDateTime",
                XmlExportDateFormat.create("yyyy-MM-dd'T'HH:mm:ss", "Europe/Paris"));
        correctDate = typeBuilder.declareDateTimeField("correctDate");
        array = typeBuilder.declareStringArrayField("array", XmlAsNode.UNIQUE_INSTANCE);
        valueAsValue = typeBuilder.declareStringField("valueAsValue", XmlValue.UNIQUE_GLOB);
        TYPE = typeBuilder.build();
    }

}
