package com.project.library.reservations;

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
public class ReservationController {
    private final ReservationService reservationService;
    @PostMapping("/reserve")
    public ResponseEntity<GenericResponse> reserveBook(@Valid @RequestBody ReservationRequest request){
        return ResponseEntity.ok(reservationService.reserveBook(request));
    }
    @PostMapping("/return")
    public ResponseEntity<String> returnBook(@Valid @RequestBody ReservationRequest request){
        return ResponseEntity.ok(reservationService.returnBook(request));
    }
}
