package org.globsframework.xml;

import org.globsframework.model.Glob;
import org.globsframework.xml.custom.XmlGlobBuilder;
import org.globsframework.xml.custom.XmlGlobReader;
import org.globsframework.xml.structured.DataWithMixValue;
import org.globsframework.xml.structured.RootWithValue;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.ZonedDateTime;

public class XmlWithValueTest {

    @Test
    public void withValue() throws IOException {
        Glob actual = XmlGlobReader.read(kind -> RootWithValue.TYPE, new StringReader(
                "<root>" +
                        "  <dataWithValue valueAsAttr=\"attrValue\" valueAsAttrDouble=\"3.14\" valueAsAttrDateTime=\"2011-12-03T10:15:30+01:00\">value</dataWithValue> " +
                        "</root>"
        ));

        Assert.assertEquals("attrValue", actual.get(RootWithValue.dataWithValue).get(DataWithMixValue.valueAsAttr));
        Assert.assertEquals(3.14, actual.get(RootWithValue.dataWithValue).get(DataWithMixValue.valueAsAttrDouble), 0.001);
        Assert.assertEquals(ZonedDateTime.parse("2011-12-03T10:15:30+01:00[Europe/Paris]"), actual.get(RootWithValue.dataWithValue).get(DataWithMixValue.valueAsAttrDateTime));
        Assert.assertEquals("value", actual.get(RootWithValue.dataWithValue).get(DataWithMixValue.valueAsValue));
        StringWriter writer = new StringWriter();
        XmlGlobBuilder.write(actual, writer);
        Assert.assertEquals("<rootWithValue><dataWithValue valueAsAttr=\"attrValue\" valueAsAttrDouble=\"3.14\" valueAsAttrDateTime=\"2011-12-03T10:15:30+01:00[Europe/Paris]\">value</dataWithValue></rootWithValue>", writer.toString());
    }


}
