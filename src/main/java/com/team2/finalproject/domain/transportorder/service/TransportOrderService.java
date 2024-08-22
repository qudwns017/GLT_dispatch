package com.team2.finalproject.domain.transportorder.service;

import com.team2.finalproject.domain.deliverydestination.repository.DeliveryDestinationRepository;
import com.team2.finalproject.domain.sm.repository.SmRepository;
import com.team2.finalproject.domain.transportorder.model.dto.request.SmNameAndPostalCodeRequest;
import com.team2.finalproject.domain.transportorder.model.dto.response.SmNameAndPostalCodeResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransportOrderService {
  
    private final SmRepository smRepository;
    private final DeliveryDestinationRepository deliveryDestinationRepository;

    private static final short FONT_SIZE_NAME = 14;
    private static final short FONT_SIZE_REQUIRED = 12;
    private static final short COMMENT_ROW_HEIGHT = 2000;
    private static final int COLUMN_PADDING = 8192;
    
    public void downloadOrderFormExcel(HttpServletResponse response) {
        List<TransportOrderExcelHeader> transportOrdersList = TransportOrderExcelHeader.getTransportOrders();

        XSSFWorkbook workbook = new XSSFWorkbook();

        XSSFCellStyle nameRowStyle = createCellStyle(workbook, IndexedColors.GREY_40_PERCENT, FONT_SIZE_NAME, true,
                HorizontalAlignment.CENTER, VerticalAlignment.CENTER, true);
        XSSFCellStyle requiredRowStyle = createCellStyle(workbook, IndexedColors.GREY_25_PERCENT, FONT_SIZE_REQUIRED,
                true, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, true);
        requiredRowStyle.getFont().setColor(IndexedColors.RED.getIndex());

        XSSFCellStyle commentRowStyle = createCellStyle(workbook, IndexedColors.LEMON_CHIFFON, (short) 0, false, null,
                VerticalAlignment.CENTER, true);
        commentRowStyle.setWrapText(true);

        XSSFCellStyle exampleRowStyle = createCellStyle(workbook, null, (short) 0, false, null,
                VerticalAlignment.CENTER, true);
        exampleRowStyle.setWrapText(true);

        XSSFSheet sheet = workbook.createSheet("운송_주문_양식");

        createRow(sheet, 0, transportOrdersList, nameRowStyle);
        createRow(sheet, 1, transportOrdersList, requiredRowStyle);
        createRow(sheet, 2, transportOrdersList, commentRowStyle);
        createRow(sheet, 3, transportOrdersList, exampleRowStyle);

        adjustColumnWidths(sheet, transportOrdersList.size());

        sheet.getRow(2).setHeight(COMMENT_ROW_HEIGHT);

        try {
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    public List<SmNameAndPostalCodeResponse> validateSmNameAndPostalCodes(List<SmNameAndPostalCodeRequest> requests) {
        Map<String, Integer> smNameWithIdMap = smRepository.findAllSmNameWithIdsToMap();
        Map<String, Integer> postalWithIdMap = deliveryDestinationRepository.findAllPostalCodeWithIdsToMap();

        return requests.stream()
                .map(request -> SmNameAndPostalCodeResponse.builder()
                        .postalCodeValid(postalWithIdMap.containsKey(request.postalCode()))
                        .deliveryDestinationId(postalWithIdMap.getOrDefault(request.postalCode(), 0))
                        .smNameValid(smNameWithIdMap.containsKey(request.smName()))
                        .smId(smNameWithIdMap.getOrDefault(request.smName(), 0))
                        .build())
                .toList();
    }

    private XSSFCellStyle createCellStyle(XSSFWorkbook workbook, IndexedColors backgroundColor, short fontSize,
                                          boolean bold, HorizontalAlignment hAlign, VerticalAlignment vAlign,
                                          boolean border) {
        XSSFCellStyle style = workbook.createCellStyle();
        if (backgroundColor != null) {
            style.setFillForegroundColor(backgroundColor.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        if (fontSize > 0 || bold) {
            XSSFFont font = workbook.createFont();
            font.setFontHeightInPoints(fontSize);
            font.setBold(bold);
            style.setFont(font);
        }
        if (hAlign != null) {
            style.setAlignment(hAlign);
        }
        if (vAlign != null) {
            style.setVerticalAlignment(vAlign);
        }
        if (border) {
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
        }
        return style;
    }

    private void createRow(XSSFSheet sheet, int rowIndex, List<TransportOrderExcelHeader> transportOrdersList,
                           XSSFCellStyle style) {
        Row row = sheet.createRow(rowIndex);
        int cellNumber = 0;
        for (TransportOrderExcelHeader header : transportOrdersList) {
            Cell cell = row.createCell(cellNumber++);
            if (rowIndex == 0) {
                cell.setCellValue(header.name());
            } else if (rowIndex == 1) {
                cell.setCellValue(header.required());
            } else if (rowIndex == 2) {
                cell.setCellValue(header.comment());
            } else if (rowIndex == 3) {
                cell.setCellValue(header.example());
            }
            cell.setCellStyle(style);
        }
    }

    private void adjustColumnWidths(XSSFSheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + COLUMN_PADDING);
        }
    }
}

