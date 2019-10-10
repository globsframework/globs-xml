package org.globsframework.xml.custom;

import org.globsframework.metamodel.GlobModel;
import org.globsframework.metamodel.impl.DefaultGlobModel;

public class ModelAnnotations {
    static public final GlobModel MODELS = new DefaultGlobModel(_XmlAsNode.TYPE, _XmlValueAsCData.TYPE);
}
