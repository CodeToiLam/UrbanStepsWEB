package vn.urbansteps.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.urbansteps.service.ThongKeService;
import java.util.Map;

@RestController
@RequestMapping("/api/thong-ke")
public class ThongKeController {
    @Autowired
    private ThongKeService thongKeService;

    @GetMapping("")
    public Map<String, Object> thongKeTongQuan() {
        return thongKeService.thongKeTongQuan();
    }
}
