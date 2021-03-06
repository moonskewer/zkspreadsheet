/* ActiveRangeHelper.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jan 30, 2012 3:44:46 PM , Created by sam
}}IS_NOTE

Copyright (C) 2012 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zss.ui.impl;

import java.util.HashMap;

import org.zkoss.zss.api.AreaRef;
import org.zkoss.zss.model.sys.XSheet;

/**
 * @author sam
 *
 */
public class ActiveRangeHelper {

	private HashMap<XSheet, AreaRef> activeRanges = new HashMap<XSheet, AreaRef>();
	
	public void setActiveRange(XSheet sheet, int tRow, int lCol, int bRow, int rCol) {
		AreaRef rect = activeRanges.get(sheet);
		if (rect == null) {
			activeRanges.put(sheet, rect = new AreaRef(tRow, lCol, bRow, rCol));
		} else {
			rect.setArea(tRow, lCol, bRow, rCol);
		}
	}
	
	public AreaRef getArea(XSheet sheet) {
		return activeRanges.get(sheet);
	}
	
	public boolean containsSheet(XSheet sheet) {
		return activeRanges.containsKey(sheet);
	}
	
	public boolean contains(XSheet sheet, int row, int col) {
		return contains(sheet, row, col, row, col);
	}
	
	public boolean contains(XSheet sheet, int tRow, int lCol, int bRow, int rCol) {
		AreaRef rect = activeRanges.get(sheet);
		if (rect == null)
			return false;
		return rect.contains(tRow, lCol, bRow, rCol);
	}
}
