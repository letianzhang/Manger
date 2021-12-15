package com.example.manager.web;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.example.manager.constant.CustomConstant;
import com.example.manager.dto.*;
import com.example.manager.util.OkHttpUtils;
import com.example.manager.util.PortalProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class HomeController {
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
        List<ExcelDTO> exampleList = new ArrayList<>();
        ExcelDTO excelDTO = new ExcelDTO();
        excelDTO.setResuName("定点药店");
        excelDTO.setResuCodg("yb001");
        excelDTO.setSubsysName("成都银海医药通");
        excelDTO.setResuPath("/#/");
        excelDTO.setPrntResuName("菜单列表");
        exampleList.add(excelDTO);
        excelDTO = new ExcelDTO();
        excelDTO.setResuName("药店结算");
        excelDTO.setResuCodg("yb002");
        excelDTO.setSubsysName("成都银海医药通");
        excelDTO.setResuPath("medicareSettlement.html#/settlement");
        excelDTO.setPrntResuName("定点药店");
        excelDTO.setApi("/web/personInfo/*\n/web/personInfo/*\n/disease/*");
        exampleList.add(excelDTO);
        EasyExcel.write(response.getOutputStream(), ExcelDTO.class).sheet("模板").doWrite(exampleList);
        return "redirect:index";
    }

    @PostMapping("import")
    public String startImport(MultipartFile file, Model model) throws IOException {
        List<ExcelDTO> list = EasyExcel.read(file.getInputStream()).head(ExcelDTO.class).sheet().doReadSync();
        if (CollectionUtils.isEmpty(list)) {
            model.addAttribute("result", "Excel为空！");
            return "import_result";
        }
        Set<String> subSysNameSet = list.stream().map(ExcelDTO::getSubsysName).collect(Collectors.toSet());
        if (subSysNameSet.size() > 2) {
            model.addAttribute("result", "仅支持一次导入一个子系统下的所有菜单！");
            return "import_result";
        }
        // 根据子系统名称查询子系统信息
        String baseUrl = portalProperties.getUrl();
        String querySubSysUrl = baseUrl + "/ips/admin/web/subsys/sysSubsysD/page?pageSize=10&pageNumber=1&subsysName=" + subSysNameSet.iterator().next();
        String s = OkHttpUtils.get(querySubSysUrl, portalProperties.getCookie(), portalProperties.getToken());
        SubsystemDTO subsystemDTO = JSONObject.parseObject(s, SubsystemDTO.class);
        if (!Objects.equals(subsystemDTO.getCode(), CustomConstant.CODE_SUCCESS)) {
            model.addAttribute("result", subsystemDTO.getMessage());
            return "import_result";
        }
        // 后续新增的菜单也需要向该map中写入
        Map<String, String> menuNameToIdMap = new HashMap<>();
        menuNameToIdMap.put("菜单列表", "-1");
        // 子系统ID
        String subsysId = subsystemDTO.getData().getResult().get(0).getSubsysId();
        // 循环添加菜单
        String addMenuUrl = baseUrl + "/ips/admin/web/sysResuD";
        for (ExcelDTO item : list) {
            MenuDTO dto = MenuDTO.builder()
                    .prntResuId(menuNameToIdMap.get(item.getPrntResuName()))
                    .resuType("1")
                    .subsysId(subsysId)
                    .resuName(item.getResuName())
                    .resuCodg(item.getResuCodg())
                    .resuPath(item.getResuPath()).build();
            String addMenuResult = OkHttpUtils.post(addMenuUrl, portalProperties.getCookie(), portalProperties.getToken(), JSONObject.toJSONString(dto));
            AddMenuResultDTO addMenuResultDTO = JSONObject.parseObject(addMenuResult, AddMenuResultDTO.class);
            if (!Objects.equals(addMenuResultDTO.getCode(), CustomConstant.CODE_SUCCESS)) {
                model.addAttribute("result", addMenuResultDTO.getMessage());
                return "import_result";
            }
            menuNameToIdMap.put(item.getResuName(), addMenuResultDTO.getData().getResuId());

            // 添加API
            if (item.getApi() == null || item.getApi().trim().length() == 0) {
                continue;
            }
            String queryFunctionUrl = baseUrl + "/ips/admin/web/sysResuD/sysr/" + addMenuResultDTO.getData().getResuId();
            String functionResult = OkHttpUtils.get(queryFunctionUrl, portalProperties.getCookie(), portalProperties.getToken());
            FunctionDTO functionDTO = JSONObject.parseObject(functionResult, FunctionDTO.class);
            if (!Objects.equals(functionDTO.getCode(), CustomConstant.CODE_SUCCESS)) {
                model.addAttribute("result", functionDTO.getMessage());
                return "import_result";
            }
            String functionId = functionDTO.getData().get(0).getResuId();
            String[] apis = item.getApi().trim().split(CustomConstant.menuApiSeparateChar);
            for (String api : apis) {
                ApiDTO apiDTO = ApiDTO.builder()
                        .resuName(api)
                        .prntResuId(functionId)
                        .resuPath(api)
                        .resuType("2")
                        .subsysId(subsysId).build();
                String apiResult = OkHttpUtils.post(addMenuUrl, portalProperties.getCookie(), portalProperties.getToken(), JSONObject.toJSONString(apiDTO));
                AddApiResultDTO addApiResultDTO = JSONObject.parseObject(apiResult, AddApiResultDTO.class);
                if (!Objects.equals(addApiResultDTO.getCode(), CustomConstant.CODE_SUCCESS)) {
                    model.addAttribute("result", addApiResultDTO.getMessage());
                    return "import_result";
                }
            }
        }
        model.addAttribute("result", "导入成功");
        return "import_result";
    }
}
