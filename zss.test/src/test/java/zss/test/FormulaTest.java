package zss.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;

import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.hamcrest.number.IsCloseTo;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.zkoss.poi.ss.usermodel.Cell;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zss.api.Range;
import org.zkoss.zss.api.Ranges;
import org.zkoss.zss.api.model.CellData.CellType;
import org.zkoss.zss.api.model.Sheet;
import org.zkoss.zss.model.sys.XSheet;
import org.zkoss.zss.ui.Spreadsheet;
import org.zkoss.zss.ui.impl.XUtils;

/**
 * For testing formulas.
 * 
 * @author Hawk
 *
 */
public class FormulaTest extends SpreadsheetTestCaseBase{

	private DesktopAgent desktop; 
	private ComponentAgent zss ;
	private Spreadsheet spreadsheet ;
	
	// after reading the number of row without a formula, the program will stop to read more rows
	private static final int MAX_ROW_READ_FROM_LAST_FORUMULA = 30;
	
	@Rule
    public ErrorCollector collector = new ErrorCollector();
	
	protected FormulaTest(String testPage){
		desktop = Zats.newClient().connect(testPage);
		
		zss = desktop.query("spreadsheet");
		spreadsheet = zss.as(Spreadsheet.class);
		
	}
	
	public FormulaTest(){
		this("/formula.zul");
	}
	

	@Test
	public void testMath() {
		Sheet sheet = spreadsheet.getBook().getSheetAt(2);
		
		int nFormula = verifyFormulaResult(sheet);
//		int nFormula = testFormulaByPoiInSheet(sheet);
		System.out.println("tested "+nFormula+ " formulas in sheet: "+sheet.getSheetName());
	}
	
	@Test
	public void testLogical() {
		Sheet sheet = spreadsheet.getBook().getSheetAt(3); 
		
		int nFormula = verifyFormulaResult(sheet);
//		int nFormula = testFormulaByPoiInSheet(sheet);
		System.out.println("tested "+nFormula+ " formulas in sheet: "+sheet.getSheetName());
	}
	
	@Test
	public void testText() {
		Sheet sheet = spreadsheet.getBook().getSheetAt(4); 
		
		int nFormula = verifyFormulaResult(sheet);
//		int nFormula = testFormulaByPoiInSheet(sheet);
		System.out.println("tested "+nFormula+ " formulas in sheet: "+sheet.getSheetName());
	}
	
	@Test
	public void testInfo() {
		Sheet sheet = spreadsheet.getBook().getSheetAt(5); 
		
		int nFormula = verifyFormulaResult(sheet);
//		int nFormula = testFormulaByPoiInSheet(sheet);
		System.out.println("tested "+nFormula+ " formulas in sheet: "+sheet.getSheetName());
	}

	@Test
	public void testDateTime() {
		Sheet sheet = spreadsheet.getBook().getSheetAt(6); 
		
		int nFormula = verifyFormulaResult(sheet);
//		int nFormula = testFormulaByPoiInSheet(sheet);
		System.out.println("tested "+nFormula+ " formulas in sheet: "+sheet.getSheetName());
	}
	
	@Test
	public void testFinancial() {
		Sheet sheet = spreadsheet.getBook().getSheetAt(7); 
		
		int nFormula = verifyFormulaResult(sheet);
//		int nFormula = testFormulaByPoiInSheet(sheet);
		System.out.println("tested "+nFormula+ " formulas in sheet: "+sheet.getSheetName());
	}
	
	@Test
	public void testStatistical() {
		Sheet sheet = spreadsheet.getBook().getSheetAt(8); 
		
		int nFormula = verifyFormulaResult(sheet);
//		int nFormula = testFormulaByPoiInSheet(sheet);
		System.out.println("tested "+nFormula+ " formulas in sheet: "+sheet.getSheetName());
	}	

	@Test
	public void testEngineering() {
		Sheet sheet = spreadsheet.getBook().getSheetAt(9); 
		
		int nFormula = verifyFormulaResult(sheet);
//		int nFormula = testFormulaByPoiInSheet(sheet);
		System.out.println("tested "+nFormula+ " formulas in sheet: "+sheet.getSheetName());
	}	
	
