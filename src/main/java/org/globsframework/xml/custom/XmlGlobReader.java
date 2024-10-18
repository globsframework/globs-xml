package org.globsframework.xml.custom;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.MutableGlob;
import org.globsframework.core.utils.Strings;
import org.globsframework.saxstack.parser.*;
import org.globsframework.saxstack.utils.XmlUtils;
import org.globsframework.xml.XmlGlobWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;

import java.io.Reader;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

public class XmlGlobReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlGlobReader.class);

    public static Glob read(GlobTypeAccessor globTypeAccessor, Reader reader) {
        GlobTypeXmlNodeModelService globTypeXmlNodeModelService = new GlobTypeXmlNodeModelService();
        RootGlobXmlNode rootNode = new RootGlobXmlNode(globTypeAccessor, globTypeXmlNodeModelService);
        SaxStackParser.parse(XmlUtils.getXmlReader(), rootNode, reader);
        return rootNode.mutableGlob;
    }

    public interface GlobTypeAccessor {
        GlobType get(String kind);
    }


    interface ManageFieldNode {
        XmlNode fillFromSubNode(MutableGlob mutableGlob, String childName, Attributes xmlAttrs);
    }

    interface ManageFieldAttr {
        void fill(MutableGlob mutableGlob, Attributes xmlAttrs);
    }

    interface UpdateFromStr {
        void update(MutableGlob mutableGlob, String value);
    }

    static class StringManageFieldNode implements ManageFieldNode {
        StringField field;

        public StringManageFieldNode(StringField field) {
            this.field = field;
        }

        public XmlNode fillFromSubNode(MutableGlob mutableGlob, String childName, Attributes xmlAttrs) {
            return new DefaultXmlNode() {
                public void setValue(String value) {
                    mutableGlob.set(field, value);
                }
            };
        }
    }

    static class StringArrayManageFieldNode implements ManageFieldNode {
        StringArrayField field;

        public StringArrayManageFieldNode(StringArrayField field) {
            this.field = field;
        }

        public XmlNode fillFromSubNode(MutableGlob mutableGlob, String childName, Attributes xmlAttrs) {
            return new DefaultXmlNode() {
                public void setValue(String value) {
                    String[] strings = mutableGlob.get(field);
                    if (strings == null) {
                        mutableGlob.set(field, new String[]{value});
                    } else {
                        strings = Arrays.copyOf(strings, strings.length + 1);
                        strings[strings.length - 1] = value;
                        mutableGlob.set(field, strings);
                    }
                }
            };
        }
    }

    static class IntegerManageFieldNode implements ManageFieldNode {
        IntegerField field;

        public IntegerManageFieldNode(IntegerField field) {
            this.field = field;
        }

        public XmlNode fillFromSubNode(MutableGlob mutableGlob, String childName, Attributes xmlAttrs) {
            return new DefaultXmlNode() {
                public void setValue(String value) {
                    mutableGlob.set(field, Integer.parseInt(value));
                }
            };
        }
    }

    static class BooleanManageFieldNode implements ManageFieldNode {
        BooleanField field;

        public BooleanManageFieldNode(BooleanField field) {
            this.field = field;
        }

        public XmlNode fillFromSubNode(MutableGlob mutableGlob, String childName, Attributes xmlAttrs) {
            return new DefaultXmlNode() {
                public void setValue(String value) {
                    mutableGlob.set(field, Boolean.parseBoolean(value));
                }
            };
        }
    }

    static class DateTimeManageFieldNode implements ManageFieldNode {
        DateTimeConverter dateTimeConverter;

        public DateTimeManageFieldNode(DateTimeField field) {
            dateTimeConverter = new DateTimeConverter(field);
        }

        public XmlNode fillFromSubNode(MutableGlob mutableGlob, String childName, Attributes xmlAttrs) {
            return new DefaultXmlNode() {
                public void setValue(String value) {
                    dateTimeConverter.update(mutableGlob, value);
                }
            };
        }
    }

    static class GlobManageFieldNode implements ManageFieldNode {
        private final GlobTypeXmlNodeModelService nodeModelService;
        GlobField field;

        public GlobManageFieldNode(GlobField field, GlobTypeXmlNodeModelService nodeModelService) {
            this.nodeModelService = nodeModelService;
            this.field = field;
        }

        public XmlNode fillFromSubNode(MutableGlob mutableGlob, String childName, Attributes xmlAttrs) {
            GlobType type = field.getTargetType();
            MutableGlob instantiate = type.instantiate();
            mutableGlob.set(field, instantiate);
            return new GlobTypeXmlNode(nodeModelService, instantiate, xmlAttrs);
        }
    }

    static class GlobArrayManageFieldNode implements ManageFieldNode {
        private final GlobTypeXmlNodeModelService nodeModelService;
        private final GlobArrayField field;

        public GlobArrayManageFieldNode(GlobTypeXmlNodeModelService nodeModelService, GlobArrayField field) {
            this.nodeModelService = nodeModelService;
            this.field = field;
        }

        public XmlNode fillFromSubNode(MutableGlob mutableGlob, String childName, Attributes xmlAttrs) {
            GlobType type = field.getTargetType();
            MutableGlob instantiate = type.instantiate();
            Glob[] globs = mutableGlob.get(field);
            Glob[] values = globs != null ? Arrays.copyOf(globs, globs.length + 1) : new Glob[1];
            values[values.length - 1] = instantiate;
            mutableGlob.set(field, values);
            return new GlobTypeXmlNode(nodeModelService, instantiate, xmlAttrs);
        }
    }

    static class GlobTypeXmlNodeModelService {
        Map<GlobType, GlobTypXmlNodeModel> xmlNodeModel = new HashMap<>();

        public GlobTypXmlNodeModel get(GlobType globType) {
            GlobTypXmlNodeModel globTypXmlNodeModel = xmlNodeModel.get(globType);
            if (globTypXmlNodeModel == null) {
                globTypXmlNodeModel = new GlobTypXmlNodeModel(globType, this);
                xmlNodeModel.put(globType, globTypXmlNodeModel);
            }
            return globTypXmlNodeModel;
        }
    }

    static abstract class AbstractManageFieldAttr implements ManageFieldAttr {
        final String xmlName;

        protected AbstractManageFieldAttr(String xmlName) {
            this.xmlName = xmlName;
        }

        public void fill(MutableGlob mutableGlob, Attributes xmlAttrs) {
            String value = xmlAttrs.getValue(xmlName);
            if (value != null) {
                update(mutableGlob, value);
            }
        }

        abstract void update(MutableGlob mutableGlob, String value);
    }

    static class StringManageFieldAttr extends AbstractManageFieldAttr {
        final StringField field;

        StringManageFieldAttr(StringField field, String xmlName) {
            super(xmlName);
            this.field = field;
        }

        void update(MutableGlob mutableGlob, String value) {
            mutableGlob.set(field, value);
        }
    }

    static class IntegerManageFieldAttr extends AbstractManageFieldAttr {
        final IntegerField field;

        IntegerManageFieldAttr(IntegerField field, String xmlName) {
            super(xmlName);
            this.field = field;
        }

        void update(MutableGlob mutableGlob, String value) {
            mutableGlob.set(field, Integer.parseInt(value));
        }
    }

    static class DoubleManageFieldAttr extends AbstractManageFieldAttr {
        final DoubleField field;

        DoubleManageFieldAttr(DoubleField field, String xmlName) {
            super(xmlName);
            this.field = field;
        }

        void update(MutableGlob mutableGlob, String value) {
            mutableGlob.set(field, Double.parseDouble(value));
        }
    }

    static class IntegerConverter implements UpdateFromStr {
        private final IntegerField field;

        IntegerConverter(IntegerField field) {
            this.field = field;
        }

        public void update(MutableGlob mutableGlob, String value) {
            if (Strings.isNotEmpty(value)) {
                mutableGlob.set(field, Integer.parseInt(value));
            }
        }
    }

    static class DoubleConverter implements UpdateFromStr {
        private final DoubleField field;

        DoubleConverter(DoubleField field) {
            this.field = field;
        }

        public void update(MutableGlob mutableGlob, String value) {
            if (Strings.isNotEmpty(value)) {
                mutableGlob.set(field, Double.parseDouble(value));
            }
        }
    }

    static class BooleanConverter implements UpdateFromStr {
        private final BooleanField field;

        BooleanConverter(BooleanField field) {
            this.field = field;
        }

        public void update(MutableGlob mutableGlob, String value) {
            if (Strings.isNotEmpty(value)) {
                mutableGlob.set(field, Boolean.parseBoolean(value));
            }
        }
    }

    static class DateConverter implements UpdateFromStr {
        private final DateField field;
        private final DateTimeFormatter dateTimeFormatter;

        DateConverter(DateField field) {
            this.field = field;
            Glob dataFormat = field.findAnnotation(XmlExportDateFormat.UNIQUE_KEY);
            if (dataFormat != null) {
                String s = dataFormat.get(XmlExportDateFormat.FORMAT);
                dateTimeFormatter = DateTimeFormatter.ofPattern(s);
            } else {
                dateTimeFormatter = DateTimeFormatter.ISO_DATE;
            }
        }

        public void update(MutableGlob mutableGlob, String value) {
            if (Strings.isNotEmpty(value)) {
                mutableGlob.set(field, LocalDate.from(dateTimeFormatter.parse(value.trim())));
            }
        }
    }

    static class DateManageFieldAttr extends AbstractManageFieldAttr {
        DateConverter dateConverter;

        protected DateManageFieldAttr(DateField date, String xmlName) {
            super(xmlName);
            dateConverter = new DateConverter(date);
        }

        void update(MutableGlob mutableGlob, String value) {
            dateConverter.update(mutableGlob, value);
        }
    }

    static class DateTimeConverter implements UpdateFromStr {
        final DateTimeField field;
        private final ZoneId zoneId;
        private DateTimeFormatter dateTimeFormatter;

        DateTimeConverter(DateTimeField field) {
            this.field = field;
            Glob dataFormat = field.findAnnotation(XmlExportDateFormat.UNIQUE_KEY);
            if (dataFormat != null) {
                String s = dataFormat.get(XmlExportDateFormat.FORMAT);
                zoneId = ZoneId.of(dataFormat.get(XmlExportDateFormat.ZONE_ID, ZoneId.systemDefault().getId()));
                dateTimeFormatter = DateTimeFormatter.ofPattern(s).withZone(zoneId);
            } else {
                zoneId = ZoneId.systemDefault();
                dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME.withZone(zoneId);
            }
        }

        public void update(MutableGlob mutableGlob, String value) {
            if (Strings.isNotEmpty(value)) {
                TemporalAccessor temporalAccessor = dateTimeFormatter.parseBest(value.trim(), ZonedDateTime::from, LocalDateTime::from, LocalDate::from);
                if (temporalAccessor instanceof ZonedDateTime) {
                    mutableGlob.set(field, (ZonedDateTime) temporalAccessor);
                } else if (temporalAccessor instanceof LocalDateTime) {
                    mutableGlob.set(field, ((LocalDateTime) temporalAccessor).atZone(zoneId));
                } else if (temporalAccessor instanceof LocalDate) {
                    mutableGlob.set(field, ZonedDateTime.of((LocalDate) temporalAccessor, LocalTime.MIDNIGHT, zoneId));
                }
            }
        }
    }

    static class DateTimeFieldReader extends AbstractManageFieldAttr {
        DateTimeConverter dateTimeConverter;

        DateTimeFieldReader(DateTimeField field, String xmlName) {
            super(xmlName);
            dateTimeConverter = new DateTimeConverter(field);
        }

        void update(MutableGlob mutableGlob, String value) {
            dateTimeConverter.update(mutableGlob, value);
        }
    }


    static class GlobTypXmlNodeModel {
        private final GlobTypeXmlNodeModelService nodeModelService;
        private UpdateFromStr valueUpdated = null;
        private GlobType globType;
        private Map<String, ManageFieldNode> fieldAsNode = new HashMap<>();
        private Collection<ManageFieldAttr> fieldAsAttribute = new ArrayList<>();
        private Field valueField;

        public GlobTypXmlNodeModel(GlobType globType, GlobTypeXmlNodeModelService nodeModelService) {
            this.nodeModelService = nodeModelService;
            this.globType = globType;
            for (Field field : globType.getFields()) {
                String xmlName = XmlGlobWriter.getXmlName(field);
                if (field.hasAnnotation(XmlAsNode.UNIQUE_KEY) || !field.getDataType().isPrimive()) {
                    fieldAsNode.put(xmlName, field.safeAccept(new FieldModelVisitor(this.nodeModelService)).manageFieldNode);
                } else if (field.hasAnnotation(XmlValue.UNIQUE_KEY)) {
                    valueUpdated = field.safeAccept(new ConvertFieldAsAttrVisitor()).updateFromStr;
                } else {
                    fieldAsAttribute.add(field.safeAccept(new ManagedFieldAsAttrVisitor(xmlName)).manageFieldAttr);
                }
            }
        }

        public void fillAttr(MutableGlob mutableGlob, Attributes xmlAttrs) {
            for (ManageFieldAttr manageFieldAttr : fieldAsAttribute) {
                manageFieldAttr.fill(mutableGlob, xmlAttrs);
            }
        }

        public XmlNode fillFromSubNode(MutableGlob mutableGlob, String childName, Attributes xmlAttrs) {
            ManageFieldNode field = fieldAsNode.get(childName);
            if (field != null) {
                return field.fillFromSubNode(mutableGlob, childName, xmlAttrs);
            } else {
                LOGGER.warn(childName + " ignored.");
                return SilentXmlNode.INSTANCE;
            }
        }

        public void setValue(MutableGlob mutableGlob, String value) {
            if (valueUpdated != null) {
                valueUpdated.update(mutableGlob, value);
            }
        }

        private static class FieldModelVisitor extends FieldVisitor.AbstractWithErrorVisitor {
            private final GlobTypeXmlNodeModelService nodeModelService;
            private ManageFieldNode manageFieldNode;

            private FieldModelVisitor(GlobTypeXmlNodeModelService nodeModelService) {
                this.nodeModelService = nodeModelService;
            }

            public void visitBoolean(BooleanField field) throws Exception {
                manageFieldNode = new BooleanManageFieldNode(field);
            }

            public void visitString(StringField field) throws Exception {
                manageFieldNode = new StringManageFieldNode(field);
            }

            public void visitStringArray(StringArrayField field) throws Exception {
                manageFieldNode = new StringArrayManageFieldNode(field);
            }

            public void visitInteger(IntegerField field) throws Exception {
                manageFieldNode = new IntegerManageFieldNode(field);
            }

            public void visitDate(DateField field) throws Exception {
                manageFieldNode = new DateManageFieldNode(field);
            }

            public void visitDateTime(DateTimeField field) throws Exception {
                manageFieldNode = new DateTimeManageFieldNode(field);
            }

            public void visitGlob(GlobField field) throws Exception {
                manageFieldNode = new GlobManageFieldNode(field, nodeModelService);
            }

            public void visitGlobArray(GlobArrayField field) throws Exception {
                manageFieldNode = new GlobArrayManageFieldNode(nodeModelService, field);
            }

            private static class DateManageFieldNode implements ManageFieldNode {
                final DateConverter dateConverter;
                private final DateField field;

                public DateManageFieldNode(DateField field) {
                    this.field = field;
                    dateConverter = new DateConverter(field);
                }

                public XmlNode fillFromSubNode(MutableGlob mutableGlob, String childName, Attributes xmlAttrs) {
                    return new DefaultXmlNode() {
                        @Override
                        public void setValue(String value) {
                            dateConverter.update(mutableGlob, value);
                        }
                    };
                }
            }
        }

        private static class ManagedFieldAsAttrVisitor extends FieldVisitor.AbstractWithErrorVisitor {
            ManageFieldAttr manageFieldAttr;
            private String xmlName;

            public ManagedFieldAsAttrVisitor(String xmlName) {
                this.xmlName = xmlName;
            }

            public void visitString(StringField field) throws Exception {
                manageFieldAttr = new StringManageFieldAttr(field, xmlName);
            }

            public void visitInteger(IntegerField field) throws Exception {
                manageFieldAttr = new IntegerManageFieldAttr(field, xmlName);
            }

            public void visitDouble(DoubleField field) throws Exception {
                manageFieldAttr = new DoubleManageFieldAttr(field, xmlName);
            }

            public void visitDate(DateField field) throws Exception {
                manageFieldAttr = new DateManageFieldAttr(field, xmlName);
            }

            public void visitDateTime(DateTimeField field) throws Exception {
                manageFieldAttr = new DateTimeFieldReader(field, xmlName);
            }
        }

        private static class ConvertFieldAsAttrVisitor extends FieldVisitor.AbstractWithErrorVisitor {
            UpdateFromStr updateFromStr;

            public void visitString(StringField field) throws Exception {
                updateFromStr = new StringUpdateFromStr(field);
            }

            public void visitInteger(IntegerField field) throws Exception {
                updateFromStr = new IntegerConverter(field);
            }

            public void visitDouble(DoubleField field) throws Exception {
                updateFromStr = new DoubleConverter(field);
            }

            public void visitDate(DateField field) throws Exception {
                updateFromStr = new DateConverter(field);
            }

            public void visitDateTime(DateTimeField field) throws Exception {
                updateFromStr = new DateTimeConverter(field);
            }

            public void visitBoolean(BooleanField field) throws Exception {
                updateFromStr = new BooleanConverter(field);
            }

            private class StringUpdateFromStr implements UpdateFromStr {
                private StringField field;

                public StringUpdateFromStr(StringField field) {
                    this.field = field;
                }

                public void update(MutableGlob mutableGlob, String value) {
                    mutableGlob.set(field, value);
                }
            }
        }
    }

    static class GlobTypeXmlNode implements XmlNode {
        private final GlobTypXmlNodeModel globTypXmlNodeModel;
        MutableGlob mutableGlob;

        public GlobTypeXmlNode(GlobTypeXmlNodeModelService globTypeXmlNodeModelService, MutableGlob mutableGlob, Attributes xmlAttrs) {
            this.globTypXmlNodeModel = globTypeXmlNodeModelService.get(mutableGlob.getType());
            this.mutableGlob = mutableGlob;
            if (xmlAttrs != null && xmlAttrs.getLength() > 0) {
                globTypXmlNodeModel.fillAttr(mutableGlob, xmlAttrs);
            }
        }

        public XmlNode getSubNode(String childName, Attributes xmlAttrs, String uri, String fullName) throws ExceptionHolder {
            return globTypXmlNodeModel.fillFromSubNode(mutableGlob, childName, xmlAttrs);
        }

        public void setValue(String value) throws ExceptionHolder {
            globTypXmlNodeModel.setValue(mutableGlob, value);
        }

        public void complete() throws ExceptionHolder {

        }
    }

    private static class RootGlobXmlNode extends DefaultXmlNode {
        private final GlobTypeAccessor globTypeAccessor;
        private final GlobTypeXmlNodeModelService globTypeXmlNodeModelService;
        private MutableGlob mutableGlob;

        public RootGlobXmlNode(GlobTypeAccessor globTypeAccessor, GlobTypeXmlNodeModelService globTypeXmlNodeModelService) {
            this.globTypeAccessor = globTypeAccessor;
            this.globTypeXmlNodeModelService = globTypeXmlNodeModelService;
        }

        public XmlNode getSubNode(String childName, Attributes xmlAttrs, String uri, String fullName) {
            GlobType globType = globTypeAccessor.get(childName);
            mutableGlob = globType.instantiate();
            return new GlobTypeXmlNode(globTypeXmlNodeModelService, mutableGlob, xmlAttrs);
        }
    }
}
