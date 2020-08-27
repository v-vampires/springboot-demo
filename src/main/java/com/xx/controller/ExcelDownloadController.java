package com.xx.controller;

import com.google.common.collect.Lists;
import com.xx.util.ExcelExporter;
import com.xx.vo.ParamVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

/**
 * @author yifanl
 * @Date 2020/5/18 20:24
 */
@RestController
@Slf4j
@RequestMapping("/excel")
public class ExcelDownloadController {
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void exportIvrHotel(ParamVo paramVo, HttpServletResponse response) {
        try {

            String fileName = UUID.randomUUID().toString()+".xls";
            List<Object> list = Lists.newArrayList();
            List<Object> headers = Lists.newArrayList("门店名称", "门店id", "优先级","城市","负责人","巡房时间","巡房结果","操作结果");
            List<Object> attrNames = Lists.newArrayList("hotelName", "hotelId", "pLevel","cityName","platformManagerName","outCallTimeStr","outCallResult","bdTaskResult");

            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
            ExcelExporter.exportExcelExtended("ivrHotel列表导出", headers, attrNames, list, response.getOutputStream(), "yyyy-MM-dd HH:mm:ss");
        } catch (Exception e) {
            log.error("ivrHotel列表导出失败!", e);
        }
    }
}