	@Test
	public void testUnsupported() {
		Sheet sheet = spreadsheet.getBook().getSheetAt(10); 
		
		int nFormula = verifyFormulaResult(sheet);
//		int nFormula = testFormulaByPoiInSheet(sheet);
		System.out.println("tested "+nFormula+ " formulas in sheet: "+sheet.getSheetName());
	}	
	
	@Test
	public void testCustom() {
		Sheet sheet = spreadsheet.getBook().getSheetAt(11); 
		
		int nFormula = verifyFormulaResult(sheet);
//		int nFormula = testFormulaByPoiInSheet(sheet);
		System.out.println("tested "+nFormula+ " formulas in sheet: "+sheet.getSheetName());
	}	
	

	@Test
	public void testMissingArguments(){
		SpreadsheetAgent ssAgent = new SpreadsheetAgent(zss);
		Sheet sheet = spreadsheet.getBook().getSheetAt(0); 

		//required arguments
		ssAgent.edit(0,1, "=ISEVEN()");
		assertEquals("#VALUE!", Ranges.range(sheet, 0, 1).getCellData().getFormatText());
		
		//missing all
		ssAgent.edit(0,2, "=OCT2BIN()");
		assertEquals("#VALUE!", Ranges.range(sheet, 0, 2).getCellData().getFormatText());
		
		//missing one
		ssAgent.edit(0,2, "=OCT2BIN(,3)");
		assertEquals("#VALUE!", Ranges.range(sheet, 0, 2).getCellData().getFormatText());
		
		ssAgent.edit(0,2, "=OCT2BIN(3,)");
		assertEquals("#VALUE!", Ranges.range(sheet, 0, 2).getCellData().getFormatText());
		
		//missing with comma
		ssAgent.edit(0,2, "=OCT2BIN(,)");
		assertEquals("#VALUE!", Ranges.range(sheet, 0, 2).getCellData().getFormatText());
		

		//optional arguments
		//missing an optional argument in the middle
		ssAgent.edit(0, 0, "=PV(0.08/12,20*12,500, ,0)");
		assertEquals("#VALUE!", Ranges.range(sheet, 0, 0).getCellData().getFormatText());
		
	}
	
	//verify in text
	private int verifyFormulaResult(Sheet sheet) {
		int readRowFromLastFormula = 0; //reset after verifying a formula
		int nFormula = 0;
		int row = 0 ; //current working row
		while (!isNoMoreFormula(readRowFromLastFormula)){
			Range formulaCell = Ranges.range(sheet, row, 1);
			Range verifyCell = Ranges.range(sheet, row, 3);
			if (existFormula2Verify(verifyCell, formulaCell)){ 
				String evaluationResult = getEvaluationResult(formulaCell);
				
				//verify evaluation result upon supported or not
				if (verifyCell.getCellEditText().equals("unsupported")){
					collector.checkThat(getFailedReason(formulaCell),evaluationResult
							, equalTo("#NAME?"));
				}else if (verifyCell.getCellEditText().equals("invalid")){
					collector.checkThat(getFailedReason(formulaCell),evaluationResult
							, equalTo("#VALUE!"));
				}else{
					collector.checkThat(getFailedReason(formulaCell),evaluationResult
							, equalTo(getExpectedResult(sheet, row)));
				}
				
				nFormula ++;
				readRowFromLastFormula = 0;
			}
			//TODO verify total tested formula count
			row ++;
			readRowFromLastFormula++;
		}
		return nFormula;
	}

	private String getEvaluationResult(Range formulaCell) {
		try{
			return formulaCell.getCellData().getFormatText();
		}catch (Exception e) {
			return e.getMessage();
		}
	}

	private String getExpectedResult(Sheet sheet, int row) {
		return Ranges.range(sheet, row, 2).getCellData().getFormatText();
	}
	
	/*
	 * Determine this row has a formula to verify or not
	 */
	private boolean existFormula2Verify(Range verifyCell, Range formulaCell) {
		//skip "human check" cases
		return verifyCell != null  && (verifyCell.getCellData().getType() == CellType.FORMULA || "unsupported".equals(verifyCell.getCellEditText()))
				&& formulaCell!=null && formulaCell.getCellData().getType() == CellType.FORMULA;
	}


