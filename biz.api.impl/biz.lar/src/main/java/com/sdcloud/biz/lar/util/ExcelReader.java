package com.sdcloud.biz.lar.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.sdcloud.api.lar.entity.DistributeExpress;
import com.sdcloud.api.lar.entity.TakingExpress;
import com.sdcloud.api.lar.service.IRowReader;

	/** 
     * 抽象Excel2007读取器，excel2007的底层数据结构是xml文件，采用SAX的事件驱动的方法解析 
     * xml，需要继承DefaultHandler，在遇到文件内容时，事件会触发，这种做法可以大大降低 
     * 内存的耗费，特别使用于大数据量的文件。 
     * 
     */  
    public class ExcelReader extends DefaultHandler {  
    	
    	/**
         * 单元格中的数据可能的数据类型
         */
        enum CellDataType{
            BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER, DATE, NULL
        }
    	
    	//共享字符串表
        private SharedStringsTable sst;
        //上一次的内容
        private String lastContents;
        //字符串标识
        private boolean nextIsString;
        //工作表索引
        private int sheetIndex = -1;
    	//结果集
        private List<DistributeExpress.ExportBean> distributeBean = new ArrayList<>();
        
        private List<TakingExpress.ExportBean> takingBean = new ArrayList<>();
        //业务类型
        private int type;
        //当前行  
        private int curRow = 0;  
        //当前元素的位置，可处理空单元格
        private String curRef = null;
        //当前行的数据集合
        private Map<String, String> rowData = new HashMap<>();
        //是否为空单元格
        private boolean cellNull;
        
        private final DataFormatter formatter = new DataFormatter();
        
        private short formatIndex;
     
        private String formatString;
        
        //单元格
        private StylesTable stylesTable;
        //Excel数据逻辑处理
        private IRowReader row;
        //单元格数据类型，默认为字符串类型
        private CellDataType nextDataType = CellDataType.SSTINDEX; 
        
        public ExcelReader(){}
        
        public ExcelReader(IRowReader row, int type){
        	this.row = row;
        	this.type = type;
        }
          
        public List<DistributeExpress.ExportBean> getDistributeBean() {
			return distributeBean;
		}

		public void setDistributeBean(List<DistributeExpress.ExportBean> distributeBean) {
			this.distributeBean = distributeBean;
		}

		public List<TakingExpress.ExportBean> getTakingBean() {
			return takingBean;
		}

		public void setTakingBean(List<TakingExpress.ExportBean> takingBean) {
			this.takingBean = takingBean;
		}

		/**只遍历一个电子表格，其中sheetId为要遍历的sheet索引，从1开始，1-3 
         * @param filename 
         * @param sheetId 
         * @throws Exception 
         */  
        public void processOneSheet(String filename,int sheetId) throws Exception {  
            OPCPackage pkg = OPCPackage.open(filename);  
            XSSFReader r = new XSSFReader(pkg); 
            stylesTable = r.getStylesTable();  
            SharedStringsTable sst = r.getSharedStringsTable();  
            XMLReader parser = fetchSheetParser(sst);  
            // 根据 rId# 或 rSheet# 查找sheet  
            InputStream sheet2 = r.getSheet("rId"+sheetId);  
            sheetIndex++;  
            InputSource sheetSource = new InputSource(sheet2);  
            parser.parse(sheetSource);  
            sheet2.close();  
        }  
      
        /** 
         * 遍历工作簿中所有的电子表格 
         * @param is  文件流 
         * @throws Exception 
         */  
        public void process(InputStream is) throws Exception {  
            OPCPackage pkg = OPCPackage.open(is);  
            XSSFReader r = new XSSFReader(pkg); 
            stylesTable = r.getStylesTable();
            SharedStringsTable sst = r.getSharedStringsTable();  
            XMLReader parser = fetchSheetParser(sst);  
            Iterator<InputStream> sheets = r.getSheetsData(); 
            
            while (sheets.hasNext()) {  
                curRow = 0;  
                sheetIndex++;  
                InputStream sheet = sheets.next();  
                InputSource sheetSource = new InputSource(sheet);  
                parser.parse(sheetSource);  
                sheet.close();
                break;
            }  
        }  
      
        public XMLReader fetchSheetParser(SharedStringsTable sst)  
                throws SAXException {  
            XMLReader parser = XMLReaderFactory  
                    .createXMLReader();  
            this.sst = sst;  
            parser.setContentHandler(this);  
            return parser;  
        }  
      
        public void startElement(String uri, String localName, String name,  
                Attributes attributes) throws SAXException {  
              
        	// c => 单元格  
            if ("c".equals(name)) {  
                // 如果下一个元素是 SST 的索引，则将nextIsString标记为true  
                String cellType = attributes.getValue("t");
                
                curRef = attributes.getValue("r");
                if ("s".equals(cellType)) {  
                    nextIsString = true;  
                    cellNull = false;  
                }else {  
                    nextIsString = false;  
                    cellNull = true;  
                }
                // 设定单元格类型
                this.setNextDataType(attributes);
            }  
            // 置空  
            lastContents = ""; 
            
        }  
      
        public void endElement(String uri, String localName, String name)  
                throws SAXException {  
        	// 根据SST的索引值的到单元格的真正要存储的字符串  
            // 这时characters()方法可能会被调用多次  
            if (nextIsString) {  
                try {  
                    int idx = Integer.parseInt(lastContents);  
                    lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();  
                } catch (Exception e) {  
      
                }  
            }  
            if ("v".equals(name) || "t".equals(name)) {
            	
            	// v => 单元格的值，如果单元格是字符串则v标签的值为该字符串在SST中的索引
                String value = this.getDataValue(lastContents.trim(), "");
                
                value = value.equals("") ? " " : value; 
                rowData.put(curRef, value);
                cellNull = false;  
            }else if("c".equals(name) && cellNull == true){  
            	rowData.put(curRef, "");
                cellNull = false;  
            }else {  
                // 如果标签名称为 row ，这说明已到行尾，调用 optRows() 方法  
                if (name.equals("row")) {
                	if(type == 0){
                		row.takingResult(curRow, rowData, takingBean); 
                	}
            		if(type == 1){
            			row.distributeResult(curRow, rowData, distributeBean); 
                	}
            		rowData.clear();  
                    curRow++;  
                }  
            }
        	
        }  
      
        public void characters(char[] ch, int start, int length)  
                throws SAXException {  
            //得到单元格内容的值  
            lastContents += new String(ch, start, length);  
        }  
        
        @Override
        public void endDocument() throws SAXException {
        	super.endDocument();
	        //文档结束后处理业务逻辑
        	
        }
        /**
         * 处理数据类型
         * 
         * @param attributes
         */
        public void setNextDataType(Attributes attributes){
            nextDataType = CellDataType.NUMBER;
            formatIndex = -1;
            formatString = null;
            String cellType = attributes.getValue("t");
            String cellStyleStr = attributes.getValue("s");
     
            if ("b".equals(cellType)){
                nextDataType = CellDataType.BOOL;
            }else if ("e".equals(cellType)){
                nextDataType = CellDataType.ERROR;
            }else if ("inlineStr".equals(cellType)){
                nextDataType = CellDataType.INLINESTR;
            }else if ("s".equals(cellType)){
                nextDataType = CellDataType.SSTINDEX;
            }else if ("str".equals(cellType)){
                nextDataType = CellDataType.FORMULA;
            }
     
            if (cellStyleStr != null) {
                int styleIndex = Integer.parseInt(cellStyleStr);
                XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
                formatIndex = style.getDataFormat();
                formatString = style.getDataFormatString();
     
                if ("m/d/yy h:mm" == formatString ){
                    nextDataType = CellDataType.DATE;
                    formatString = "yyyy/MM/dd hh:mm:ss";
                }
                if ("m/d/yy" == formatString){
                    nextDataType = CellDataType.DATE;
                    formatString = "yyyy/MM/dd";
                }
                if (formatString == null){
                    nextDataType = CellDataType.NULL;
                    formatString = BuiltinFormats.getBuiltinFormat(formatIndex);
                }
            }
        }
        /**
         * 对解析出来的数据进行类型处理
         * 
         * @param value
         *            单元格的值（这时候是一串数字）
         * @param thisStr
         *            一个空字符串
         * @return
         */
        public String getDataValue(String value, String thisStr){
            switch (nextDataType)
            {
                // 这几个的顺序不能随便交换，交换了很可能会导致数据错误
                case BOOL:
                    char first = value.charAt(0);
                    thisStr = first == '0' ? "FALSE" : "TRUE";
                    break;
                case ERROR:
                    thisStr = "\"ERROR:" + value.toString() + '"';
                    break;
                case FORMULA:
                    thisStr = '"' + value.toString() + '"';
                    break;
                case INLINESTR:
                    XSSFRichTextString rtsi = new XSSFRichTextString(value.toString());
     
                    thisStr = rtsi.toString();
                    rtsi = null;
                    break;
                case SSTINDEX:
                    //String sstIndex = value.toString();
                    thisStr = value.toString();
                    /*try
                    {
                        int idx = Integer.parseInt(sstIndex);
                        XSSFRichTextString rtss = new XSSFRichTextString(sst.getEntryAt(idx));
                        thisStr = rtss.toString();
                        rtss = null;
                    }
                    catch (Exception ex)
                    {
                        thisStr = value.toString();
                    }*/
                    break;
                case NUMBER:
                    if (formatString != null){
                        thisStr = formatter.formatRawCellContents(Double.parseDouble(value), formatIndex, formatString)
                                .trim();
                    }else {
                        thisStr = value;
                    }
     
                    thisStr = thisStr.replace("_", "").trim();
                    break;
                case DATE:
                    thisStr = formatter.formatRawCellContents(Double.parseDouble(value), formatIndex, formatString);
                    break;
                default:
                    thisStr = " ";
     
                    break;
            }
     
            return thisStr;
        }
        
}