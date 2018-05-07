package basic;
/*******************************************************************************
 * Copyright (c) 2001-2003 The PriDE team and MATHEMA Software Ltd.
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of an extended GNU Public License
 * (GPL) which accompanies this distribution, and is available at
 * http://pride.sourceforge.net/EGPL.html
 * 
 * Contributors:
 *     Jan Lessner, MATHEMA Software Ltd. - initial API and implementation
 *******************************************************************************/
import de.mathema.pride.*;

import java.io.*;
import java.sql.*;
import java.text.DateFormat;
import java.util.Properties;

/**
 * This resource accessor is required for MySQL with version less than 4
 * It avoids setting auto commit for connections which throws an
 * exception in older MySQL versions without transaction support
 * 
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
public class ResourceAccessorMySQL extends ResourceAccessorJ2SE
{
    public ResourceAccessorMySQL(Properties props)
        throws Exception {
        super(props);
    }
    
    protected void setAutoCommit(Connection con, boolean state) { }
}

/* $Log: $
 */
