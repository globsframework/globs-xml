package org.globsframework.xml.structured;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.annotations.Target;
import org.globsframework.core.metamodel.annotations.Targets;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.xml.custom.XmlAsNode;
import org.globsframework.xml.custom.XmlNode_;

import java.util.function.Supplier;

public class DummyObjects {

    public static class DummyObject {
        public static GlobType TYPE;

        @XmlNode_
        public static StringField NAME;

        @XmlNode_
        public static DoubleField DOUBLE_VALUE;

        @XmlNode_(name = "SIMPLE")
        @Target(SubDummy.class)
        public static GlobField SIMPLE_SUB;

        @XmlNode_
        @Target(SubDummy.class)
        public static GlobArrayField SIMPLE_SUB_ARRAY;

        @Targets({SubDummy.class, SubDummy2.class})
        public static GlobUnionField SIMPLE_SUB_UNION;

        static {
            GlobTypeBuilder typeBuilder =  GlobTypeBuilderFactory.create("dummyObject");
            NAME = typeBuilder.declareStringField("name", XmlAsNode.UNIQUE_INSTANCE);
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

        @XmlNode_
        public static StringField SUB_NAME;

        @XmlNode_
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
