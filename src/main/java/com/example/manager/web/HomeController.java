package com.example.manager.web;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.example.manager.util.OkHttpUtils;
import com.example.manager.util.PortalProperties;
import com.example.manager.dto.SubsystemDTO;
import com.example.manager.dto.ExcelDTO;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

@Controller
@Slf4j
public class HomeController {
    private final OkHttpClient client = new OkHttpClient();

    private final PortalProperties portalProperties;

    public HomeController(PortalProperties portalProperties) {
        this.portalProperties = portalProperties;
    }

    @GetMapping
    public String index() {
        return "index";
    }

    @GetMapping("download")
    public String download(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("导入模板", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), ExcelDTO.class).sheet("模板").doWrite(new ArrayList<>());
        return "redirect:index";
    }

    @PostMapping("import")
    public void startImport(MultipartFile file) throws IOException {
        String url = portalProperties.getUrl();
        url += "/ips/admin/web/subsys/sysSubsysD/page?pageSize=10&pageNumber=1&subsysName=德阳银海医药通";
        String s = OkHttpUtils.get(url, portalProperties.getCookie());
        SubsystemDTO subsystemDTO = JSONObject.parseObject(s, SubsystemDTO.class);
        log.error("");
    }
}
