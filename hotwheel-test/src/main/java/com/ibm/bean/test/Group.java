package com.ibm.bean.test;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

@GroupSequence({Default.class, GroupA.class, GroupB.class})
public interface Group {

}
