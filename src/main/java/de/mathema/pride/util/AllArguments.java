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
package de.mathema.pride.util;

import de.mathema.pride.MappedObject;
import de.mathema.pride.RecordDescriptor;

/**
 * <p>Header: DMD3000 Framework Business Components</p>
 * <p>Description:
 * PriDE database adapter for Oracle's ALL_ARGUMENTS table which
 * contains meta data about stored procedures and is therefore
 * assential input for the {@link StoredProcedureGenerator}
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Organisation: arvato systems</p>
 * @author Jan Lessner / Christoph Apke
 * @version 1.0
 */
public class AllArguments extends MappedObject
{
    protected static RecordDescriptor red = new RecordDescriptor
        (AllArguments.class, "ALL_ARGUMENTS", null, new String[][] {
            { "owner",   "getOwner",   "setOwner" },
            { "object_name",   "getObjectName",   "setObjectName" },
            { "package_name",   "getPackageName",   "setPackageName" },
            { "object_id",   "getObjectId",   "setObjectId" },
            { "overload",   "getOverload",   "setOverload" },
            { "argument_name",   "getArgumentName",   "setArgumentName" },
            { "position",   "getPosition",   "setPosition" },
            { "sequence",   "getSequence",   "setSequence" },
            { "data_level",   "getDataLevel",   "setDataLevel" },
            { "data_type",   "getDataType",   "setDataType" },
            { "default_length",   "getDefaultLength",   "setDefaultLength" },
            { "in_out",   "getInOut",   "setInOut" },
            { "data_length",   "getDataLength",   "setDataLength" },
            { "data_precision",   "getDataPrecision",   "setDataPrecision" },
            { "data_scale",   "getDataScale",   "setDataScale" },
            { "radix",   "getRadix",   "setRadix" },
            { "character_set_name",   "getCharacterSetName",   "setCharacterSetName" },
            { "type_owner",   "getTypeOwner",   "setTypeOwner" },
            { "type_name",   "getTypeName",   "setTypeName" },
            { "type_subname",   "getTypeSubname",   "setTypeSubname" },
            { "type_link",   "getTypeLink",   "setTypeLink" },
            { "pls_type",   "getPlsType",   "setPlsType" },
        });
    protected RecordDescriptor getDescriptor() { return red; }

    private static String[] primaryKey = new String[] { "object_name", "package_name" };
    public String[] getKeyFields() { return primaryKey; }

    // Data members
    private String owner;
    private String objectName;
    private String packageName;
    private long objectId;
    private String overload;
    private String argumentName;
    private long position;
    private long sequence;
    private long dataLevel;
    private String dataType;
    private Long defaultLength;
    private String inOut;
    private Long dataLength;
    private Long dataPrecision;
    private Long dataScale;
    private Long radix;
    private String characterSetName;
    private String typeOwner;
    private String typeName;
    private String typeSubname;
    private String typeLink;
    private String plsType;

    // Read access functions
    public String getOwner()   { return owner; }
    public String getObjectName()   { return objectName; }
    public String getPackageName()   { return packageName; }
    public long getObjectId()   { return objectId; }
    public String getOverload()   { return overload; }
    public String getArgumentName()   { return argumentName; }
    public long getPosition()   { return position; }
    public long getSequence()   { return sequence; }
    public long getDataLevel()   { return dataLevel; }
    public String getDataType()   { return dataType; }
    public Long getDefaultLength()   { return defaultLength; }
    public String getInOut()   { return inOut; }
    public Long getDataLength()   { return dataLength; }
    public Long getDataPrecision()   { return dataPrecision; }
    public Long getDataScale()   { return dataScale; }
    public Long getRadix()   { return radix; }
    public String getCharacterSetName()   { return characterSetName; }
    public String getTypeOwner()   { return typeOwner; }
    public String getTypeName()   { return typeName; }
    public String getTypeSubname()   { return typeSubname; }
    public String getTypeLink()   { return typeLink; }
    public String getPlsType()   { return plsType; }

    // Write access functions
    public void setOwner(String owner) { this.owner = owner; }
    public void setObjectName(String objectName) { this.objectName = objectName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public void setObjectId(long objectId) { this.objectId = objectId; }
    public void setOverload(String overload) { this.overload = overload; }
    public void setArgumentName(String argumentName) { this.argumentName = argumentName; }
    public void setPosition(long position) { this.position = position; }
    public void setSequence(long sequence) { this.sequence = sequence; }
    public void setDataLevel(long dataLevel) { this.dataLevel = dataLevel; }
    public void setDataType(String dataType) { this.dataType = dataType; }
    public void setDefaultLength(Long defaultLength) { this.defaultLength = defaultLength; }
    public void setInOut(String inOut) { this.inOut = inOut; }
    public void setDataLength(Long dataLength) { this.dataLength = dataLength; }
    public void setDataPrecision(Long dataPrecision) { this.dataPrecision = dataPrecision; }
    public void setDataScale(Long dataScale) { this.dataScale = dataScale; }
    public void setRadix(Long radix) { this.radix = radix; }
    public void setCharacterSetName(String characterSetName) { this.characterSetName = characterSetName; }
    public void setTypeOwner(String typeOwner) { this.typeOwner = typeOwner; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
    public void setTypeSubname(String typeSubname) { this.typeSubname = typeSubname; }
    public void setTypeLink(String typeLink) { this.typeLink = typeLink; }
    public void setPlsType(String plsType) { this.plsType = plsType; }

    public String toString() { return super.toString(); }
}
