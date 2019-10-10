package org.globsframework.xml.custom;

import org.globsframework.metamodel.Field;
import org.globsframework.metamodel.GlobType;
import org.globsframework.metamodel.fields.*;
import org.globsframework.model.Glob;
import org.globsframework.model.MutableGlob;
import org.globsframework.saxstack.parser.*;
import org.globsframework.saxstack.utils.XmlUtils;
import org.globsframework.xml.XmlGlobWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;

import java.io.Reader;
import java.util.*;

public class XmlGlobReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlGlobReader.class);

    public interface GlobTypeAccessor {
        GlobType get(String kind);
    }

    public static Glob read(GlobTypeAccessor globTypeAccessor, Reader reader) {
        GlobTypeXmlNodeModelService globTypeXmlNodeModelService = new GlobTypeXmlNodeModelService();
        RootGlobXmlNode rootNode = new RootGlobXmlNode(globTypeAccessor, globTypeXmlNodeModelService);
        SaxStackParser.parse(XmlUtils.getXmlReader(), rootNode, reader);
        return rootNode.mutableGlob;
    }


    interface ManageFieldNode {
         XmlNode fillFromSubNode(MutableGlob mutableGlob, String childName, Attributes xmlAttrs);
    }

    static class StringManageFieldNode implements ManageFieldNode {
        StringField field;

        public StringManageFieldNode(StringField field) {
            this.field = field;
        }

        public XmlNode fillFromSubNode(MutableGlob mutableGlob, String childName, Attributes xmlAttrs) {
            return new DefaultXmlNode(){
                public void setValue(String value) {
                    mutableGlob.set(field, value);
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
            return new DefaultXmlNode(){
                public void setValue(String value) {
                    mutableGlob.set(field, Integer.parseInt(value));
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
            GlobType type = field.getType();
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
            GlobType type = field.getType();
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

        public GlobTypXmlNodeModel get(GlobType globType){
            GlobTypXmlNodeModel globTypXmlNodeModel = xmlNodeModel.get(globType);
            if (globTypXmlNodeModel == null) {
                globTypXmlNodeModel = new GlobTypXmlNodeModel(globType, this);
                xmlNodeModel.put(globType, globTypXmlNodeModel);
            }
            return globTypXmlNodeModel;
        }
    }

    interface ManageFieldAttr {
        void fill(MutableGlob mutableGlob, Attributes xmlAttrs);
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

    static class GlobTypXmlNodeModel {
        private final GlobTypeXmlNodeModelService nodeModelService;
        GlobType globType;
        Map<String, ManageFieldNode> fieldAsNode = new HashMap<>();
        Collection<ManageFieldAttr> fieldAsAttribute = new ArrayList<>();

        public GlobTypXmlNodeModel(GlobType globType, GlobTypeXmlNodeModelService nodeModelService) {
            this.nodeModelService = nodeModelService;
            this.globType = globType;
            for (Field field : globType.getFields()) {
                String xmlName = XmlGlobWriter.getXmlName(field);
                if (field.hasAnnotation(_XmlAsNode.UNIQUE_KEY) || !field.getDataType().isPrimive()) {
                    fieldAsNode.put(xmlName, field.safeVisit(new FieldModelVisitor(this.nodeModelService)).manageFieldNode);
                } else {
                    fieldAsAttribute.add(field.safeVisit(new ManagedFialdAsAttrVisitor(xmlName)).manageFieldAttr);
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
            }
            else {
                LOGGER.warn(childName + " ignored.");
                return SilentXmlNode.INSTANCE;
            }
        }

        private static class FieldModelVisitor extends FieldVisitor.AbstractWithErrorVisitor {
            private ManageFieldNode manageFieldNode;
            private final GlobTypeXmlNodeModelService nodeModelService;

            private FieldModelVisitor(GlobTypeXmlNodeModelService nodeModelService) {
                this.nodeModelService = nodeModelService;
            }

            public void visitString(StringField field) throws Exception {
                manageFieldNode = new StringManageFieldNode(field);
            }

            public void visitInteger(IntegerField field) throws Exception {
                manageFieldNode = new IntegerManageFieldNode(field);
            }

            public void visitGlob(GlobField field) throws Exception {
                manageFieldNode = new GlobManageFieldNode(field, nodeModelService);
            }

            public void visitGlobArray(GlobArrayField field) throws Exception {
                manageFieldNode = new GlobArrayManageFieldNode(nodeModelService, field);
            }
        }

        private static class ManagedFialdAsAttrVisitor extends FieldVisitor.AbstractWithErrorVisitor {
            ManageFieldAttr manageFieldAttr;
            private String xmlName;

            public ManagedFialdAsAttrVisitor(String xmlName) {
                this.xmlName = xmlName;
            }

            public void visitString(StringField field) throws Exception {
                manageFieldAttr = new StringManageFieldAttr(field, xmlName);
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
