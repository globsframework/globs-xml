package org.globsframework.xml.custom;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.annotations.GlobCreateFromAnnotation;
import org.globsframework.core.metamodel.annotations.InitUniqueKey;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;
import org.globsframework.core.model.MutableGlob;

public class XmlExportDateFormat {
    public static final GlobType TYPE;

    public static final StringField FORMAT;

    public static final StringField ZONE_ID;

    @InitUniqueKey
    public static final Key UNIQUE_KEY;

    static {
        GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("XmlExportDateFormat");
        FORMAT = typeBuilder.declareStringField("format");
        ZONE_ID = typeBuilder.declareStringField("zoneId");
        typeBuilder.register(GlobCreateFromAnnotation.class, annotation -> createAnnotation((XmlExportDateFormat_) annotation));
        TYPE = typeBuilder.build();
        UNIQUE_KEY = KeyBuilder.newEmptyKey(TYPE);
    }

    private static MutableGlob createAnnotation(XmlExportDateFormat_ annotation) {
        return TYPE.instantiate()
                .set(FORMAT, annotation.value())
                .set(ZONE_ID, annotation.zoneId());
    }

    public static Glob create(String format, String zone) {
        return TYPE.instantiate()
                .set(FORMAT, format)
                .set(ZONE_ID, zone);
    }
}
