package org.globsframework.xml;

import org.globsframework.metamodel.GlobModel;
import org.globsframework.metamodel.impl.DefaultGlobModel;
import org.globsframework.xml.custom._XmlAsNode;

public class GlobModelInit {
    public static GlobModel MODEL = new DefaultGlobModel(_XmlAsNode.TYPE);
}
