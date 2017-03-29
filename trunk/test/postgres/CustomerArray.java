package postgres;

import java.math.BigDecimal;
import java.util.Date;

import de.mathema.pride.RecordDescriptor;

import basic.CustomerType;
import basic.IdentifiedEntity;

public class CustomerArray extends IdentifiedEntity {
    String[] permissions;
    int[] logintimes;
    Date[] logindates;
    BigDecimal[] turnovers;
    CustomerType types[];
    
    public String[] getPermissions() { return permissions; }
    public void setPermissions(String[] permissions) { this.permissions = permissions; }
    public int[] getLogintimes() { return logintimes; }
    public void setLogintimes(int[] logintimes) { this.logintimes = logintimes; }    
    public Date[] getLogindates() { return logindates; }
    public void setLogindates(Date[] logindates) { this.logindates = logindates; }
    public BigDecimal[] getTurnovers() { return turnovers; }
    public void setTurnovers(BigDecimal[] turnovers) { this.turnovers = turnovers; }
    public CustomerType[] getTypes() { return types; }
    public void setTypes(CustomerType[] types) { this.types = types; }
    
    public CustomerArray(int id) { super(id); }

    protected static RecordDescriptor red =
            new RecordDescriptor(CustomerArray.class, PostgresArrayTest.ARRAY_TEST_TABLE,
                    IdentifiedEntity.red, new String[][] {
                { "permissions", "getPermissions", "setPermissions" },
                { "logintimes", "getLogintimes", "setLogintimes" },
                { "logindates", "getLogindates", "setLogindates" },
                { "turnovers", "getTurnovers", "setTurnovers" },
                { "types", "getTypes", "setTypes" }
            }
        );

    protected RecordDescriptor getDescriptor() { return red; }


}
