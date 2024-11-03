package com.huoguo.core.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.huoguo.common.utils.ZipMultipleFiles;
import com.huoguo.core.dao.repository.EbutlerCustomerRepo;
import com.huoguo.model.dto.EbutlerUploadDto;
import com.huoguo.model.entity.EbutlerCustomer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.*;
import java.util.function.BiConsumer;

@Slf4j
@Service
public class EbutlerService {

    @Autowired
    private EbutlerCustomerRepo repo;
    private final Map<String, BiConsumer<EbutlerCustomer, String>> searchSetterMap = new HashMap<>();

    {
        searchSetterMap.put("站点", EbutlerCustomer::setSiteName);
        searchSetterMap.put("线路", EbutlerCustomer::setLineName);
        searchSetterMap.put("户号", EbutlerCustomer::setAccountNumber);
        searchSetterMap.put("户名", EbutlerCustomer::setAccountName);
        searchSetterMap.put("客户号", EbutlerCustomer::setCustomerName);
        searchSetterMap.put("产权分界点", EbutlerCustomer::setDivisionPoint);
        searchSetterMap.put("容量", EbutlerCustomer::setCapacity);
        searchSetterMap.put("电器容量", EbutlerCustomer::setElectricalCapacity);
        searchSetterMap.put("自备电源", EbutlerCustomer::setSelfPower);
        searchSetterMap.put("通讯号码", EbutlerCustomer::setTelephone);
        searchSetterMap.put("光伏户号", EbutlerCustomer::setPhotovoltaicNumber);
        searchSetterMap.put("光伏容量", EbutlerCustomer::setPhotovoltaicCapacity);
        searchSetterMap.put("变压器型号", EbutlerCustomer::setTransformerModel);
        searchSetterMap.put("变压器阻抗电压", EbutlerCustomer::setTransformerImpedanceVoltage);
    }

    public Page<EbutlerCustomer> searchUsers(String searchKey, String searchValue, int page, int size) {
        BiConsumer<EbutlerCustomer, String> setter = searchSetterMap.get(searchKey);
//        if (setter == null) {
//            return repo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")));
//        }

        // 创建实例作为查询模板
        EbutlerCustomer template = new EbutlerCustomer();
        if (setter != null) {
            setter.accept(template, searchValue);
        } else {
            searchSetterMap.values().forEach(set -> set.accept(template, searchValue));
        }

        // 创建ExampleMatcher来定制匹配策略
        // ExampleMatcher.matching()是所有都匹配
        ExampleMatcher matcher = ExampleMatcher.matchingAny()
                .withIgnoreNullValues()
                .withIgnoreCase() // 忽略大小写
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING); // 包含匹配
        // 创建Example对象
        Example<EbutlerCustomer> example = Example.of(template, matcher);

