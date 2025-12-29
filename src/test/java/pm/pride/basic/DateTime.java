package pm.pride.basic;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import pm.pride.MappedObject;
import pm.pride.RecordDescriptor;

public class DateTime extends MappedObject {

  public static final String TABLE = "DATETIME_PRIDE_TEST";
  public static final String COL_RECORD_NAME = "RECORD_NAME";
  public static final String COL_TIME_PLAIN = "TIME_PLAIN";
  public static final String COL_TIME_AS_DATE = "TIME_AS_DATE";
  public static final String COL_DATE_PLAIN = "DATE_PLAIN";
  public static final String COL_DATE_AS_TIME = "DATE_AS_TIME";
  public static final String COL_DATE_AS_DATE = "DATE_AS_DATE";
  public static final String COL_DATE_AS_LOCAL_DATE = "DATE_AS_LOCAL_DATE";
  public static final String COL_DATE_AS_LOCAL_DATE_TIME = "DATE_AS_LOCAL_DATE_TIME";

  String recordName;
  Timestamp timePlain;
  java.util.Date timeAsDate;
  java.sql.Date datePlain;
  Timestamp dateAsTime;
  java.util.Date dateAsDate;
  LocalDate localDate;
  LocalDateTime localDateTime;

  public DateTime() {
  }

  public DateTime(DateTime dtref) throws SQLException {
    recordName = dtref.recordName;
    findByExample(COL_RECORD_NAME);
  }

  public DateTime(String recordName) {
    this.recordName = recordName;
  }

	public String getRecordName() { return recordName; }
	public Timestamp getTimePlain() { return timePlain; }
	public void setTimePlain(Timestamp timePlain) { this.timePlain = timePlain; }
	public java.util.Date getTimeAsDate() { return timeAsDate; }
	public void setTimeAsDate(java.util.Date timeAsDate) { this.timeAsDate = timeAsDate; }
	public java.sql.Date getDatePlain() { return datePlain; }
	public LocalDate getLocalDate() { return localDate; }
	public LocalDateTime getLocalDateTime() { return localDateTime; }

	public void setRecordName(String recordName) { this.recordName = recordName; }
	public void setDatePlain(java.sql.Date datePlain) { this.datePlain = datePlain; }
	public Timestamp getDateAsTime() { return dateAsTime; }
	public void setDateAsTime(Timestamp dateAsTime) { this.dateAsTime = dateAsTime; }
	public java.util.Date getDateAsDate() { return dateAsDate; }
	public void setDateAsDate(java.util.Date dateAsDate) { this.dateAsDate = dateAsDate; }
	public void setLocalDate(LocalDate localDate) { this.localDate = localDate; }
	public void setLocalDateTime(LocalDateTime localDateTime) { this.localDateTime = localDateTime; }

  protected static final RecordDescriptor red =
    new RecordDescriptor(DateTime.class, TABLE, null)
      .row(COL_RECORD_NAME, "getRecordName", "setRecordName")
      .row(COL_TIME_PLAIN, "getTimePlain", "setTimePlain")
      .row(COL_TIME_AS_DATE, "getTimeAsDate", "setTimeAsDate", Timestamp.class)
      .row(COL_DATE_PLAIN, "getDatePlain", "setDatePlain")
      .row(COL_DATE_AS_TIME, "getDateAsTime", "setDateAsTime")
      .row(COL_DATE_AS_DATE, "getDateAsDate", "setDateAsDate")
      .row(COL_DATE_AS_LOCAL_DATE, "getLocalDate", "setLocalDate")
      .row(COL_DATE_AS_LOCAL_DATE_TIME, "getLocalDateTime", "setLocalDateTime");

  public RecordDescriptor getDescriptor() {
    return red;
  }
}
