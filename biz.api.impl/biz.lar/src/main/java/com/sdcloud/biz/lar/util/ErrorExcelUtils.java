package com.sdcloud.biz.lar.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.sdcloud.api.lar.entity.DistributeExpress;
import com.sdcloud.api.lar.entity.TakingExpress;

/**
 * 快件揽收、派件接单导出错误的上传数据工具类
 * @author jzc
 * 2016年10月27日
 */
public class ErrorExcelUtils {
	
    private Workbook workbook;
    private Sheet sheet;
    private CellStyle cellStyle;
    private CellStyle redCellStyle;
    

    /**
     * @param sheetName sheet页名称
     */
    public ErrorExcelUtils(String sheetName) {
        this.initialize(sheetName);
    }

    /**
     * 创建workbook对象
     *
     * @param sheetName
     */
    private void initialize(String sheetName) {
        workbook = new SXSSFWorkbook(1000);
        sheet = workbook.createSheet(sheetName);
        sheet.setColumnWidth(0, 7*256);
        sheet.setColumnWidth(1, 25*256);
        sheet.setColumnWidth(2, 14*256);
        sheet.setColumnWidth(3, 14*256);
        sheet.setColumnWidth(4, 10*256);
        sheet.setColumnWidth(5, 19*256);
        sheet.setColumnWidth(6, 14*256);
        sheet.setColumnWidth(7, 14*256);
        sheet.setColumnWidth(8, 20*256);
        sheet.setColumnWidth(9, 20*256);
        sheet.setColumnWidth(10, 20*256);
        this.initCellStyle();
    }
    
    private void initCellStyle(){
    	cellStyle= workbook.createCellStyle();
        Font fontStyle = workbook.createFont();
        fontStyle.setFontName("黑体");
        fontStyle.setBoldweight(Font.BOLDWEIGHT_BOLD);
        fontStyle.setFontHeightInPoints((short) 12);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        cellStyle.setFont(fontStyle);
        
        redCellStyle= workbook.createCellStyle();
        Font rerFont= workbook.createFont();
        rerFont.setColor(Font.COLOR_RED);
        rerFont.setFontHeightInPoints((short) 10);
        redCellStyle.setFont(rerFont);
    }

