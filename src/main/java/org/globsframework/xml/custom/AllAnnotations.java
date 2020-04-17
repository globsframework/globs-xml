package org.globsframework.xml.custom;

import org.globsframework.metamodel.GlobModel;
import org.globsframework.metamodel.impl.DefaultGlobModel;

public class AllAnnotations {
    public static GlobModel globModel = new DefaultGlobModel(_XmlAsNode.TYPE, _XmlValueAsCData.TYPE);
}
