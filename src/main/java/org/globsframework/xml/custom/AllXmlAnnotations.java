package org.globsframework.xml.custom;

import org.globsframework.core.metamodel.GlobModel;
import org.globsframework.core.metamodel.impl.DefaultGlobModel;

public class AllXmlAnnotations {
    public static GlobModel MODEL = new DefaultGlobModel(XmlAsNode.TYPE, XmlValueAsCData.TYPE, XmlNS.TYPE,
            XmlValue.TYPE, XmlExportDateFormat.TYPE, XmlUseParentNS.TYPE);
}
