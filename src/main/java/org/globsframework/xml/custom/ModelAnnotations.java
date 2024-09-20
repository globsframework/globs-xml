package org.globsframework.xml.custom;

import org.globsframework.core.metamodel.GlobModel;
import org.globsframework.core.metamodel.impl.DefaultGlobModel;

public class ModelAnnotations {
    static public final GlobModel MODELS = new DefaultGlobModel(_XmlAsNode.TYPE, _XmlValueAsCData.TYPE);
}
