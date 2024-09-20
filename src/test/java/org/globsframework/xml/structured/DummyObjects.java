package org.globsframework.xml.structured;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeLoaderFactory;
import org.globsframework.core.metamodel.annotations.Target;
import org.globsframework.core.metamodel.fields.GlobArrayField;
import org.globsframework.core.metamodel.fields.GlobField;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.xml.custom.XmlNode_;

public class DummyObjects {

    public static class DummyObject {
        public static GlobType TYPE;

        @XmlNode_
        public static StringField NAME;

        @XmlNode_
        @Target(SubDummy.class)
        public static GlobField SIMPLE_SUB;

        @XmlNode_
        @Target(SubDummy.class)
        public static GlobArrayField SIMPLE_SUB_ARRAY;

//        public static GlobUnionField SIMPLE_SUB_ARRAY;

//        public static GlobArrayUnionField SIMPLE_SUB_ARRAY;

        static {
            GlobTypeLoaderFactory.create(DummyObject.class, true).load();
        }
    }


    public static class SubDummy {
        public static GlobType TYPE;

        @XmlNode_
        public static StringField SUB_NAME;

        @XmlNode_
        public static IntegerField COUNT;

        static {
            GlobTypeLoaderFactory.createAndLoad(SubDummy.class, true);
        }
    }
}
