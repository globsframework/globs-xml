package org.globsframework.xml;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
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
        public static GlobType TYPE;

        public static StringField header;

        public static GlobField<Y2SoapBodyType> body;

        static {
            GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("Envelope");
            typeBuilder.addAnnotation(XmlNS.create("soapenv", "http://schemas.xmlsoap.org/soap/envelope/"));
            header = typeBuilder.declareStringField("Header", XmlAsNode.UNIQUE_INSTANCE);
            body = typeBuilder.declareGlobField("Body", () -> Y2SoapBodyType.TYPE);
            TYPE = typeBuilder.build();
        }
    }

    public static class Y2SoapBodyType {
        public static GlobType TYPE;

        public static GlobField<Y2GetCustomerDetailRequest> getCustomerDetailRequest;

        static {
            GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("Y2SoapBodyType");
            getCustomerDetailRequest = typeBuilder.declareGlobField("GetCustomerDetail", () -> Y2GetCustomerDetailRequest.TYPE);
            TYPE = typeBuilder.build();
        }
    }

    public static class Y2GetCustomerDetailRequest {
        public static GlobType TYPE;

        public static StringField customerId;

        public static GlobField<PriosOtherDetailRequest> priosWithParentNS;

        static {
            GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("Y2GetCustomerDetailRequest");
            typeBuilder.addAnnotation(XmlNS.create("", "http://www.cegid.fr/Retail/1.0"));
            customerId = typeBuilder.declareStringField("customerId", XmlAsNode.UNIQUE_INSTANCE);
            priosWithParentNS = typeBuilder.declareGlobField("priosWithParentNS", () -> Y2GetCustomerDetailRequest.TYPE, XmlAsNode.UNIQUE_INSTANCE,
                    XmlUseParentNS.useParentNS);
            TYPE = typeBuilder.build();
        }

    }

    public static class PriosOtherDetailRequest {
        public static GlobType TYPE;

        public static StringField customerId;

        static {
            GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("PriosOtherDetailRequest");
            typeBuilder.addAnnotation(XmlNS.create("prios", "http://www.prios.fr"));
            customerId = typeBuilder.declareStringField("customerId", XmlAsNode.UNIQUE_INSTANCE);
            TYPE = typeBuilder.build();
        }

    }

}
