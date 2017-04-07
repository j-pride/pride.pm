package de.mathema.pride;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PreparedSelect extends PreparedOperation {

    protected String[] dbkeyfields;

    public PreparedSelect(String[] dbkeyfields, RecordDescriptor red) throws SQLException, IllegalAccessException,
            InvocationTargetException {
        super(String.format("select %s from %s where %s",
                red.getResultFields(),
                red.getTableName(),
                red.getConstraint(null, dbkeyfields, false, DatabaseFactory.getDatabase(red.getContext()))),
            red);
        this.dbkeyfields = dbkeyfields;
    }

    @Override
    public int execute(Object obj) throws SQLException {
        throw new RuntimeException("Ausgeschlagenes Erbe: Rückgabetyp int ist für Selects nicht sinnvoll.");
    }

    @Override
    public int[] executeBatch() throws SQLException {
        throw new RuntimeException("Ausgeschlagenes Erbe: Rückgabetyp int[] ist für Selects nicht sinnvoll.");
    }

    public ResultIterator executeQuery(Object obj) throws SQLException {
        try {
            setParameters(obj);
            if (db.isLogging()) // Optimization to avoid string assembly if not required
                db.sqlLog(operation + " using " + obj.toString());
            ResultSet rs = stmt.executeQuery();
            ResultIterator ri = new ResultIterator(stmt, true, rs, obj, red, db, db.getConnection());
            if (!ri.next()) {
                ri.close();
                throw new NoResultsException(operation + " using " + obj.toString());
            }
            return ri;
        }
        catch(NoResultsException nrx) {
            throw nrx;
        }
        catch(SQLException sqlx) {
            db.sqlLogError(sqlx);
            throw sqlx;
        }
        finally {
            stmt.clearParameters();
        }
    }
    
    @Override
    public void setParameters(Object obj) throws SQLException {
        try {
            red.getConstraint(obj, dbkeyfields, this, null, 1);
        } catch (Exception x) {
            db.processSevereButSQLException(x);
        }
    }
}
