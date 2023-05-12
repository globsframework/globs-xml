package org.globsframework.xml;

import org.globsframework.model.Glob;
import org.globsframework.xml.custom.XmlGlobBuilder;
import org.globsframework.xml.custom.XmlGlobReader;
import org.globsframework.xml.structured.DataWithMixValue;
import org.globsframework.xml.structured.RootWithValue;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class XmlWithValueTest {

    @Test
    @Ignore
    public void withValue() throws IOException {
        Glob actual = XmlGlobReader.read(kind -> RootWithValue.TYPE, new StringReader(
                "<root>" +
                        "  <dataWithValue valueAsAttr=\"attrValue\" valueAsAttrDouble=\"3.14\" valueAsAttrDateTime=\"2011-12-03T10:15:30\"" +
                        "   correctDate=\"9999-12-31T23:59:59.000Z\">value</dataWithValue> " +
                        "</root>"
        ));

        Assert.assertEquals("attrValue", actual.get(RootWithValue.dataWithValue).get(DataWithMixValue.valueAsAttr));
        Assert.assertEquals(3.14, actual.get(RootWithValue.dataWithValue).get(DataWithMixValue.valueAsAttrDouble), 0.001);
        Assert.assertEquals(ZonedDateTime.parse("2011-12-03T10:15:30+01:00[Europe/Paris]"), actual.get(RootWithValue.dataWithValue).get(DataWithMixValue.valueAsAttrDateTime));
        Assert.assertEquals(ZonedDateTime.parse("+10000-01-01T00:59:59+01:00[Europe/Paris]"), actual.get(RootWithValue.dataWithValue).get(DataWithMixValue.correctDate));
        Assert.assertEquals("value", actual.get(RootWithValue.dataWithValue).get(DataWithMixValue.valueAsValue));
        StringWriter writer = new StringWriter();
        XmlGlobBuilder.write(actual, writer);
        String date = DateTimeFormatter.ISO_DATE_TIME.format(actual.get(RootWithValue.dataWithValue).get(DataWithMixValue.correctDate));
        Assert.assertEquals("<rootWithValue><dataWithValue valueAsAttr=\"attrValue\" valueAsAttrDouble=\"3.14\" valueAsAttrDateTime=\"2011-12-03T10:15:30\"" +
                " correctDate=\""+ date + "\">value</dataWithValue></rootWithValue>", writer.toString());
    }


}
