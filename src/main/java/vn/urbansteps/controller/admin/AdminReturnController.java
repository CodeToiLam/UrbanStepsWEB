package vn.urbansteps.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.urbansteps.service.ReturnRequestService;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminReturnController {
    private final ReturnRequestService service;

    @GetMapping("/returns")
    public String list(Model model){
    // Legacy endpoint: redirect to new management page
    return "redirect:/admin/quan-ly-yeu-cau-tra-hang";
    }

    @PostMapping("/returns/{id}/approve")
    public String approve(@PathVariable Long id){
        service.approve(id);
        return "redirect:/admin/returns";
    }

    @PostMapping("/returns/{id}/reject")
    public String reject(@PathVariable Long id, @RequestParam(required=false) String reason){
        service.reject(id, reason);
        return "redirect:/admin/returns";
    }
}
