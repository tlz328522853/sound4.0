package com.sdcloud.web.lar.util;

import com.sdcloud.api.lar.annotations.ExcelField;
import com.sdcloud.api.lar.entity.BaseEntity;
import com.sdcloud.api.lar.entity.ShipmentShop;
import org.apache.poi.hssf.record.PageBreakRecord;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 导出excel工具类
 * Created by 韩亚辉 on 2016/3/21.
 */
public class ExportExcelUtils<T> {
    private Workbook workbook;
    private Sheet sheet;

    /**
     * @param sheetName sheet页名称
     */
    public ExportExcelUtils(String sheetName) {
        this.initialize(sheetName);
    }

    /**
     * 创建workbook对象
     *
     * @param sheetName
     */
    private void initialize(String sheetName) {
        workbook = new SXSSFWorkbook();
        sheet = workbook.createSheet(sheetName);
        //导出文件 压缩比例设置
        ZipSecureFile.setMinInflateRatio(0.0001);
    }

    /**
     * 设置表头
     *
     * @param totalTitle
     * @param size
     */
    private void totalTitle(String totalTitle, int size) {
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
    private void title(List<String> titles) {
        Row row = sheet.createRow(1);
        for (int i = 0; i < titles.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(titles.get(i));
        }
    }

    /**
     * @param totalTitle 总标题
     * @param contents   内容
     * @return
     * @throws IllegalAccessException
     */
    public Workbook writeContents(String totalTitle, List<T> contents) throws IllegalAccessException {
        List<String> titles = new ArrayList<>();
        this.totalTitle(totalTitle, this.fieldSize(contents, titles));
        this.title(titles);
        List<List<Object>> list = this.contentsConvert(contents);
        for (int i = 0; i < list.size(); i++) {
            Row row = sheet.createRow(i + 2);
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
        return workbook;
    }

    /**
     * 把数据转成list
     *
     * @param contents
     * @return
     * @throws IllegalAccessException
     */
    private List<List<Object>> contentsConvert(List<T> contents) throws IllegalAccessException {
        List<List<Object>> result = new ArrayList<>();
        for (T t : contents) {
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
     * @param contents
     * @return
     */
    private int fieldSize(List<T> contents, List<String> titles) {
        int size = 0;
        titles.add("序号");
        for (T t : contents) {
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
