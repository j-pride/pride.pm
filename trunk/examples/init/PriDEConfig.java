/*******************************************************************************
 * Copyright (c) 2001-2007 The PriDE team and MATHEMA Software GmbH
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public
 * License (LGPL) which accompanies this distribution, and is available
 * at http://pride.sourceforge.net/LGPL.html
 * 
 * Contributors:
 *     Jan Lessner, MATHEMA Software GmbH - Example code
 *******************************************************************************/
package init;

import de.mathema.pride.*;
import de.mathema.util.Singleton;

public class PriDEConfig {
    private final static Singleton _singleton =
        new Singleton() {
                private ResourceAccessor initJ2EE() throws Exception {
                    DatabaseFactory.setDatabaseName("jdbc.mydb");
                    return new ResourceAccessorJ2EE(System.getProperties());
                }
                
                private ResourceAccessor initJ2SE() throws Exception {
                    DatabaseFactory.setDatabaseName("jdbc:odbc:mydb");
                    return new ResourceAccessorJ2SE(System.getProperties());
                }
                
                private boolean isJ2EE() { return false; } // find out somehow
                
                protected Object createInstance() throws Exception {
                    ResourceAccessor accessor = isJ2EE() ? initJ2EE() : initJ2SE();
                    DatabaseFactory.setResourceAccessor(accessor);
                    return accessor;
                }
            };
    
    public static void init() throws Exception {
        _singleton.getInstance();
    }
}
