package org.globsframework.xml;

import org.globsframework.metamodel.GlobType;
import org.globsframework.metamodel.GlobTypeLoaderFactory;
import org.globsframework.metamodel.annotations.FieldNameAnnotation;
import org.globsframework.metamodel.annotations.Target;
import org.globsframework.metamodel.fields.GlobField;
import org.globsframework.metamodel.fields.StringField;
import org.globsframework.model.Glob;
import org.globsframework.xml.custom.XmlGlobBuilder;
import org.globsframework.xml.custom.XmlGlobReader;
import org.globsframework.xml.custom.XmlNS_;
import org.globsframework.xml.custom.XmlNode_;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class NamespaceTest {

    @Test
    public void name() throws IOException {
        String text2 = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://www.cegid.fr/Retail/1.0\">\n" +
                "   <soapenv:Body>\n" +
                "      <ns:GetCustomerDetail>\n" +
                "         <ns:customerId>001000000018</ns:customerId>\n" +
                "      </ns:GetCustomerDetail>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        String text3 = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" >\n" +
                "   <soapenv:Body>\n" +
                "      <GetCustomerDetail xmlns=\"http://www.cegid.fr/Retail/1.0\">\n" +
                "         <customerId>001000000018</customerId>\n" +
                "      </GetCustomerDetail>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        Glob glob1 = XmlGlobReader.read(kind -> Y2EnvelopeType.TYPE, new StringReader(text2));
        Glob glob2 = XmlGlobReader.read(kind -> Y2EnvelopeType.TYPE, new StringReader(text3));

        Assert.assertTrue(glob1.matches(glob2));

        StringWriter writer = new StringWriter();
        XmlGlobBuilder.write(glob1, writer);
        XmlTestUtils.assertEquivalent(text3, writer.toString());


        XmlGlobBuilder xmlGlobBuilder = new XmlGlobBuilder();
        xmlGlobBuilder.withNS("ns", "http://www.cegid.fr/Retail/1.0");
        writer = new StringWriter();
        XmlGlobBuilder.write(glob1, writer);
        XmlTestUtils.assertEquivalent(text2, writer.toString());
    }

    public static class Y2EnvelopeType {
        @XmlNS_(url = "http://schemas.xmlsoap.org/soap/envelope/", name = "soapenv")
        public static GlobType TYPE;

        @XmlNode_(mandatory = true)
        @FieldNameAnnotation("Header")
        public static StringField header;

        @FieldNameAnnotation("Body")
        @Target(Y2SoapBodyType.class)
        public static GlobField body;

        static {
            GlobTypeLoaderFactory.create(Y2EnvelopeType.class, "Envelope").load();
        }
    }

    public static class Y2SoapBodyType {
        public static GlobType TYPE;

        @FieldNameAnnotation("GetCustomerDetail")
        @Target(Y2GetCustomerDetailRequest.class)
        public static GlobField getCustomerDetailRequest;

        static {
            GlobTypeLoaderFactory.create(Y2SoapBodyType.class).load();
        }
    }

    public static class Y2GetCustomerDetailRequest {
        @XmlNS_(url = "http://www.cegid.fr/Retail/1.0")
        public static GlobType TYPE;

        @XmlNode_
        @FieldNameAnnotation("customerId")
        public static StringField customerId;

        static {
            GlobTypeLoaderFactory.create(Y2GetCustomerDetailRequest.class).load();
        }

    }

}
