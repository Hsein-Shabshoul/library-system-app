package com.project.library.subscriptions;

import com.project.library.security.otp.GenericResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class SubController {
    private final SubService subService;

    @PostMapping("/subscribe")
    public ResponseEntity<GenericResponse> subscribe(@Valid @RequestBody SubRequest request){
        return ResponseEntity.ok(subService.subscribe(request));
    }
    @PostMapping("/resubscribe")
    public ResponseEntity<GenericResponse> resubscribe(@Valid @RequestBody SubRequest request){
        return ResponseEntity.ok(subService.resubscribe(request));
    }
}
