package com.huoguo.controller;

import com.huoguo.core.dao.repository.EbutlerCustomerRepo;
import com.huoguo.core.dao.repository.EbutlerLineRepo;
import com.huoguo.core.dao.repository.EbutlerSiteRepo;
import com.huoguo.core.service.EbutlerService;
import com.huoguo.model.dto.EbutlerUploadDto;
import com.huoguo.model.entity.EbutlerCustomer;
import com.huoguo.model.entity.EbutlerLine;
import com.huoguo.model.entity.EbutlerSite;
import com.huoguo.model.vo.Result;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@RequestMapping("/ebutler")
@Controller
public class EbutlerController {

    @Autowired
    private EbutlerService ebutlerService;
    @Autowired
    private EbutlerCustomerRepo customerRepo;
    @Autowired
    private EbutlerSiteRepo siteRepo;
    @Autowired
    private EbutlerLineRepo lineRepo;

    private <T> Result<T> process(Supplier<T> supplier) {
        try {
            T t = supplier.get();
            return Result.success(t);
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        } catch (Throwable e) {
            log.error("process error", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/page")
    @ResponseBody
    public Object getData(@RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                          @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
                          @RequestParam(value = "sort", required = false) String sort,
                          @RequestParam(value = "order", required = false) String order,
                          @RequestParam(value = "search", required = false) String search) {
        Page<EbutlerCustomer> paged;
        int pageNumber = offset / limit;
        int pageSize = limit;
        if (StringUtils.hasLength(search) && search.contains(":")) {
            String[] split = search.split(":");
            paged = ebutlerService.searchUsers(split[0], split[1], pageNumber, pageSize);
        } else {
            paged = ebutlerService.searchUsers(null, search, pageNumber, pageSize);
        }
        List<EbutlerCustomer> content = paged.getContent();
        Map<String, Object> map = new HashMap<>();
        map.put("total", paged.getTotalElements());
        map.put("rows", content);
        return map;
    }

    @SneakyThrows
    @GetMapping("/allSite")
    @ResponseBody
    public Result<List<EbutlerSite>> allSite() {
        return process(() -> siteRepo.findAll());
    }

    @SneakyThrows
    @GetMapping("/allLine")
    @ResponseBody
    public Result<List<EbutlerLine>> allLine(@RequestParam(required = false) String siteId) {
        return process(() -> {
            if (StringUtils.hasLength(siteId)) {
                return lineRepo.findBySiteId(Integer.valueOf(siteId));
            } else {
                return lineRepo.findAll();
            }
        });
    }

    @SneakyThrows
    @PostMapping("/site/upsert")
    @ResponseBody
    public Result<Void> upsertSite(@RequestBody EbutlerSite site) {
        return process(() -> {
            site.setModifyTime(LocalDateTime.now());
            if (site.getId() == null) {
                // 说明是新增
                site.setCreateTime(LocalDateTime.now());
                siteRepo.save(site);
            } else {
                customerRepo.updateSiteName(site.getId(), site.getName());
                siteRepo.save(site);
            }
            return null;
        });
    }

    @SneakyThrows
    @PostMapping("/site/del")
    @ResponseBody
    public Result<Void> delSite(@RequestParam String ids) {
        return process(() -> {
            if (!StringUtils.hasLength(ids)) {
                return null;
            }
            String[] split = ids.split(",");
            List<Integer> idList = new ArrayList<>();
            for (String s : split) {
                idList.add(Integer.valueOf(s));
            }
            siteRepo.deleteAllById(idList);
            lineRepo.deleteBySiteIds(idList);
            customerRepo.deleteBySiteIds(idList);
            return null;
        });
    }

    @SneakyThrows
    @PostMapping("/line/upsert")
    @ResponseBody
    public Result<Void> upsertSite(@RequestBody EbutlerLine line) {
        return process(() -> {
            line.setModifyTime(LocalDateTime.now());
            if (line.getId() == null) {
                // 说明是新增
                line.setCreateTime(LocalDateTime.now());
                lineRepo.save(line);
            } else {
                customerRepo.updateLineName(line.getId(), line.getName());
                trySetSiteId(line);
                lineRepo.save(line);
            }
            return null;
        });
    }

    @SneakyThrows
    @PostMapping("/line/del")
    @ResponseBody
    public Result<Void> delLine(@RequestParam String ids) {
        return process(() -> {
            if (!StringUtils.hasLength(ids)) {
                return null;
            }
            String[] split = ids.split(",");
            List<Integer> idList = new ArrayList<>();
            for (String s : split) {
                idList.add(Integer.valueOf(s));
            }
            lineRepo.deleteAllById(idList);
            customerRepo.deleteByLineIds(idList);
            return null;
        });
    }

    @SneakyThrows
    @PostMapping("/customer/upsert")
    @ResponseBody
    public Result<Void> upsertCustomer(@RequestBody EbutlerCustomer ebutlerCustomer) {
        return process(() -> {
            ebutlerCustomer.setModifyTime(LocalDateTime.now());
            if (ebutlerCustomer.getId() == null) {
                // 说明是新增
                ebutlerCustomer.setCreateTime(LocalDateTime.now());
            }
            EbutlerSite site = siteRepo.findByName(ebutlerCustomer.getSiteName());
            if (site == null) {
                throw new IllegalArgumentException("站点不存在，请先添加站点");
            }
            EbutlerLine line = lineRepo.findByName(ebutlerCustomer.getLineName());
            if (line == null) {
                throw new IllegalArgumentException("线路不存在，请先添加线路");
            }
            customerRepo.save(ebutlerCustomer);
            return null;
        });
    }

    @SneakyThrows
    @PostMapping("/customer/del")
    @ResponseBody
    public Result<Void> delCustomer(@RequestParam String ids) {
        return process(new Supplier<Void>() {
            @Override
            public Void get() {
                if (!StringUtils.hasLength(ids)) {
                    return null;
                }
                String[] split = ids.split(",");
                List<Integer> idList = new ArrayList<>();
                for (String s : split) {
                    idList.add(Integer.valueOf(s));
                }
                customerRepo.deleteAllById(idList);
                return null;
            }
        });
    }

    @SneakyThrows
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        String indexPage = "redirect:/static/index.html";
        if (file.isEmpty()) {
            return indexPage;
        }
        byte[] bytes = file.getBytes();
        List<EbutlerUploadDto> list = ebutlerService.readFromExcel(bytes, file.getOriginalFilename());
        Map<String, Set<String>> siteLineMap = new HashMap<>();
        for (EbutlerUploadDto dto : list) {
            Set<String> lines = siteLineMap.computeIfAbsent(dto.getSiteName(), e -> new HashSet<>());
            lines.add(dto.getLineName());
        }

        // TODO 判断站点、线路、客户号是否有重复
        List<EbutlerSite> sites = siteLineMap.keySet().stream().map(e -> {
            EbutlerSite site = new EbutlerSite();
            site.setName(e);
            site.setCreateTime(LocalDateTime.now());
            site.setCreator("admin");
            site.setModifier("admin");
            return site;
        }).collect(Collectors.toList());
        siteRepo.saveAll(sites);
        Map<String, Integer> siteIdMap = sites.stream().collect(Collectors.toMap(EbutlerSite::getName, EbutlerSite::getId));

        List<EbutlerLine> lines = new ArrayList<>();
        siteLineMap.forEach((siteName, lst) ->
                lines.addAll(lst.stream().map(e -> {
                    EbutlerLine line = new EbutlerLine();
                    line.setName(e);
                    line.setSiteId(siteIdMap.get(siteName));
                    line.setCreateTime(LocalDateTime.now());
                    line.setCreator("admin");
                    line.setModifier("admin");
                    return line;
                }).collect(Collectors.toList())));
        lineRepo.saveAll(lines);
        Map<String, Integer> lineIdMap = lines.stream().collect(Collectors.toMap(EbutlerLine::getName, EbutlerLine::getId));

        List<EbutlerCustomer> ebutlerCustomers = list.stream().map(e -> {
            EbutlerCustomer ebutlerCustomer = e.getEbutlerCustomer();
            ebutlerCustomer.setSiteId(siteIdMap.get(e.getSiteName()));
            ebutlerCustomer.setLineId(lineIdMap.get(e.getLineName()));
            ebutlerCustomer.setCreateTime(LocalDateTime.now());
            ebutlerCustomer.setCreator("admin");
            ebutlerCustomer.setModifier("admin");
            return ebutlerCustomer;
        }).collect(Collectors.toList());

        List<EbutlerCustomer> all = customerRepo.findAll();
        if (CollectionUtils.isEmpty(all)) {
            customerRepo.saveAll(ebutlerCustomers);
            return indexPage;
        }

        // 使用户号区分更新还是新增
        Map<String, EbutlerCustomer> map = new HashMap<>();
        for (EbutlerCustomer e : all) {
            EbutlerCustomer old = map.put(e.getAccountNumber(), e);
            if (old != null) {
                log.info(String.format("存在相同户号[%s]，已选择一个进行覆盖", e.getAccountNumber()));
            }
        }

        for (EbutlerCustomer ebutlerCustomer : ebutlerCustomers) {
            String accountNumber = ebutlerCustomer.getAccountNumber();
            if (map.containsKey(accountNumber)) {
                // 更新，设置id
                ebutlerCustomer.setId(map.get(accountNumber).getId());
            }
        }

        customerRepo.saveAll(ebutlerCustomers);
        return indexPage;
    }

    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> downloadFile() throws IOException {
        String path = ebutlerService.writeToZip();

        InputStreamResource resource = new InputStreamResource(new FileInputStream(path));

        String[] split = path.split("/");
        String filename = split[split.length - 1];
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    private void trySetSiteId(EbutlerLine line) {
        EbutlerLine entity = lineRepo.findById(line.getId()).get();
        line.setSiteId(entity.getSiteId());
    }
}
