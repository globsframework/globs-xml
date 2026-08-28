package org.globsframework.xml.structured;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.xml.custom.XmlAsNode;

import java.util.function.Supplier;

public class DummyObjects {

    public static class DummyObject {
        public static final GlobType TYPE;

        public static final StringField NAME;

        public static final BooleanField valid;

        public static final DoubleField DOUBLE_VALUE;

        public static final GlobField<SubDummy> SIMPLE_SUB;

        public static final GlobArrayField<SubDummy> SIMPLE_SUB_ARRAY;

        public static final GlobUnionField SIMPLE_SUB_UNION;

        static {
            GlobTypeBuilder typeBuilder =  GlobTypeBuilderFactory.create("dummyObject");
            NAME = typeBuilder.declareStringField("name", XmlAsNode.UNIQUE_INSTANCE);
            valid = typeBuilder.declareBooleanField("valid");
            DOUBLE_VALUE = typeBuilder.declareDoubleField("doubleValue", XmlAsNode.UNIQUE_INSTANCE);
            SIMPLE_SUB = typeBuilder.declareGlobField("simpleSub", () -> SubDummy.TYPE, XmlAsNode.create("SIMPLE"));
            SIMPLE_SUB_ARRAY = typeBuilder.declareGlobArrayField("simpleSubArray", () -> SubDummy.TYPE, XmlAsNode.UNIQUE_INSTANCE);
            SIMPLE_SUB_UNION = typeBuilder.declareGlobUnionField("simpleSubUnion", new Supplier[]{() -> SubDummy.TYPE, () -> SubDummy2.TYPE});
            TYPE = typeBuilder.build();
        }
    }

    public static class SubDummy {
        public static GlobType TYPE;

        public static LongField longValue;

        public static StringField SUB_NAME;

        public static IntegerField COUNT;

        static {
            GlobTypeBuilder typeBuilder =  GlobTypeBuilderFactory.create("subDummy");
            longValue = typeBuilder.declareLongField("longValue");
            SUB_NAME = typeBuilder.declareStringField("subName", XmlAsNode.UNIQUE_INSTANCE);
            COUNT = typeBuilder.declareIntegerField("count", XmlAsNode.UNIQUE_INSTANCE);
            TYPE = typeBuilder.build();
        }
    }

    public static class SubDummy2 {
        public static GlobType TYPE;

        public static LongField name;

        static {
            GlobTypeBuilder typeBuilder =  GlobTypeBuilderFactory.create("subDummy2");
            name = typeBuilder.declareLongField("name");
            TYPE = typeBuilder.build();
        }
    }
}