	private String getFailedReason(Range cell) {
		return cell.getCellEditText()+" failed, at row "+(cell.getRow()+1)+" in sheet: "+cell.getSheet().getSheetName();
	}


	private boolean isNoMoreFormula(int nRowFromLastFormula) {
		return nRowFromLastFormula > MAX_ROW_READ_FROM_LAST_FORUMULA;
	}
	
	
	
	//verify each formula according cell's value
	private int testFormulaByPoiInSheet(Sheet sheet) {
	
		XSheet xsheet = spreadsheet.getXBook().getWorksheet(sheet.getSheetName());
		//tolerance delta value between correct value and expected value for a double number
		double DELTA = 1e-5;
	
		int nRowFromLastFormula = 0; //reset when read a formula cell
		int nFormula = 0;
		int row = 0 ; //current working row
		while (!isNoMoreFormula(nRowFromLastFormula)){
			Cell formulaCell = XUtils.getCell(xsheet, row, 1);
			Cell expectedCell = XUtils.getCell(xsheet, row, 2);
			Cell verifyCell = XUtils.getCell(xsheet, row, 3);
			if (isFormulaRow(verifyCell)
					&& formulaCell!=null && formulaCell.getCellType() == Cell.CELL_TYPE_FORMULA){ 
				if (formulaCell.getCachedFormulaResultType() == Cell.CELL_TYPE_NUMERIC){
					collector.checkThat(getFailedReason(formulaCell),formulaCell.getNumericCellValue()
							, IsCloseTo.closeTo(expectedCell.getNumericCellValue(),DELTA));
				}else{
					collector.checkThat(getFailedReason(formulaCell),getCellValue(formulaCell), equalTo(getCellValue(expectedCell)));
				}
				nFormula ++;
				nRowFromLastFormula = 0;				
			}
			row ++;
			nRowFromLastFormula++;
		}
		return nFormula;
	}
	
	private boolean isFormulaRow(Cell verifyCell) {
		//skip "human check" cases
		return verifyCell != null  && verifyCell.getCellType() == Cell.CELL_TYPE_FORMULA;
	}    


	private String getFailedReason(Cell cell) {
		return cell.getCellFormula()+" failed, at row "+(cell.getRowIndex()+1)+" in sheet: "+cell.getSheet().getSheetName();
	}	
	
	private Object getCellValue(Cell cell) {
		int cellType = cell.getCellType() == Cell.CELL_TYPE_FORMULA ? cell.getCachedFormulaResultType():cell.getCellType();
		switch(cellType){
		case Cell.CELL_TYPE_NUMERIC:
			return cell.getNumericCellValue();
		case Cell.CELL_TYPE_STRING:
			return cell.getStringCellValue();
		case Cell.CELL_TYPE_BOOLEAN:
			return cell.getBooleanCellValue();
		case Cell.CELL_TYPE_ERROR:
			return cell.getErrorCellValue();
		default:
			return "";
		}
	}
	
	/*
	 * Some formulas that cannot pass the test by Ranges API will also failed the test by POI API only when those formulas
	 * are put in the "first" sheet.  If they are not in the "first" sheet, the error won't occur.
	 * The root cause is that those specific formulas cells' getCachedFormulaResultType() always return 
	 * CELL_TYPE_ERROR in the first sheet, but when those formulas are not put in first sheet, 
	 * getCachedFormulaResultType() returns correct type.
	 * 
	 * Perform testFirst() and testTemp() can reproduce the error mentioned above.
	 */
	/*
//	@Test
	public void testFirst() {
		Sheet sheet = spreadsheet.getSheet(0);
		int nFormula = testFormulaByPoiInSheet(sheet);
		System.out.println("tested "+nFormula+ " formulas in sheet: "+sheet.getSheetName());
	}
	
//	@Test
	public void testTemp() {
		Sheet sheet = spreadsheet.getSheet(1);
		int nFormula = testFormulaByPoiInSheet(sheet);
		System.out.println("tested "+nFormula+ " formulas in sheet: "+sheet.getSheetName());
	}
	
	
	
	

	
	 */


}