        // 设置分页信息
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));

        // 执行查询
        return repo.findAll(example, pageable);
    }

    public List<EbutlerUploadDto> readFromExcel(byte[] bytes, String fileName) {
        EbutlerUploadDtoListener readListener = new EbutlerUploadDtoListener(fileName);
        EasyExcel.read(new ByteArrayInputStream(bytes), readListener).doReadAllSync();
        return readListener.getResult();
    }

    /**
     * 每个站点写入到独立的excel，最后打成压缩包，返回压缩包地址
     */
    public String writeToZip() {
        List<EbutlerCustomer> all = repo.findAll();
        Map<String, List<EbutlerCustomer>> siteMap = new HashMap<>();
        for (EbutlerCustomer customer : all) {
            List<EbutlerCustomer> list = siteMap.computeIfAbsent(customer.getSiteName(), e -> new ArrayList<>());
            list.add(customer);
        }
        List<String> excelsPath = new ArrayList<>();
        siteMap.forEach((site, data) -> {
            String excel = processSite(site, data);
            excelsPath.add(excel);
        });
        return doZip(excelsPath);
    }

    private String getLocalPath() {
        return new File(".").getAbsolutePath();
    }

    private String processSite(String site, List<EbutlerCustomer> lst) {
        String fileName = getLocalPath() + "/" + site + ".xls";

        Map<String, List<EbutlerCustomer>> lineMap = new HashMap<>();
        for (EbutlerCustomer ebutlerCustomer : lst) {
            List<EbutlerCustomer> list = lineMap.computeIfAbsent(ebutlerCustomer.getLineName(), e -> new ArrayList<>());
            list.add(ebutlerCustomer);
        }

        ExcelWriter excelWriter = EasyExcel.write(fileName).build();
        int idx = 0;
        for (Map.Entry<String, List<EbutlerCustomer>> entry : lineMap.entrySet()) {
            //这里 需要指定写用哪个class去写
            WriteSheet writeSheet = EasyExcel.writerSheet(idx++, entry.getKey()).head(EbutlerCustomer.class).build();
            excelWriter.write(entry.getValue(), writeSheet);
        }
        excelWriter.finish();

        return fileName;
    }

    private String doZip(List<String> excelsPath) {
        String outputZipFile = new File(".").getAbsolutePath() + "/download.zip";
        ZipMultipleFiles.zipFiles(excelsPath, outputZipFile);
        return outputZipFile;
    }

    @Getter
    static class EbutlerUploadDtoListener implements ReadListener<LinkedHashMap> {
        private final List<EbutlerUploadDto> result = new ArrayList<>();
        private int lastSheetNo = -1;
        private boolean canBegin = false;
        private Map<String, BiConsumer<String, EbutlerUploadDto>> setterMap = new HashMap<>();

        {
            setterMap.put("序号", (s, ebutlerUploadDto) -> {
                //序号不做处理
            });

            setterMap.put("户名", (s, ebutlerUploadDto) -> ebutlerUploadDto.setAccountName(s));
            setterMap.put("户号", (s, ebutlerUploadDto) -> ebutlerUploadDto.setAccountNumber(s));
            setterMap.put("客户名称", (s, ebutlerUploadDto) -> ebutlerUploadDto.setCustomerName(s));
            setterMap.put("产权分界点", (s, ebutlerUploadDto) -> ebutlerUploadDto.setDivisionPoint(s));
            setterMap.put("容量", (s, ebutlerUploadDto) -> ebutlerUploadDto.setCapacity(s));
            setterMap.put("电容器容量", (s, ebutlerUploadDto) -> ebutlerUploadDto.setElectricalCapacity(s));
            setterMap.put("自备电源", (s, ebutlerUploadDto) -> ebutlerUploadDto.setSelfPower(s));
            setterMap.put("通讯号码", (s, ebutlerUploadDto) -> ebutlerUploadDto.setTelephone(s));
            setterMap.put("变压器型号", (s, ebutlerUploadDto) -> ebutlerUploadDto.setTransformerModel(s));
            setterMap.put("变压器阻抗电压", (s, ebutlerUploadDto) -> ebutlerUploadDto.setTransformerImpedanceVoltage(s));
            setterMap.put("光伏户号", (s, ebutlerUploadDto) -> ebutlerUploadDto.setPhotovoltaicNumber(s));
            setterMap.put("光伏（kVA）", (s, ebutlerUploadDto) -> ebutlerUploadDto.setPhotovoltaicCapacity(s));
            setterMap.put("光伏容量", (s, ebutlerUploadDto) -> ebutlerUploadDto.setPhotovoltaicCapacity(s));
        }

        private Map<Integer, String> colIdxNameMap = new HashMap<>();

        private final String fileName;

        public EbutlerUploadDtoListener(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public void invoke(LinkedHashMap linkedHashMap, AnalysisContext analysisContext) {
            try {
                Integer sheetNo = analysisContext.readSheetHolder().getSheetNo();
                if (sheetNo != lastSheetNo) {
                    canBegin = false;
                    lastSheetNo = sheetNo;
                    colIdxNameMap.clear();
                }
                String sheetName = analysisContext.readSheetHolder().getSheetName();

                // 最多12列
                int maxColNum = 12;
                if (!canBegin) {
                    for (int i = 0; i < maxColNum; i++) {
                        Object o = linkedHashMap.get(i);
                        if (o == null) {
                            continue;
                        }
                        colIdxNameMap.put(i, ((String) o).trim());
                    }
                    canBegin = true;
                    return;
                }

                EbutlerUploadDto dto = new EbutlerUploadDto();
                dto.setEbutlerCustomer(new EbutlerCustomer());
                // 文件名为站点
                dto.setSiteName(fileName.replace(".xlsx", "").replace(".xls", ""));
                // sheet名为线路
                dto.setLineName(sheetName);
                for (int i = 0; i < maxColNum; i++) {
                    String colName = colIdxNameMap.get(i);
                    BiConsumer<String, EbutlerUploadDto> setter = setterMap.get(colName);
                    if (setter == null) {
                        log.info(String.format("没找到对应的setter,sheetNo=%s,colName=%s,lhm=%s", sheetNo, colName, JSON.toJSONString(linkedHashMap)));
                        continue;
                    }
                    Object o = linkedHashMap.get(i);
                    if (o == null) {
                        continue;
                    }
                    setter.accept(((String) o).trim(), dto);
                }
                result.add(dto);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        }
    }
}
