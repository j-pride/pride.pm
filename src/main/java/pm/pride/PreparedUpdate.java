/*******************************************************************************
 * Copyright (c) 2001-2007 The PriDE team and MATHEMA Software GmbH
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public
 * License (LGPL) which accompanies this distribution, and is available
 * at http://pride.sourceforge.net/LGPL.html
 * 
 * Contributors:
 *     Jan Lessner, MATHEMA Software GmbH - initial API and implementation
 *******************************************************************************/
package pm.pride;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
public class PreparedUpdate extends PreparedOperation
{
    protected String[] dbkeyfields;
    protected String[] updatefields;

	public PreparedUpdate(String[] dbkeyfields, String[] updatefields, RecordDescriptor red, boolean checkRevisioningIntegrity)
		throws SQLException, ReflectiveOperationException {
		super("update " + red.getTableName() + " set " +
			  red.getUpdateValues(null, dbkeyfields, updatefields, DatabaseFactory.getDatabase(red.getContext())) + " where " +
			  red.getConstraint(null, dbkeyfields, false, DatabaseFactory.getDatabase(red.getContext())), red);
		this.dbkeyfields = dbkeyfields;
		this.updatefields = updatefields;
		if (red.isRevisioned()) {
            RevisionedRecordDescriptor revisionedRed = (RevisionedRecordDescriptor) red;
            revisioningChecks(dbkeyfields, updatefields, checkRevisioningIntegrity, revisionedRed);
            revisioningPreparedInsert = new PreparedInsert(revisionedRed.getRevisioningRecordDescriptor());
		}
	}

    public PreparedUpdate(String[] dbkeyfields, String[] updatefields, RecordDescriptor red)
            throws SQLException, ReflectiveOperationException {
        this(dbkeyfields, updatefields, red, true);
    }

    public PreparedUpdate(String[] dbkeyfields, RecordDescriptor red, boolean checkRevisioningIntegrity)
            throws SQLException, ReflectiveOperationException {
        this(dbkeyfields, null, red, checkRevisioningIntegrity);
    }

	public PreparedUpdate(String[] dbkeyfields, RecordDescriptor red)
		throws SQLException, ReflectiveOperationException {
		this(dbkeyfields, red, true);
	}

    public PreparedUpdate(RecordDescriptor red)
    	throws SQLException, ReflectiveOperationException {
    	this(red.getPrimaryKeyFields(), red, false);
    }

	public void setParameters(Object obj) throws SQLException {
		try {
			int position = red.getUpdateValues(obj, dbkeyfields, updatefields, this, null, 1);
			red.getConstraint(obj, dbkeyfields, this, null, position);
		}
		catch(Exception x) { db.processSevereButSQLException(x); }
	}

    protected void revisioningChecks(String[] dbkeyfields, String[] updatefields, boolean checkRevisioningIntegrity, RevisionedRecordDescriptor revisionedRed) {
        if (checkRevisioningIntegrity) {
            if (!updateFieldsContainAllRevisioningFields(revisionedRed, updatefields)) {
                throw new BatchUpdateRevisioningException("UpdateFields are not a subset of revisioning fields. Revisioning won't work. " +
                        "With the update fields provided, the revisioned entity will not be complete. " +
                        "Turn off revisioning for this PreparedUpdate and do revisioning manually.");
            }
            if (!constructorKeyFieldsMatchRecordDescriptorKeyFields(revisionedRed, dbkeyfields)) {
                throw new BatchUpdateRevisioningException("PreparedUpdate keyFields don't match keyField of RevisionedRecordDescriptor. Revisioning won't work. " +
                        "PreparedUpdate potentially addresses multiple entities with every execution. " +
                        "Turn off revisioning for this PreparedUpdate and do revisioning manually.");
            }
        }
    }

    protected boolean updateFieldsContainAllRevisioningFields(RevisionedRecordDescriptor red, String[] updatefields) {
	    if (updatefields == null) // null addresses all fields for update which of course includes all revisioning fields
	        return true;
        List<String> redAttributesForRevisioning = red
                .extractRawAttributeMapForRevisioning()
                .stream()
                .map(element -> element[0])
                .collect(Collectors.toList());
        redAttributesForRevisioning.removeAll(Arrays.asList(red.getPrimaryKeyFields()));
        List<String> updateFieldsList = Arrays.asList(updatefields);
        return redAttributesForRevisioning.stream().allMatch(attribute -> updateFieldsList.contains(attribute));
    }

    protected boolean constructorKeyFieldsMatchRecordDescriptorKeyFields(RevisionedRecordDescriptor red, String[] dbkeyfields) {
        List<String> redKeyFields = Arrays.asList(red.getPrimaryKeyFields());
        List<String> constructorKeyFields = Arrays.asList(dbkeyfields);
        return constructorKeyFields.stream().allMatch((keyField) -> redKeyFields.contains(keyField)) &&
                redKeyFields.stream().allMatch((redKeyField) -> constructorKeyFields.contains(redKeyField));
    }
}