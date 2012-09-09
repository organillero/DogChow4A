package mx.ferreyra.dogapp.org.ksoap2.serialization;

import junit.framework.TestCase;

public class PropertyInfoTest extends TestCase {
    
    public void testClearingValues() {
        PropertyInfo info = new PropertyInfo();
        info.type = new Integer(1);
        info.name = "propertyName";
        info.namespace = "namespaceName";
        info.flags = 12;
        
        info.clear();
        
        assertEquals(PropertyInfo.OBJECT_CLASS, info.type);
        assertEquals(0, info.flags);
        assertEquals(null, info.name);
        assertEquals(null, info.namespace);
    }

}
