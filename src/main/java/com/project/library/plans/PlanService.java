package com.project.library.plans;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class PlanService {
    private final PlanRepository planRepository;

    public List<Plan> getAllPlans(){
        log.info("Requested All Plans.");
        return planRepository.findAll();
    }

    public Plan createPlan(Plan plan){
        Plan newPlan = planRepository.save(plan);
        log.info("New Plan Added:\n{}", newPlan);
        return newPlan;
    }
}
