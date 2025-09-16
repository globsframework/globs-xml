package org.globsframework.xml;

import org.globsframework.core.metamodel.DummyModel;
import org.globsframework.core.metamodel.DummyObject;
import org.globsframework.core.model.*;
import org.globsframework.core.utils.TestUtils;
import org.globsframework.core.utils.exceptions.InvalidParameter;
import org.globsframework.core.utils.exceptions.ItemNotFound;
import org.globsframework.core.xml.XmlChangeSetParser;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;


public class XmlChangeSetParserTest {
    @Test
    public void testStandardCase() throws Exception {
        ChangeSet changeSet = org.globsframework.core.xml.XmlChangeSetParser.parse(DummyModel.get(), new StringReader(
                "<changes>"
                        + "  <create type='dummyObject' id='1' name='name1' value='2.0' present='true'/>"
                        + "  <update type='dummyObject' id='2' name='newName' _name='previousName'/>"
                        + "  <delete type='dummyObject' id='3' _name='name3'/>"
                        + "</changes>"
        ));

        assertEquals(3, changeSet.getChangeCount(DummyObject.TYPE));
        changeSet.accept(new ChangeSetVisitor() {
            public void visitCreation(Key key, FieldsValueScanner values) throws Exception {
                assertEquals(1, key.get(DummyObject.ID).intValue());
                assertEquals(8, values.size());
                assertEquals("name1", TestUtils.get(values, DummyObject.NAME));
                assertEquals(2.0, (Double) TestUtils.get(values, DummyObject.VALUE), 0.01);
                assertTrue((Boolean) TestUtils.get(values, DummyObject.PRESENT));
                assertNull(TestUtils.get(values, DummyObject.DATE));
            }

            public void visitUpdate(Key key, FieldsValueWithPreviousScanner values) throws Exception {
                assertEquals(2, key.get(DummyObject.ID).intValue());
                assertEquals(1, values.size());
                assertEquals("newName", TestUtils.get(values, DummyObject.NAME));
                assertEquals("previousName", TestUtils.getPrevious(values, DummyObject.NAME));
                assertFalse(TestUtils.contains(values, DummyObject.DATE));
                assertFalse(TestUtils.contains(values, DummyObject.VALUE));
            }

            public void visitDeletion(Key key, FieldsValueScanner values) throws Exception {
                assertEquals(3, key.get(DummyObject.ID).intValue());
                assertEquals(8, values.size());
                assertEquals("name3", TestUtils.get(values, DummyObject.NAME));
                assertNull(TestUtils.get(values, DummyObject.VALUE));
                assertNull(TestUtils.get(values, DummyObject.PRESENT));
                assertNull(TestUtils.get(values, DummyObject.DATE));
            }
        });
    }

    public void testMissingType() throws Exception {
        try {
            org.globsframework.core.xml.XmlChangeSetParser.parse(DummyModel.get(), new StringReader(
                    "<changes>"
                            + "  <create id='2' name='name1'/>"
                            + "</changes>"
            ));
            fail();
        } catch (InvalidParameter e) {
            assertEquals("Missing attribute 'type' in tag 'create'", e.getMessage());
        }
    }

    public void testWrongType() throws Exception {
        try {
            org.globsframework.core.xml.XmlChangeSetParser.parse(DummyModel.get(), new StringReader(
                    "<changes>"
                            + "  <create type='unknown' id='2' name='name1'/>"
                            + "</changes>"
            ));
            fail();
        } catch (ItemNotFound e) {
            assertEquals("No object type found with name: unknown", e.getMessage());
        }
    }

    public void testUnknownField() throws Exception {
        try {
            org.globsframework.core.xml.XmlChangeSetParser.parse(DummyModel.get(), new StringReader(
                    "<changes>"
                            + "  <create type='dummyObject' id='2' toto='name1'/>"
                            + "</changes>"
            ));
            fail();
        } catch (ItemNotFound e) {
            assertEquals("Unknown field 'toto' for type 'dummyObject'", e.getMessage());
        }
    }

    public void testInvalidValue() throws Exception {
        try {
            XmlChangeSetParser.parse(DummyModel.get(), new StringReader(
                    "<changes>"
                            + "  <create type='dummyObject' id='2' value='toto'/>"
                            + "</changes>"
            ));
            fail();
        } catch (InvalidParameter e) {
            assertEquals("'toto' is not a proper value for field 'value' in type 'dummyObject'", e.getMessage());
        }
    }
}
