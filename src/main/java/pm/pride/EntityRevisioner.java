package pm.pride;

import java.sql.SQLException;

public final class EntityRevisioner {

    public static void revisionEntity(RecordDescriptor red, Object entity) throws SQLException {
        if (red instanceof RevisionedRecordDescriptor) {
            RecordDescriptor recordDescriptorForRevisioning = ((RevisionedRecordDescriptor) red).getRevisioningRecordDescriptor();
            getDatabase(red).createRecord(recordDescriptorForRevisioning, entity, null);
        }
    }

    protected static Database getDatabase(RecordDescriptor red) {
        return DatabaseFactory.getDatabase(red.getContext());
    }
}
