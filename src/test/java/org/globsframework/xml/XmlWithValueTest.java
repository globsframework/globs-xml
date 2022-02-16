package org.globsframework.xml;

import org.globsframework.model.Glob;
import org.globsframework.model.repository.DefaultGlobRepository;
import org.globsframework.xml.custom.XmlGlobBuilder;
import org.globsframework.xml.custom.XmlGlobReader;
import org.globsframework.xml.structured.DataWithMixValue;
import org.globsframework.xml.structured.RootWithValue;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

public class XmlWithValueTest {

    @Test
    public void withValue() throws IOException {
        Glob actual = XmlGlobReader.read(kind -> RootWithValue.TYPE, new StringReader(
                "<root>" +
                        "  <dataWithValue valueAsAttr=\"attrValue\">value</dataWithValue> " +
                        "</root>"
        ));

        Assert.assertEquals("attrValue", actual.get(RootWithValue.dataWithValue).get(DataWithMixValue.valueAsAttr));
        Assert.assertEquals("value", actual.get(RootWithValue.dataWithValue).get(DataWithMixValue.valueAsValue));
        StringWriter writer = new StringWriter();
        XmlGlobBuilder.write(actual, writer);
        Assert.assertEquals("<rootWithValue><dataWithValue valueAsAttr=\"attrValue\">value</dataWithValue></rootWithValue>", writer.toString());

    }


}
