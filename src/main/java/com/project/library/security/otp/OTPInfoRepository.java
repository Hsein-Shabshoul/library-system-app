package com.project.library.security.otp;

import com.project.library.security.otp.OTPInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OTPInfoRepository extends JpaRepository<OTPInfo, Long> {

}
