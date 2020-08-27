package com.xx.util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelExporter {
    private static final Logger logger = LoggerFactory.getLogger(ExcelExporter.class);
    private static Pattern PATTERN = Pattern.compile("^\\d+(\\.\\d+)?$");

    public static <T> void exportExcelExtended(String title, List<Object> headers, List<Object> attrNames,
                                               Collection<T> dataset, OutputStream out, String pattern) throws Exception {
        try (HSSFWorkbook workbook = new HSSFWorkbook()) {
            HSSFSheet sheet = workbook.createSheet(title);
            sheet.setDefaultColumnWidth(30);
            HSSFRow row = sheet.createRow(0);
            int headerColumnNum = 0;
            for (int i = 0; i < headers.size(); i++) {
                Object header = headers.get(i);
                if (header instanceof String) {
                    HSSFCell cell = row.createCell(headerColumnNum);
                    headerColumnNum++;
                    HSSFRichTextString text = new HSSFRichTextString((String) headers.get(i));
                    cell.setCellValue(text);
                } else if (header instanceof List) {
                    List<Object> subHeaders = (List) header;
                    for (Object one : subHeaders) {
                        HSSFCell cell = row.createCell(headerColumnNum);
                        headerColumnNum++;
                        HSSFRichTextString text = new HSSFRichTextString((String) one);
                        cell.setCellValue(text);
                    }
                }
            }

            Iterator<T> itor = dataset.iterator();
            int index = 0;
            while (itor.hasNext()) {
                index++;
                row = sheet.createRow(index);
                T t = (T) itor.next();
                int columnNum = 0;
                for (int j = 0; j < attrNames.size(); j++) {
                    if (attrNames.get(j) instanceof List) {
                        List subAttrNames = (List) attrNames.get(j);
                        String attrName = (String) subAttrNames.get(0);
                        String methodName = "get" + attrName.substring(0, 1).toUpperCase() + attrName.substring(1);
                        Object value = null;
                        try {
                            Method method = t.getClass().getMethod(methodName);
                            if (method != null) {
                                value = method.invoke(t);
                            }
                        } catch (Exception e) {
                            value = null;
                        }
                        if (value == null) {
                            continue;
                        }
                        if (!(value instanceof Map)) {
                            throw new Exception("Cannot convert " + attrName + " to Map");
                        }
                        Map valueMap = (Map) value;
                        for (int k = 1; k < subAttrNames.size(); k++) {
                            String subName = (String) subAttrNames.get(k);
                            Object subValue = valueMap.get(subName);
                            HSSFCell cell = row.createCell(columnNum);
                            columnNum++;
                            if (subValue != null) {
                                String subText = subValue.toString();
                                Matcher matcher = PATTERN.matcher(subText);
                                if (matcher.matches()) {
                                    cell.setCellValue(Double.parseDouble(subText));
                                } else {
                                    cell.setCellValue(new HSSFRichTextString(subText));
                                }
                            } else {
                                cell.setCellValue(new HSSFRichTextString(""));
                            }
                        }
                        continue;
                    }
                    String attrName = (String) attrNames.get(j);
                    String methodName = "get" + attrName.substring(0, 1).toUpperCase() + attrName.substring(1);
                    HSSFCell cell = row.createCell(columnNum);
                    columnNum++;
                    Object value = null;
                    try {
                        Method method = t.getClass().getMethod(methodName);
                        if (method != null) {
                            value = method.invoke(t);
                        }
                    } catch (Exception e) {
                        value = null;
                    }

                    if (value == null) {
                        cell.setCellValue(new HSSFRichTextString(""));
                        continue;
                    }
                    String textValue = null;
                    if (value instanceof Date) {
                        Date date = (Date) value;
                        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                        textValue = sdf.format(date);
                    } else {
                        textValue = value.toString();
                    }
                    if (textValue != null) {
                        Matcher matcher = PATTERN.matcher(textValue);
                        if (matcher.matches()) {
                            cell.setCellValue(Double.parseDouble(textValue));
                        } else {
                            cell.setCellValue(new HSSFRichTextString(textValue));
                        }
                    }
                }
            }

            workbook.write(out);
        }
    }

    /**
     * 导出excel模板
     *
     * @param fileName 文件名
     * @param title    sheet name
     * @param headers  表头
     * @param response
     * @throws Exception
     */
    public static void exportExcel(String fileName, String title, List<String> headers, HttpServletResponse response) throws Exception {
        setExcelFileName(fileName, response);
        XSSFWorkbook workbook = new XSSFWorkbook();
        try {
            workbook.createSheet(title);
            XSSFSheet sheet = workbook.getSheetAt(0);
            sheet.setDefaultColumnWidth(50);
            XSSFRow row = sheet.createRow(0);
            for (int i = 0; i < headers.size(); ++i) {
                XSSFCell cell = row.createCell(i);
                XSSFRichTextString text = new XSSFRichTextString(headers.get(i));
                cell.setCellValue(text);
            }
            workbook.write(response.getOutputStream());
        } catch (IOException var25) {
            logger.error("写入excel文档失败.", var25);
            throw var25;
        } finally {
            workbook.close();
        }
    }

    private static void setExcelFileName(String fileName, HttpServletResponse response) throws UnsupportedEncodingException {
        fileName = fileName + ".xlsx";
        String decodeFileName = new String(fileName.getBytes("gbk"), "iso8859-1");
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
        response.addHeader("Content-Disposition", "attachment;filename=" + decodeFileName);
    }

    public static String getExcelCellValue(Cell cell) {
        String value = "";

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                value = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    if (date != null) {
                        value = new SimpleDateFormat("yyyy-MM-dd").format(date);
                    } else {
                        value = "";
                    }
                } else {
                    value = new DecimalFormat("0").format(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_FORMULA:
                // 导入时如果为公式生成的数据则无值
                if (!"".equals(cell.getStringCellValue())) {
                    value = cell.getStringCellValue();
                } else {
                    value = cell.getNumericCellValue() + "";
                }
                break;
            case Cell.CELL_TYPE_BLANK:
                break;
            case Cell.CELL_TYPE_ERROR:
                value = "";
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = (cell.getBooleanCellValue() ? "Y" : "N");
                break;
            default:
                value = "";
        }

        return value.trim();
    }
}
