package com.example.lab05.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.lab05.dto.DashboardResponse;
import com.example.lab05.service.DashboardService;

@RestController
@RequestMapping("/${ID}/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/{personName}")
    public DashboardResponse dashboard(@PathVariable String personName) {
        return dashboardService.getDashboard(personName);
    }
}
