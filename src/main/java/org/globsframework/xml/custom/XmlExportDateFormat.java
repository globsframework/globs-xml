package org.globsframework.xml.custom;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.annotations.GlobCreateFromAnnotation;
import org.globsframework.core.metamodel.annotations.InitUniqueKey;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;

public class XmlExportDateFormat {
    public static final GlobType TYPE;

    public static final StringField FORMAT;

    public static final StringField ZONE_ID;

    @InitUniqueKey
    public static final Key UNIQUE_KEY;

    static {
        GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("XmlExportDateFormat");
        TYPE = typeBuilder.unCompleteType();
        FORMAT = typeBuilder.declareStringField("format");
        ZONE_ID = typeBuilder.declareStringField("zoneId");
        typeBuilder.complete();
        UNIQUE_KEY = KeyBuilder.newEmptyKey(TYPE);
        typeBuilder.register(GlobCreateFromAnnotation.class, annotation -> TYPE.instantiate()
                .set(FORMAT, ((XmlExportDateFormat_) annotation).value())
                .set(ZONE_ID, ((XmlExportDateFormat_) annotation).zoneId()));

//        GlobTypeLoaderFactory.create(XmlExportDateFormat.class, "XmlExportDateFormat")
//                .register(GlobCreateFromAnnotation.class, annotation -> TYPE.instantiate()
//                        .set(FORMAT, ((XmlExportDateFormat_) annotation).value())
//                        .set(ZONE_ID, ((XmlExportDateFormat_) annotation).zoneId())
//                )
//                .load();
    }

}
