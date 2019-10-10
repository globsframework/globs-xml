package org.globsframework.xml.structured;

import org.globsframework.model.Glob;
import org.globsframework.model.MutableGlob;
import org.globsframework.xml.XmlTestUtils;
import org.globsframework.xml.custom.XmlGlobBuilder;
import org.globsframework.xml.custom.XmlGlobReader;
import org.junit.Test;

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
                "    <simpleSub>\n" +
                "        <subName>sub1</subName>\n" +
                "        <count>2</count>\n" +
                "    </simpleSub>\n" +
                "    <simpleSubArray>\n" +
                "        <subName>\n" +
                "            sub2\n" +
                "        </subName>\n" +
                "        <count>\n" +
                "            3\n" +
                "        </count>\n" +
                "    </simpleSubArray>\n" +
                "    <simpleSubArray>\n" +
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

    private MutableGlob create(String name, int count) {
        return SubDummy.TYPE.instantiate()
                .set(SubDummy.SUB_NAME, name)
                .set(SubDummy.COUNT, count);
    }
}
