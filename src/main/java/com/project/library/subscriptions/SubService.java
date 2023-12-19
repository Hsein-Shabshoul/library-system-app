package com.project.library.subscriptions;

import com.project.library.exception.BadRequestException;
import com.project.library.plans.Plan;
import com.project.library.plans.PlanRepository;
import com.project.library.security.config.JwtService;
import com.project.library.security.otp.GenericResponse;
import com.project.library.security.user.User;
import com.project.library.security.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubService {
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final JwtService jwtService;

    public String extractToken() {
        // Get the current request
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        // Extract the token from the Authorization header
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // Extracting the token excluding "Bearer "
        }
        // Token not found in the header
        return null;
    }

    public GenericResponse subscribe(SubRequest request){
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isPresent()){
            if(!userOptional.get().getEmail().equals(jwtService.extractUsername(extractToken()))){
                throw new BadRequestException("Provided email does not match signed in email.");
            }
        }
        else {
            throw new BadRequestException("Email not registered");
        }

        Optional<Plan> planOptional = planRepository.findById(request.getPlanId());
        if (planOptional.isPresent()){
            User user = userOptional.get();
            if (user.getPlan() != null){
                throw new BadRequestException("User already subscribed to a plan, need to change/renew subscription");
            }
            Plan plan = planOptional.get();
            user.setPlan(plan);
            user.setRemainingBooks(plan.getQuantity());
            LocalDateTime now = LocalDateTime.now();
            user.setPlanExpiry(now.plusDays(plan.getDuration()));
            userRepository.save(user);
            return GenericResponse.builder()
                    .response("Subscribed to " + plan.getType() + " plan.").build();
        }
        else {
            throw new BadRequestException("Plan with ID:" + request.getPlanId() + " does not exist");
        }
    }

    public GenericResponse resubscribe(SubRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isPresent()) {
            if (!userOptional.get().getEmail().equals(jwtService.extractUsername(extractToken()))) {
                throw new BadRequestException("Provided email does not match signed in email.");
            }
        } else {
            throw new BadRequestException("Email not registered");
        }

        Optional<Plan> planOptional = planRepository.findById(request.getPlanId());
        if (planOptional.isPresent()) {
            User user = userOptional.get();
            //if user not subscribed before, subscribe.
            if (user.getPlan() == null) {
                return subscribe(request);
            }
            else {
                Plan newPlan = planOptional.get();
                Plan oldPlan = user.getPlan();
                if (newPlan.getQuantity() <= user.getRemainingBooks()){
                    return GenericResponse.builder()
                            .response("Your current plan has a higher/equal limit compared to this " + newPlan.getType() + " plan.").build();
                }
                user.setPlan(newPlan);
                user.setRemainingBooks(newPlan.getQuantity());
                LocalDateTime now = LocalDateTime.now();
                user.setPlanExpiry(now.plusDays(newPlan.getDuration()));
                userRepository.save(user);
                if (oldPlan.equals(newPlan)){
                    return GenericResponse.builder()
                            .response("Renewed " + newPlan.getType() + " plan.").build();
                }
                return GenericResponse.builder()
                        .response("Changed subscription to " + newPlan.getType() + " plan.").build();
            }
        }
        else{
            throw new BadRequestException("Plan with ID:" + request.getPlanId() + " does not exist");
        }
    }
}
