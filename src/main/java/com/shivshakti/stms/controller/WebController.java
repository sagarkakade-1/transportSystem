package com.shivshakti.stms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * Web Controller for Thymeleaf views
 * Handles web page rendering and navigation
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Controller
@RequestMapping("/")
public class WebController {

    @GetMapping
    public String dashboard(Model model) {
        // Set page metadata
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("contentTemplate", "dashboard");
        
        // Mock statistics data (in real implementation, fetch from services)
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTrips", 156);
        stats.put("activeTrucks", 12);
        stats.put("activeDrivers", 18);
        stats.put("totalClients", 45);
        model.addAttribute("stats", stats);
        
        // Mock financial data
        Map<String, Object> financials = new HashMap<>();
        financials.put("monthlyRevenue", "2,45,000.00");
        financials.put("monthlyExpenses", "1,85,000.00");
        financials.put("netProfit", "60,000.00");
        financials.put("revenueGrowth", "12.5");
        financials.put("expenseGrowth", "8.3");
        financials.put("profitMargin", "24.5");
        model.addAttribute("financials", financials);
        
        return "layout/base";
    }

    @GetMapping("/drivers")
    public String drivers(Model model) {
        model.addAttribute("pageTitle", "Driver Management");
        model.addAttribute("contentTemplate", "drivers/list");
        return "layout/base";
    }

    @GetMapping("/clients")
    public String clients(Model model) {
        model.addAttribute("pageTitle", "Client Management");
        model.addAttribute("contentTemplate", "clients/list");
        return "layout/base";
    }

    @GetMapping("/trucks")
    public String trucks(Model model) {
        model.addAttribute("pageTitle", "Truck Management");
        model.addAttribute("contentTemplate", "trucks/list");
        return "layout/base";
    }

    @GetMapping("/trips")
    public String trips(Model model) {
        model.addAttribute("pageTitle", "Trip Management");
        model.addAttribute("contentTemplate", "trips/list");
        return "layout/base";
    }

    @GetMapping("/builties")
    public String builties(Model model) {
        model.addAttribute("pageTitle", "Builty Management");
        model.addAttribute("contentTemplate", "builties/list");
        return "layout/base";
    }

    @GetMapping("/expenses")
    public String expenses(Model model) {
        model.addAttribute("pageTitle", "Expense Management");
        model.addAttribute("contentTemplate", "expenses/list");
        return "layout/base";
    }

    @GetMapping("/incomes")
    public String incomes(Model model) {
        model.addAttribute("pageTitle", "Income Management");
        model.addAttribute("contentTemplate", "incomes/list");
        return "layout/base";
    }

    @GetMapping("/maintenance")
    public String maintenance(Model model) {
        model.addAttribute("pageTitle", "Maintenance Management");
        model.addAttribute("contentTemplate", "maintenance/list");
        return "layout/base";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("pageTitle", "Reports & Analytics");
        model.addAttribute("contentTemplate", "reports/dashboard");
        return "layout/base";
    }
}

