package org.globsframework.xml.custom;

import org.globsframework.core.metamodel.GlobModel;
import org.globsframework.core.metamodel.impl.DefaultGlobModel;

public class AllAnnotations {
    public static GlobModel MODEL = new DefaultGlobModel(_XmlAsNode.TYPE, _XmlValueAsCData.TYPE, XmlNS.TYPE,
            _XmlValue.TYPE, _XmlExportDateFormat.TYPE, XmlUseParentNS.TYPE);
}
