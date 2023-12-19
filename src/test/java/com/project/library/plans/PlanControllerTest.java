package com.project.library.plans;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Arrays;
import static org.mockito.Mockito.when;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PlanControllerTest {
    private MockMvc mockMvc;

    @Mock
    private PlanService planService;

    @InjectMocks
    private PlanController planController;

    public PlanControllerTest() {
        MockitoAnnotations.openMocks(this);
    }
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(planController)
                .build();
    }

    @Test
    void testCreatePlan() {
        Plan plan = new Plan();
        plan.setType("New Plan");

        when(planService.createPlan(plan)).thenReturn(plan);
        ResponseEntity<Plan> responseEntity = planController.createPlan(plan);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(plan);

        verify(planService, times(1)).createPlan(plan);
    }

    @Test
    void getPlans_shouldReturnOkResponse() {
        /* initialize list of plans */
        List<Plan> plans = List.of();
        when(planService.getAllPlans()).thenReturn(plans);

        ResponseEntity<List<Plan>> response = planController.getPlans();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(plans, response.getBody());
        verify(planService, times(1)).getAllPlans();
    }

}
