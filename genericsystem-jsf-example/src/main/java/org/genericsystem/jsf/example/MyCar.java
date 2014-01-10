package org.genericsystem.jsf.example;

import org.genericsystem.annotation.Meta;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.value.StringValue;
import org.genericsystem.core.GenericImpl;

@SystemGeneric
@Meta(CarType.class)
@StringValue("myCar")
public class MyCar extends GenericImpl {

}
