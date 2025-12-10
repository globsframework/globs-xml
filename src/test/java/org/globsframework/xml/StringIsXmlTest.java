package org.globsframework.xml;

import org.globsframework.core.model.Glob;
import org.globsframework.xml.custom.XmlGlobBuilder;
import org.globsframework.xml.custom.XmlGlobReader;
import org.globsframework.xml.structured.DataWithInnerXml;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;

public class StringIsXmlTest {

    @Test
    public void testStringIsXml() throws Exception {
        final StringWriter writer = new StringWriter();
        final String xml = "<hello><a>data</a></hello>";
        XmlGlobBuilder.write(DataWithInnerXml.create("toto", xml), writer);
        Assertions.assertEquals("<DataWithInnerXml name=\"toto\"><subXml><hello><a>data</a></hello></subXml></DataWithInnerXml>", writer.toString());
        final Glob read = XmlGlobReader.read(kind -> DataWithInnerXml.TYPE, new StringReader(writer.toString()));
        Assertions.assertNotNull(read);
        Assertions.assertEquals(xml, read.get(DataWithInnerXml.subXml));
    }
}
