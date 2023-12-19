package com.project.library.plans;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PlanServiceTest {

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private PlanService planService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllPlans() {
        Plan plan1 = new Plan();
        plan1.setType("Plan 1");
        Plan plan2 = new Plan();
        plan2.setType("Plan 2");
        List<Plan> plans = Arrays.asList(plan1, plan2);

        when(planRepository.findAll()).thenReturn(plans);

        List<Plan> result = planService.getAllPlans();

        assertThat(result).isEqualTo(plans);
        verify(planRepository, times(1)).findAll();
    }

    @Test
    void testCreatePlan() {
        Plan plan = new Plan();
        plan.setType("New Plan");

        when(planRepository.save(plan)).thenReturn(plan);

        Plan result = planService.createPlan(plan);

        assertThat(result).isEqualTo(plan);
        verify(planRepository, times(1)).save(plan);
    }
}

