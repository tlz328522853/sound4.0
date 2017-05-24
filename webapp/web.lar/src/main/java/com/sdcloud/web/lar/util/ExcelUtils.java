package com.sdcloud.web.lar.util;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.omg.CORBA.OBJ_ADAPTER;
import org.apache.poi.xssf.streaming.SXSSFFormulaEvaluator.SheetsFlushedException;

import com.sdcloud.api.lar.annotations.ExcelField;

/**
 * 导出excel工具类     用于导出多表
 * Created by 罗荣杰 on 2016/8/4.
 */
public class ExcelUtils {
    private Workbook workbook;
    private List<Sheet> sheets;

    /**
     * @param sheetName sheet页名称
     */
    public ExcelUtils(List<String> sheetNames) {
        this.initialize(sheetNames);
    }

    /**
     * 创建workbook对象
     *
     * @param sheetName
     */
    private void initialize(List<String> sheetNames) {
        workbook = new SXSSFWorkbook();
        sheets = new ArrayList<>();
        for(String sheetName:sheetNames){
        	sheets.add(workbook.createSheet(sheetName));
        }
        //导出文件 压缩比例设置
        ZipSecureFile.setMinInflateRatio(0.0001);
    }

    /**
     * 设置表头
     *
     * @param totalTitle
     * @param size
     */
    private void totalTitle(String totalTitle, int size ,Sheet sheet) {
        Row row = sheet.createRow(0);
        for (int i = 0; i < size; i++) {
            Cell cell = row.createCell(i);
            if (i == 0) {
                CellStyle cellStyle = workbook.createCellStyle();
                Font fontStyle = workbook.createFont();
                cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
                cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
                cellStyle.setFont(fontStyle);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(totalTitle);
            }
        }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, size));
    }

    /**
     * @param titles
     */
    private void title(List<String> titles,Sheet sheet) {
        Row row = sheet.createRow(1);
        for (int i = 0; i < titles.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(titles.get(i));
        }
    }

    /**
     * @param totalTitle 总标题
     * @param contects   内容
     * @return
     * @throws IllegalAccessException
     */
    public Workbook writeContents(List<String> totalTitles, List<List<? extends Object>> contects) throws IllegalAccessException {
    	for(int k=0;k<totalTitles.size();k++){
	        List<String> titles = new ArrayList<>();
	        this.totalTitle(totalTitles.get(k), this.fieldSize(contects.get(k), titles),sheets.get(k));
	        this.title(titles,sheets.get(k));
	        List<List<Object>> list = this.contentsConvert(contects.get(k));
	        for (int i = 0; i < list.size(); i++) {
	            Row row = sheets.get(k).createRow(i + 2);
	            //设置序号
	            Cell cell1 = row.createCell(0);
	            cell1.setCellValue(i + 1);
	            for (int j = 0; j < list.get(i).size(); j++) {
	                Object obj = list.get(i).get(j);
	                if (null != obj) {
	                    if (obj instanceof Date) {
	                        Date date = (Date) obj;
	                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	                        String dateStr = dateFormat.format(date);
	                        CellStyle cellStyle = workbook.createCellStyle();
	                        DataFormat dataFormat = workbook.createDataFormat();
	                        cellStyle.setDataFormat(dataFormat.getFormat("yyyy-MM-dd HH:mm:ss"));
	                        CellStyle cellStyle1 = workbook.createCellStyle();
	                        DataFormat dataFormat1 = workbook.createDataFormat();
	                        cellStyle1.setDataFormat(dataFormat1.getFormat("HH:mm"));
	                        Cell cell = row.createCell(j + 1);
	                        if (dateStr.contains("1970-")) {
	                            cell.setCellStyle(cellStyle1);
	                        } else {
	                            cell.setCellStyle(cellStyle);
	                        }
	                        cell.setCellValue((Date) obj);
	                    } else {
	                        Cell cell = row.createCell(j + 1);
	                        cell.setCellValue(obj + "");
	                    }
	                } else {
	                    Cell cell = row.createCell(j + 1);
	                    cell.setCellValue("");
	                }
	            }
	        }
        
        
    	}
        return workbook;
    }

    /**
     * 把数据转成list
     *
     * @param list2
     * @return
     * @throws IllegalAccessException
     */
    private List<List<Object>> contentsConvert(List<? extends Object> list2) throws IllegalAccessException {
        List<List<Object>> result = new ArrayList<>();
        for (Object t : list2) {
            Field[] fields = t.getClass().getDeclaredFields();
            Field[] superFields = t.getClass().getSuperclass().getDeclaredFields();
            List<Object> list = new ArrayList<>();
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                ExcelField annotation = fields[i].getAnnotation(ExcelField.class);
                if (null != annotation) {
                    list.add(fields[i].get(t));
                }
            }
            for (int i = 0; i < superFields.length; i++) {
                superFields[i].setAccessible(true);
                ExcelField annotation = superFields[i].getAnnotation(ExcelField.class);
                if (null != annotation) {
                    list.add(superFields[i].get(t));
                }
            }
            result.add(list);
        }
        return result;
    }

    /**
     * 获取需要导出的字段个数
     *
     * @param list
     * @return
     */
    private int fieldSize(List<? extends Object> list, List<String> titles) {
        int size = 0;
        titles.add("序号");
        for (Object t : list) {
            Field[] fields = t.getClass().getDeclaredFields();
            Field[] superFields = t.getClass().getSuperclass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                ExcelField annotation = fields[i].getAnnotation(ExcelField.class);
                if (null != annotation) {
                    size++;
                    titles.add(annotation.value());
                }
            }
            for (int i = 0; i < superFields.length; i++) {
                superFields[i].setAccessible(true);
                ExcelField annotation = superFields[i].getAnnotation(ExcelField.class);
                if (null != annotation) {
                    titles.add(annotation.value());
                    size++;
                }
            }
            break;
        }
        return size;
    }
}
