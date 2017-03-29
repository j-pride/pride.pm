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
package relation;

import java.sql.SQLException;
import de.mathema.pride.*;

public class Order extends MappedObject {
    protected static RecordDescriptor red = new RecordDescriptor
        (Order.class, "address", null, new String[][] {
            { "customer_id", "getCustomerId", "setCustomerId" },
            { "article_id",  "getArticleId",  "setArticleId" },
            { "amount",      "getAmount",     "setAmount" }
        });
	protected RecordDescriptor getDescriptor() { return red; }

    private int customerId;
    private int articleId;
    private int amount;

    public int getCustomerId() { return customerId; }
	public int getArticleId() { return articleId; }
	public int getAmount() { return amount; }

    public void setCustomerId(int val) { customerId = val; }
	public void setArticleId(int val) { articleId = val; }
	public void setAmount(int val) { amount = val; }
    
    public Order(int customerId) {
    	setCustomerId(customerId);
    }
}
