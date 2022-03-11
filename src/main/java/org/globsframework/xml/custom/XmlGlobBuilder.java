package org.globsframework.xml.custom;

import org.globsframework.metamodel.Field;
import org.globsframework.metamodel.GlobType;
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
import java.util.*;

public class XmlGlobBuilder {
    private final Map<String, String> nsMapping = new HashMap<>();

    public static void write(Glob data, Writer writer) throws IOException {
        new XmlGlobBuilder().writeWithNS(data, writer);
    }

    public XmlGlobBuilder() {
    }

    public XmlGlobBuilder withNS(String name, String url) {
        nsMapping.put(url, name);
        return this;
    }

    public void writeWithNS(Glob data, Writer writer) throws IOException {
        GlobType type = data.getType();
        XmlNamespace xmlNamespace = XmlNamespace.create(type, nsMapping);
        XmlTag xmlTag = XmlWriter.startTag(writer, xmlNamespace.addToTag(XmlGlobWriter.getXmlName(type)));
        xmlNamespace.addAttr(xmlTag);
        data.safeAccept(new XmlFieldValueVisitor(xmlTag, xmlNamespace));
        xmlTag.end();
    }

    static class XmlNamespace {
        private final String namespace;
        private final String url;
        private Map<String, String> nsMapping;
        private boolean added;

        public XmlNamespace(String namespace, String url, Map<String, String> nsMapping) {
            this.namespace = namespace;
            this.url = url;
            this.nsMapping = nsMapping;
        }

        public XmlNamespace(Map<String, String> nsMapping) {
            this("", "", nsMapping);
        }

        public static XmlNamespace create(GlobType type, Map<String, String> nsMapping) {
            Glob annotation = type.findAnnotation(XmlNS.UNIQUE_KEY);
            if (annotation != null) {
                String s = annotation.get(XmlNS.name, "");
                return new XmlNamespace(nsMapping.getOrDefault(annotation.get(XmlNS.url), s), annotation.get(XmlNS.url), nsMapping);
            }
            return new XmlNamespace(nsMapping);
        }

        public String addToTag(String tagName) {
            if (namespace.isEmpty()) {
                return tagName;
            } else {
                return namespace + ":" + tagName;
            }
        }

        public XmlNamespace sub(GlobType type) {
            Glob annotation = type.findAnnotation(XmlNS.UNIQUE_KEY);
            if (annotation != null) {
                String s = annotation.get(XmlNS.name, "");
                return new XmlNamespace(nsMapping.getOrDefault(annotation.get(XmlNS.url), s), annotation.get(XmlNS.url), nsMapping);
            }
            return this;
        }

        public void addAttr(XmlTag xmlTag) throws IOException {
            if (!url.isEmpty() && !added) {
                added = true;
                xmlTag.addAttribute("xmlns" + (namespace.isEmpty() ? "" : ":" + namespace), url);
            }
        }
    }

    private static class XmlFieldValueVisitor extends FieldValueVisitor.AbstractWithErrorVisitor {
        private XmlTag xmlTag;
        private Deque<XmlNamespace> ns = new ArrayDeque<>();

        public XmlFieldValueVisitor(XmlTag xmlTag, XmlNamespace xmlNamespace) {
            this.xmlTag = xmlTag;
            ns.push(xmlNamespace);
        }

        public void visitInteger(IntegerField field, Integer value) throws Exception {
            dumpSimpleValue(field, value != null ? Integer.toString(value) : null);
        }

        private void dumpSimpleValue(Field field, String strValue) throws IOException {
            if (field.hasAnnotation(_XmlAsNode.UNIQUE_KEY)) {
                if (strValue != null || field.getAnnotation(_XmlAsNode.UNIQUE_KEY).isTrue(_XmlAsNode.MANDATORY)) {
                    xmlTag = xmlTag.createChildTag(ns.element().addToTag(XmlGlobWriter.getXmlName(field)));
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
            } else {
                if (strValue != null) {
                    xmlTag.addAttribute(ns.element().addToTag(XmlGlobWriter.getXmlName(field)), strValue);
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
            } else {
                dateTimeFormatter = DateTimeFormatter.ISO_DATE;
            }
            dumpSimpleValue(field, value != null ? dateTimeFormatter.format(value) : null);
        }

        public void visitDateTime(DateTimeField field, ZonedDateTime value) throws Exception {
            DateTimeFormatter dateTimeFormatter;
            if (field.hasAnnotation(_XmlExportDateFormat.UNIQUE_KEY)) {
                Glob annotation = field.getAnnotation(_XmlExportDateFormat.UNIQUE_KEY);
                dateTimeFormatter = DateTimeFormatter.ofPattern(annotation.get(_XmlExportDateFormat.FORMAT));
            } else {
                dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
            }
            dumpSimpleValue(field, value != null ? dateTimeFormatter.format(value) : null);
        }

        public void visitGlob(GlobField field, Glob value) throws Exception {
            if (value != null) {
                GlobType targetType = field.getTargetType();
                ns.push(ns.element().sub(targetType));
                xmlTag = xmlTag.createChildTag(ns.element().addToTag(XmlGlobWriter.getXmlName(field)));
                ns.element().addAttr(xmlTag);
                value.safeAccept(this);
                xmlTag = xmlTag.end();
                ns.pop();
            }
        }

        public void visitGlobArray(GlobArrayField field, Glob[] value) throws Exception {
            if (value != null && value.length != 0) {
                GlobType targetType = field.getTargetType();
                for (Glob glob : value) {
                    ns.push(ns.element().sub(targetType));
                    xmlTag = xmlTag.createChildTag(ns.element().addToTag(XmlGlobWriter.getXmlName(field)));
                    ns.element().addAttr(xmlTag);
                    glob.safeAccept(this);
                    xmlTag = xmlTag.end();
                    ns.pop();
                }
            }
        }
    }
}
