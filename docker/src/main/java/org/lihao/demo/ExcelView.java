package org.lihao.demo;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class ExcelView<T> extends AbstractExcelView {

	private String fileName;
	private List<T> dataList;
	private String[] showName;
	private String[] fieldName;

	private boolean serialNumFlag = false;

	public ExcelView() {
	}

	public ExcelView(String fileName, List<T> dataList, String[] showName, String[] fieldName) {
		this.fileName = fileName;
		this.dataList = dataList;
		this.showName = showName;
		this.fieldName = fieldName;
	}

	public void setSerialNumFlag(boolean serialNumFlag) {
		this.serialNumFlag = serialNumFlag;
	}

	private static Logger logger = LoggerFactory.getLogger(ExcelView.class);

	@Override
	protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) {

		OutputStream os = null;
		try {

			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(this.fileName, "UTF-8") + ".xls");
			os = response.getOutputStream();

			HSSFSheet sheet = workbook.createSheet(this.fileName);
			PoiExcelUtils.addTitle(sheet, this.showName, this.fileName, getHeader(workbook), getContext(workbook), this.serialNumFlag);
			PoiExcelUtils.addContextByList(sheet, this.dataList, this.fieldName, getContext(workbook), this.serialNumFlag);
			workbook.write(os);
		} catch (Throwable e) {
			logger.error("资产信息导出出错：" + e.getMessage(), e);
		} finally {
			try {
				os.flush();
				os.close();
			} catch (Throwable e) {
				logger.error("资产信息导出Excel出错：" + e.getMessage(), e);
			}
		}
	}

	//标题样式
	public static HSSFCellStyle getHeader(HSSFWorkbook workbook) {
		HSSFCellStyle format = workbook.createCellStyle();
		HSSFFont font = workbook.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);  //加粗
		font.setFontName("黑体");
		font.setFontHeightInPoints((short) 16);
		format.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		format.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		format.setFont(font);
		return format;
	}

	//内容样式
	public static HSSFCellStyle getContext(HSSFWorkbook workbook) {
		HSSFCellStyle format = workbook.createCellStyle();
		HSSFFont font = workbook.createFont();
		font.setFontName("宋体");
		format.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		format.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		format.setFont(font);
		return format;
	}

}