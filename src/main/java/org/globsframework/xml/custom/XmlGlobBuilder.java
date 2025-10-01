package org.globsframework.xml.custom;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.model.Glob;
import org.globsframework.saxstack.writer.XmlTag;
import org.globsframework.saxstack.writer.XmlWriter;
import org.globsframework.xml.XmlGlobWriter;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class XmlGlobBuilder {
    private final boolean defaultToXmlNode;
    private Map<String, String> nsMapping = Map.of();

    public static void write(Glob data, Writer writer) throws IOException {
        write(data, writer, false);
    }

    public static void write(Glob data, Writer writer, boolean defaultToXmlNode) throws IOException {
        new XmlGlobBuilder(defaultToXmlNode).writeWithNS(data, writer);
    }

    public XmlGlobBuilder() {
        this.defaultToXmlNode = false;
    }

    public XmlGlobBuilder(boolean defaultToXmlNode) {
        this.defaultToXmlNode = defaultToXmlNode;
    }

    public XmlGlobBuilder withNS(String name, String url) {
        if (nsMapping == Map.<String, String>of()) {
            nsMapping = new HashMap<>();
        }
        nsMapping.put(url, name);
        return this;
    }

    public void writeWithNS(Glob data, Writer writer) throws IOException {
        GlobType type = data.getType();
        XmlNamespace xmlNamespace = XmlNamespace.create(type, nsMapping);
        XmlTag xmlTag = XmlWriter.startTag(writer, xmlNamespace.addToTag(XmlGlobWriter.getXmlName(type)));
        xmlNamespace.addAttr(xmlTag);
        data.safeAccept(new XmlFieldValueVisitor(xmlTag, xmlNamespace, defaultToXmlNode));
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
        private final boolean defaultToXmlNode;
        private Deque<XmlNamespace> ns = new ArrayDeque<>();

        public XmlFieldValueVisitor(XmlTag xmlTag, XmlNamespace xmlNamespace, boolean defaultToXmlNode) {
            this.xmlTag = xmlTag;
            this.defaultToXmlNode = defaultToXmlNode;
            ns.push(xmlNamespace);
        }

        public void visitInteger(IntegerField field, Integer value) throws Exception {
            dumpSimpleValue(field, value != null ? Integer.toString(value) : null);
        }

        private void dumpSimpleValue(Field field, String strValue) throws IOException {
            if (field.hasAnnotation(XmlValue.UNIQUE_KEY)) {
                xmlTag.addValue(strValue);
            } else if (defaultToXmlNode || field.hasAnnotation(XmlAsNode.UNIQUE_KEY)) {
                final Glob annotation = field.findAnnotation(XmlAsNode.UNIQUE_KEY);
                if (strValue != null || (annotation != null && annotation.isTrue(XmlAsNode.MANDATORY))) {
                    xmlTag = xmlTag.createChildTag(ns.element().addToTag(XmlGlobWriter.getXmlName(field)));
                    if (strValue != null) {
                        if (field.hasAnnotation(XmlValueAsCData.UNIQUE_KEY)) {
                            xmlTag.addCDataValue(strValue);
                        } else {
                            xmlTag.addValue(strValue);
                        }
                    }
                    xmlTag = xmlTag.end();
                }
            } else {
                if (strValue != null) {
                    xmlTag.addAttribute(ns.element().addToTag(XmlGlobWriter.getXmlName(field)), strValue);
                }
            }
        }

        public void visitBigDecimal(BigDecimalField field, BigDecimal value) throws Exception {
            dumpSimpleValue(field, value != null ? value.toString() : null);
        }

        public void visitLong(LongField field, Long value) throws Exception {
            dumpSimpleValue(field, value != null ? Long.toString(value) : null);
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
            if (field.hasAnnotation(XmlExportDateFormat.UNIQUE_KEY)) {
                Glob annotation = field.getAnnotation(XmlExportDateFormat.UNIQUE_KEY);
                dateTimeFormatter = DateTimeFormatter.ofPattern(annotation.get(XmlExportDateFormat.FORMAT));
            } else {
                dateTimeFormatter = DateTimeFormatter.ISO_DATE;
            }
            dumpSimpleValue(field, value != null ? dateTimeFormatter.format(value) : null);
        }

        public void visitDateTime(DateTimeField field, ZonedDateTime value) throws Exception {
            DateTimeFormatter dateTimeFormatter;
            if (field.hasAnnotation(XmlExportDateFormat.UNIQUE_KEY)) {
                Glob annotation = field.getAnnotation(XmlExportDateFormat.UNIQUE_KEY);
                dateTimeFormatter = DateTimeFormatter.ofPattern(annotation.get(XmlExportDateFormat.FORMAT));
            } else {
                dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
            }
            dumpSimpleValue(field, value != null ? dateTimeFormatter.format(value) : null);
        }

        public void visitStringArray(StringArrayField field, String[] value) throws Exception {
            for (String s : value) {
                dumpSimpleValue(field, s);
            }
        }

        public void visitGlob(GlobField field, Glob value) throws Exception {
            if (value != null) {
                GlobType targetType = field.getTargetType();
                final boolean useParent = field.findOptAnnotation(XmlUseParentNS.UNIQUE_KEY)
                        .map(XmlUseParentNS.useParent).orElse(false);
                if (!useParent) {
                    ns.push(ns.element().sub(targetType));
                }
                xmlTag = xmlTag.createChildTag(ns.element().addToTag(XmlGlobWriter.getXmlName(field)));
                if (useParent) {
                    ns.push(ns.element().sub(targetType));
                }
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
                    final boolean useParent = field.findOptAnnotation(XmlUseParentNS.UNIQUE_KEY)
                            .map(XmlUseParentNS.useParent).orElse(false);
                    if (!useParent) {
                        ns.push(ns.element().sub(targetType));
                    }
                    xmlTag = xmlTag.createChildTag(ns.element().addToTag(XmlGlobWriter.getXmlName(field)));
                    if (useParent) {
                        ns.push(ns.element().sub(targetType));
                    }
                    ns.element().addAttr(xmlTag);
                    glob.safeAccept(this);
                    xmlTag = xmlTag.end();
                    ns.pop();
                }
            }
        }
    }
}
