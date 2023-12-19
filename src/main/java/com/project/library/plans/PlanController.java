package com.project.library.plans;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/plans")
    public ResponseEntity<Plan> createPlan(@Valid @RequestBody Plan plan){
        return ResponseEntity.ok(planService.createPlan(plan));
    }
    @GetMapping("/plans")
    public ResponseEntity<List<Plan>> getPlans(){
        return ResponseEntity.ok(planService.getAllPlans());
    }
}