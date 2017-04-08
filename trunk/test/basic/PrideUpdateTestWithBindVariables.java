package basic;
import org.junit.Test;

/*******************************************************************************
 * Copyright (c) 2001-2007 The PriDE team and MATHEMA Software GmbH
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public
 * License (LGPL) which accompanies this distribution, and is available
 * at http://pride.sourceforge.net/LGPL.html
 * 
 * Contributors:
 *     Jan Lessner, MATHEMA Software GmbH - JUnit test suite
 *******************************************************************************/
import de.mathema.pride.DatabaseFactory;
import de.mathema.pride.WhereCondition;
import junit.framework.Assert;

/**
 * @author bart57
 *
 * Class to Test the Update-Behaviour of the PriDE-Framework
 */
public class PrideUpdateTestWithBindVariables extends AbstractPrideTest {

	PrideUpdateTest prideUpdateTest = new PrideUpdateTest();
	
	@Override
	public void setUp() throws Exception {
		prideUpdateTest.setUp();
		WhereCondition.setBindDefault(true);
	}

	@Override
	public void tearDown() {
		WhereCondition.setBindDefault(false);
	}
	
	@Test
	public void testUpdatePK() throws Exception{
		prideUpdateTest.testUpdatePK();
	}

	@Test
	public void testUpdateByExample() throws Exception{
		prideUpdateTest.testUpdateByExample();
	}

	@Test
	public void testUpdateFields() throws Exception{
		prideUpdateTest.testUpdateFields();
	}

	@Test
	public void testUpdateMultiple() throws Exception{
		prideUpdateTest.testUpdateMultiple();
	}

	@Test
	public void testUpdateWhere() throws Exception{
		prideUpdateTest.testUpdateWhere();
	}

}
