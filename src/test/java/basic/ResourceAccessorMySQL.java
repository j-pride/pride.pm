package basic;
import java.io.*;
import java.sql.*;
import java.text.DateFormat;
import java.util.Properties;

import pm.pride.*;

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
