package org.globsframework.xml;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.annotations.FieldName_;
import org.globsframework.core.metamodel.annotations.Target;
import org.globsframework.core.metamodel.fields.GlobField;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.model.Glob;
import org.globsframework.xml.custom.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class NamespaceTest {

    @Test
    public void name() throws IOException {
        String text2 = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://www.cegid.fr/Retail/1.0\">\n" +
                "   <soapenv:Body>\n" +
                "      <ns:GetCustomerDetail>\n" +
                "         <ns:customerId>001000000018</ns:customerId>\n" +
                "  <ns:priosWithParentNS xmlns:prios=\"http://www.cegid.fr/Retail/1.0\">" +
                "         <prios:customerId>001000000018</prios:customerId>\n" +
                "    </ns:priosWithParentNS>" +
                "      </ns:GetCustomerDetail>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        String text3 = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" >\n" +
                "   <soapenv:Body>\n" +
                "      <GetCustomerDetail xmlns=\"http://www.cegid.fr/Retail/1.0\">\n" +
                "         <customerId>001000000018</customerId>\n" +
                "     <priosWithParentNS xmlns:prios=\"http://www.cegid.fr/Retail/1.0\">" +
                "         <prios:customerId>001000000018</prios:customerId>\n" +
                "    </priosWithParentNS>" +
                "      </GetCustomerDetail>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        Glob glob1 = XmlGlobReader.read(kind -> Y2EnvelopeType.TYPE, new StringReader(text2));
        Glob glob2 = XmlGlobReader.read(kind -> Y2EnvelopeType.TYPE, new StringReader(text3));

        assertTrue(glob1.matches(glob2));

        StringWriter writer = new StringWriter();
        XmlGlobBuilder.write(glob1, writer);
        XmlTestUtils.assertEquivalent(text3, writer.toString());


        XmlGlobBuilder xmlGlobBuilder = new XmlGlobBuilder();
        xmlGlobBuilder.withNS("ns", "http://www.cegid.fr/Retail/1.0");
        writer = new StringWriter();
        XmlGlobBuilder.write(glob1, writer);
        XmlTestUtils.assertEquivalent(text2, writer.toString());
        System.out.println("NamespaceTest.name " + writer.toString());
    }

    public static class Y2EnvelopeType {
        @XmlNS_(url = "http://schemas.xmlsoap.org/soap/envelope/", name = "soapenv")
        public static GlobType TYPE;

        @XmlNode_(mandatory = true)
        @FieldName_("Header")
        public static StringField header;

        @FieldName_("Body")
        @Target(Y2SoapBodyType.class)
        public static GlobField body;

        static {
            GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("Envelope");
            header = typeBuilder.declareStringField("Header", XmlAsNode.UNIQUE_INSTANCE);
            body = typeBuilder.declareGlobField("Body", () -> Y2SoapBodyType.TYPE);
            TYPE = typeBuilder.build();
        }
    }

    public static class Y2SoapBodyType {
        public static GlobType TYPE;

        @FieldName_("GetCustomerDetail")
        @Target(Y2GetCustomerDetailRequest.class)
        public static GlobField getCustomerDetailRequest;

        static {
            GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("Y2SoapBodyType");
            getCustomerDetailRequest = typeBuilder.declareGlobField("GetCustomerDetail", () -> Y2GetCustomerDetailRequest.TYPE);
            TYPE = typeBuilder.build();
        }
    }

    public static class Y2GetCustomerDetailRequest {
        @XmlNS_(url = "http://www.cegid.fr/Retail/1.0")
        public static GlobType TYPE;

        @XmlNode_
        @FieldName_("customerId")
        public static StringField customerId;

        @Target(PriosOtherDetailRequest.class)
        @XmlNode_
        @XmlUseParentNS_
        public static GlobField priosWithParentNS;

        static {
            GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("Y2GetCustomerDetailRequest");
            customerId = typeBuilder.declareStringField("customerId", XmlAsNode.UNIQUE_INSTANCE);
            priosWithParentNS = typeBuilder.declareGlobField("priosWithParentNS", () -> Y2GetCustomerDetailRequest.TYPE, XmlAsNode.UNIQUE_INSTANCE,
                    XmlUseParentNS.useParentNS);
            TYPE = typeBuilder.build();
        }

    }

    public static class PriosOtherDetailRequest {
        @XmlNS_(url = "http://www.prios.fr", name = "prios")
        public static GlobType TYPE;

        @XmlNode_
        @FieldName_("customerId")
        public static StringField customerId;

        static {
            GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("PriosOtherDetailRequest");
            typeBuilder.addAnnotation(XmlNS.create("prios", "http://www.prios.fr"));
        }

    }

}