    /**
     * @param contects 快件揽收 错误对象集合
     * @return
     * @throws IllegalAccessException
     */
    public InputStream writeExpressErrorData(List<TakingExpress.ExportBean> contects) throws IllegalAccessException {
    	for(int i=0;i<contects.size();i++){
    		Row row = sheet.createRow(i);// 得到行
    		Cell cell1 = i==0?titleCell(row,0):row.createCell(0);
            cell1.setCellValue(contects.get(i).no);
            
            Cell cell2 = i==0?titleCell(row,1):row.createCell(1);
            cell2.setCellValue(contects.get(i).orderNo);
            
            Cell cell3 = i==0?titleCell(row,2):row.createCell(2);
            cell3.setCellValue(contects.get(i).orgStr);
            
            Cell cell4 = i==0?titleCell(row,3):row.createCell(3);
            cell4.setCellValue(contects.get(i).expressStr);
            
            Cell cell5 = i==0?titleCell(row,4):row.createCell(4);
            cell5.setCellValue(contects.get(i).takingManStr);
            
            Cell cell6 = i==0?titleCell(row,5):row.createCell(5);
            cell6.setCellValue(contects.get(i).idNo);
            
            Cell cell7 = i==0?titleCell(row,6):row.createCell(6);
            cell7.setCellValue(contects.get(i).payWayStr);
            
            Cell cell8 = i==0?titleCell(row,7):row.createCell(7);
            cell8.setCellValue(contects.get(i).moneyStr);
            
            Cell cell9 = i==0?titleCell(row,8):row.createCell(8);
            cell9.setCellValue(contects.get(i).toPayStr);
            
            Cell cell10 = i==0?titleCell(row,9):row.createCell(9);
            cell10.setCellValue(contects.get(i).takingDateStr);
            
            Cell cell11 = i==0?titleCell(row,10):row.createCell(10);
            if(i==0){
            	cell11.setCellValue("错误提示");
            }
            else{
            	StringBuffer buf=new StringBuffer();
            	for(int k=0;k<contects.get(i).errMsgs.size();k++){
            		buf.append((k+1)+":"+contects.get(i).errMsgs.get(k));
            	}
            	cell11.setCellStyle(redCellStyle);
            	cell11.setCellValue(buf.toString());
            }
        
    	}
    	return writeAndReadFile(workbook);
    }
    
    
    /**
     * @param contects 派件接单 错误对象集合
     * @return
     * @throws IllegalAccessException
     */
    public InputStream writeDistributeErrorData(List<DistributeExpress.ExportBean> contects) throws IllegalAccessException {
    	for(int i=0;i<contects.size();i++){
    		Row row = sheet.createRow(i);// 得到行
    		Cell cell1 = i==0?titleCell(row,0):row.createCell(0);
            cell1.setCellValue(contects.get(i).no);
            
            Cell cell2 = i==0?titleCell(row,1):row.createCell(1);
            cell2.setCellValue(contects.get(i).orderNo);
            
            Cell cell3 = i==0?titleCell(row,2):row.createCell(2);
            cell3.setCellValue(contects.get(i).orgStr);
            
            Cell cell4 = i==0?titleCell(row,3):row.createCell(3);
            cell4.setCellValue(contects.get(i).expressStr);
            
            Cell cell5 = i==0?titleCell(row,4):row.createCell(4);
            cell5.setCellValue(contects.get(i).distributerStr);
            
            Cell cell6 = i==0?titleCell(row,5):row.createCell(5);
            cell6.setCellValue(contects.get(i).idNo);
            
            Cell cell7 = i==0?titleCell(row,6):row.createCell(6);
            cell7.setCellValue(contects.get(i).signStatusStr);
            
            Cell cell8 = i==0?titleCell(row,7):row.createCell(7);
            cell8.setCellValue(contects.get(i).toPayStr);
            
            Cell cell9 = i==0?titleCell(row,8):row.createCell(8);
            cell9.setCellValue(contects.get(i).signTimeStr);
            
            Cell cell10 = i==0?titleCell(row,9):row.createCell(9);
            if(i==0){
            	cell10.setCellValue("错误提示");
            }
            else{
            	StringBuffer buf=new StringBuffer();
            	for(int k=0;k<contects.get(i).errMsgs.size();k++){
            		buf.append((k+1)+":"+contects.get(i).errMsgs.get(k));
            	}
            	cell10.setCellStyle(redCellStyle);
            	cell10.setCellValue(buf.toString());
            }
        
    	}
        return writeAndReadFile(workbook);
    }
    
    /**
     * 创建标题列 加样式
     * @author jzc 2016年10月27日
     * @param row
     * @param cellNum
     * @return
     */
    private Cell titleCell(Row row,int cellNum){
    	row.setHeightInPoints(20);
    	Cell cell = row.createCell(cellNum);
        cell.setCellStyle(cellStyle);
        return cell;
    }
    
    private InputStream writeAndReadFile(Workbook workbook){
    	//创建临时文件
    	String path=ErrorExcelUtils.class.getResource("/").getFile().toString();
	    File filePath= new File(path);
	    String fileName="erro_"+System.currentTimeMillis();
        File tempFile=null;
		try {
			tempFile = File.createTempFile(fileName, ".xlsx", filePath);
		} catch (IOException e1) {
			System.out.println("----------创建临时文件("+fileName+".xlsx"+")失败...");
			e1.printStackTrace();
		}
        //往临时文件中写数据
		OutputStream os = null;  
		try{  
		   os = new FileOutputStream(tempFile);  
		   workbook.write(os);  
		   os.close(); //注意：如无此关闭语句，文件将不能删除
	       tempFile.deleteOnExit();
		}catch(Exception e){  
		   System.out.println("---------写入临时文件("+fileName+".xlsx"+")失败...");
		   e.printStackTrace();
		}  
		//获取临时文件流
		InputStream in=null;
        try {
        	in= new FileInputStream(tempFile);
		} catch (FileNotFoundException e) {
			System.out.println("----------获取临时文件流("+fileName+".xlsx"+")失败...");
			e.printStackTrace();
		}
        
        return in;
    }
}
