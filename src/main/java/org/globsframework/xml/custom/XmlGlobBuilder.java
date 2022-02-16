package org.globsframework.xml.custom;

import org.globsframework.metamodel.Field;
import org.globsframework.metamodel.fields.*;
import org.globsframework.model.Glob;
import org.globsframework.saxstack.writer.XmlTag;
import org.globsframework.saxstack.writer.XmlWriter;
import org.globsframework.xml.XmlGlobWriter;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class XmlGlobBuilder {


    public static void write(Glob data, Writer writer) throws IOException {
        XmlTag xmlTag = XmlWriter.startTag(writer, XmlGlobWriter.getXmlName(data.getType()));
        data.safeAccept(new XmlFieldValueVisitor(xmlTag));
        xmlTag.end();
    }

    private static class XmlFieldValueVisitor extends FieldValueVisitor.AbstractWithErrorVisitor {
        private XmlTag xmlTag;

        public XmlFieldValueVisitor(XmlTag xmlTag) {
            this.xmlTag = xmlTag;
        }

        public void visitInteger(IntegerField field, Integer value) throws Exception {
            dumpSimpleValue(field, value != null ? Integer.toString(value) : null);
        }

        private void dumpSimpleValue(Field field, String strValue) throws IOException {
            if (field.hasAnnotation(_XmlAsNode.UNIQUE_KEY)) {
                if (strValue != null || field.getAnnotation(_XmlAsNode.UNIQUE_KEY).isTrue(_XmlAsNode.MANDATORY)) {
                    xmlTag = xmlTag.createChildTag(XmlGlobWriter.getXmlName(field));
                    if (strValue != null) {
                        if (field.hasAnnotation(_XmlValueAsCData.UNIQUE_KEY)) {
                            xmlTag.addCDataValue(strValue);
                        } else {
                            xmlTag.addValue(strValue);
                        }
                    }
                    xmlTag = xmlTag.end();
                }
            } else if (field.hasAnnotation(_XmlValue.UNIQUE_KEY)) {
                xmlTag.addValue(strValue);
            }
            else {
                if (strValue != null) {
                    xmlTag.addAttribute(XmlGlobWriter.getXmlName(field), strValue);
                }
            }
        }

        public void visitDouble(DoubleField field, Double value) throws Exception {
                dumpSimpleValue(field, value != null ? Double.toString(value) : null);
        }

        public void visitString(StringField field, String value) throws Exception {
                dumpSimpleValue(field, value);
        }

        public void visitBoolean(BooleanField field, Boolean value) throws Exception {
            dumpSimpleValue(field, value != null ? Boolean.toString(value) : null);
        }

        public void visitDate(DateField field, LocalDate value) throws Exception {
            DateTimeFormatter dateTimeFormatter;
            if (field.hasAnnotation(_XmlExportDateFormat.UNIQUE_KEY)) {
                Glob annotation = field.getAnnotation(_XmlExportDateFormat.UNIQUE_KEY);
                dateTimeFormatter = DateTimeFormatter.ofPattern(annotation.get(_XmlExportDateFormat.FORMAT));
            }
            else {
                dateTimeFormatter = DateTimeFormatter.ISO_DATE;
            }
            dumpSimpleValue(field, value != null ? dateTimeFormatter.format(value) : null);
        }

        public void visitDateTime(DateTimeField field, ZonedDateTime value) throws Exception {
            DateTimeFormatter dateTimeFormatter;
            if (field.hasAnnotation(_XmlExportDateFormat.UNIQUE_KEY)) {
                Glob annotation = field.getAnnotation(_XmlExportDateFormat.UNIQUE_KEY);
                dateTimeFormatter = DateTimeFormatter.ofPattern(annotation.get(_XmlExportDateFormat.FORMAT));
            }
            else {
                dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
            }
            dumpSimpleValue(field, value != null ? dateTimeFormatter.format(value) : null);
        }

        public void visitGlob(GlobField field, Glob value) throws Exception {
            if (value != null) {
                xmlTag = xmlTag.createChildTag(XmlGlobWriter.getXmlName(field));
                value.safeAccept(this);
                xmlTag = xmlTag.end();
            }
        }

        public void visitGlobArray(GlobArrayField field, Glob[] value) throws Exception {
            if (value != null && value.length != 0) {
                for (Glob glob : value) {
                    xmlTag = xmlTag.createChildTag(XmlGlobWriter.getXmlName(field));
                    glob.safeAccept(this);
                    xmlTag = xmlTag.end();
                }
            }

        }
    }

}
