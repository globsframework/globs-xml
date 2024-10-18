package org.globsframework.xml;

import org.globsframework.core.metamodel.GlobModel;
import org.globsframework.core.metamodel.impl.DefaultGlobModel;
import org.globsframework.xml.custom.XmlAsNode;

public class GlobModelInit {
    public static GlobModel MODEL = new DefaultGlobModel(XmlAsNode.TYPE);
}
