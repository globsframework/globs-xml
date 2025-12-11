package org.globsframework.xml.structured;

import org.globsframework.core.model.Glob;
import org.globsframework.core.model.MutableGlob;
import org.globsframework.xml.XmlTestUtils;
import org.globsframework.xml.custom.XmlGlobBuilder;
import org.globsframework.xml.custom.XmlGlobReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static org.globsframework.xml.structured.DummyObjects.DummyObject;
import static org.globsframework.xml.structured.DummyObjects.SubDummy;


public class ReadWriteTest {

    @Test
    public void writer() throws IOException {
        MutableGlob data = DummyObject.TYPE.instantiate().set(DummyObject.NAME, "dummy 1")
                .set(DummyObject.SIMPLE_SUB, create("sub1", 2))
                .set(DummyObject.SIMPLE_SUB_ARRAY, new Glob[]{create("sub2", 3), create("sub 4", 4)});

        StringWriter stringWriter = new StringWriter();
        XmlGlobBuilder.write(data, stringWriter);
        XmlTestUtils.assertEquivalent("<dummyObject>\n" +
                "    <name>dummy 1</name>\n" +
                "    <SIMPLE longValue=\"2\">\n" +
                "        <subName>sub1</subName>\n" +
                "        <count>2</count>\n" +
                "    </SIMPLE>\n" +
                "    <simpleSubArray longValue=\"3\">\n" +
                "        <subName>\n" +
                "            sub2\n" +
                "        </subName>\n" +
                "        <count>\n" +
                "            3\n" +
                "        </count>\n" +
                "    </simpleSubArray>\n" +
                "    <simpleSubArray longValue=\"4\">\n" +
                "        <subName>\n" +
                "            sub 4\n" +
                "        </subName>\n" +
                "        <count>\n" +
                "            4\n" +
                "        </count>\n" +
                "    </simpleSubArray>\n" +
                "</dummyObject>", stringWriter.toString());

        Glob glob = XmlGlobReader.read(kind -> DummyObject.TYPE, new StringReader(stringWriter.toString()));
        StringWriter newStr = new StringWriter();
        XmlGlobBuilder.write(glob, newStr);
        XmlTestUtils.assertEquivalent(stringWriter.toString(), newStr.toString());
    }

    @Test
    void writeXmlNode() throws IOException {
        MutableGlob data = DummyObject.TYPE.instantiate().set(DummyObject.NAME, "dummy 1")
                .set(DummyObject.SIMPLE_SUB, create("sub1", 2))
                .set(DummyObject.SIMPLE_SUB_ARRAY, new Glob[]{create("sub2", 3), create("sub 4", 4)});

        StringWriter stringWriter = new StringWriter();
        XmlGlobBuilder.write(data, stringWriter, true);
        XmlTestUtils.assertEquivalent("<dummyObject>\n" +
                                      "    <name>dummy 1</name>\n" +
                                      "    <SIMPLE>\n" +
                                      "        <longValue>2</longValue>\n" +
                                      "        <subName>sub1</subName>\n" +
                                      "        <count>2</count>\n" +
                                      "    </SIMPLE>\n" +
                                      "    <simpleSubArray>\n" +
                                      "        <longValue>3</longValue>\n" +
                                      "        <subName>\n" +
                                      "            sub2\n" +
                                      "        </subName>\n" +
                                      "        <count>\n" +
                                      "            3\n" +
                                      "        </count>\n" +
                                      "    </simpleSubArray>\n" +
                                      "    <simpleSubArray>\n" +
                                      "        <longValue>4</longValue>\n" +
                                      "        <subName>\n" +
                                      "            sub 4\n" +
                                      "        </subName>\n" +
                                      "        <count>\n" +
                                      "            4\n" +
                                      "        </count>\n" +
                                      "    </simpleSubArray>\n" +
                                      "</dummyObject>", stringWriter.toString());

        Glob glob = XmlGlobReader.read(kind -> DummyObject.TYPE, new StringReader(stringWriter.toString()), true);
        StringWriter newStr = new StringWriter();
        XmlGlobBuilder.write(glob, newStr, true);
        XmlTestUtils.assertEquivalent(stringWriter.toString(), newStr.toString());

    }

    private MutableGlob create(String name, int count) {
        return SubDummy.TYPE.instantiate()
                .set(SubDummy.SUB_NAME, name)
                .set(SubDummy.COUNT, count)
                .set(SubDummy.longValue, count);
    }
}
