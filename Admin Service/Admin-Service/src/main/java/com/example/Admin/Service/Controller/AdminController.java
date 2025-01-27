package com.example.Admin.Service.Controller;

import com.example.Admin.Service.Config.EventClient;
import com.example.Admin.Service.DTO.EventDTO;
import com.example.Admin.Service.Model.Admin;
import com.example.Admin.Service.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // Register a new admin
    @PostMapping("/register")
    public Admin registerAdmin(@RequestBody Admin admin) {
        return adminService.registerAdmin(admin);
    }
    // Get admin details associated with the event
    @GetMapping("/admin/{adminId}")
    public Optional<Admin> getAdminDetails(@PathVariable String id) {
        return adminService.getAdminFindById(id);
    }

    // Get admin details by username
    @GetMapping("/{username}")
    public Admin getAdmin(@PathVariable String username) {
        return adminService.getAdminByUsername(username);
    }


}
