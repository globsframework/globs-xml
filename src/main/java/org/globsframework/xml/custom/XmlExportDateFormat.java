package org.globsframework.xml.custom;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeLoaderFactory;
import org.globsframework.core.metamodel.annotations.GlobCreateFromAnnotation;
import org.globsframework.core.metamodel.annotations.InitUniqueKey;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.model.Key;

public class XmlExportDateFormat {
    public static GlobType TYPE;

    public static StringField FORMAT;

    public static StringField ZONE_ID;

    @InitUniqueKey
    public static Key UNIQUE_KEY;

    static {
        GlobTypeLoaderFactory.create(XmlExportDateFormat.class, "XmlExportDateFormat")
                .register(GlobCreateFromAnnotation.class, annotation -> TYPE.instantiate()
                        .set(FORMAT, ((XmlExportDateFormat_) annotation).value())
                        .set(ZONE_ID, ((XmlExportDateFormat_) annotation).zoneId())
                )
                .load();
    }

}
