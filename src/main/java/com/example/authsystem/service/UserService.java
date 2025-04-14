package com.example.authsystem.service;

import com.example.authsystem.dto.UserRegistrationRequestDto;
import com.example.authsystem.model.User;
import com.example.authsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.authsystem.exception.AuthenticationException;
import org.slf4j.Logger; // Correct import statement
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private StringRedisTemplate redisTemplate; // Inject RedisTemplate

    @Autowired
    private PasswordEncoder encoder; //Password encoder to hash verification codes

    //private final Map<String, String> verificationCodes = new HashMap<>(); // Temporary storage for codes (BAD for production) - REMOVED

    public User registerUser(UserRegistrationRequestDto registrationDto) {
        User user = new User();
        user.setEmail(registrationDto.getEmail());
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setMethodType(registrationDto.getMethodType());

        if (registrationDto.getBiometricToken() != null) {
            user.setBiometricToken(encoder.encode(registrationDto.getBiometricToken()));
        }


        if (user.getMethodType() == User.AuthMethod.email_password) {
            user.setHashedPassword(passwordEncoder.encode(registrationDto.getPassword()));
        }
        user.setCreatedAt(Timestamp.from(Instant.now()));

        return userRepository.save(user);
    }

    public String loginWithEmailPassword(com.example.authsystem.dto.UserLoginRequestDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );

        if (authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal(); // Get UserDetails
            return jwtService.generateToken(userDetails);
        } else {
            throw new AuthenticationException("Invalid credentials");
        }
    }

    public Optional<User> loginWithBiometric(String biometricToken) {
        Optional<User> optionalUser = userRepository.findByBiometricToken(biometricToken);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Verify that user is the same
            if (!encoder.matches(biometricToken, user.getBiometricToken())) {
                return Optional.empty();
            }
        }
        return optionalUser;
    }


    public void registerPhoneNumber(String phoneNumber) {
        // 1. Generate Verification Code
        String verificationCode = generateVerificationCode();

        // Store the code in Redis (securely and with expiration)
        String hashedCode = encoder.encode(verificationCode); // Hash the code!
        redisTemplate.opsForValue().set(
                "verification:" + phoneNumber, // Key: verification:phoneNumber
                hashedCode,              // Value: Hashed code
                10,                       // Time to live: 10 minutes
                TimeUnit.MINUTES
        );

        // 2. "Send" SMS (replace with a real SMS service)
        System.out.println("Sending SMS to " + phoneNumber + " with code: " + verificationCode);

        // 3. (Optional) Store temporary user data (if needed) - omitted for simplicity
    }

    public String verifyPhoneNumber(String phoneNumber, String verificationCode) {
        // 1. Retrieve Stored Code from Redis
        String storedHashedCode = redisTemplate.opsForValue().get("verification:" + phoneNumber);

        // 2. Verify Code (compare hashed values)
        if (storedHashedCode == null || !encoder.matches(verificationCode, storedHashedCode)) {
            throw new AuthenticationException("Invalid verification code.");
        }

        // 3. Code is Valid: Remove from Redis
        redisTemplate.delete("verification:" + phoneNumber);

        // 4. Find or Create User (treat phone number as email for simplicity)
        Optional<User> existingUser = userRepository.findByEmail(phoneNumber); // Treat phone as email
        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            // Create a new user (minimal info)
            user = new User();
            user.setEmail(phoneNumber); // Treat phone as email
            user.setFirstName("User"); // Default values
            user.setLastName("Pending Verification");
            user.setMethodType(User.AuthMethod.biometric);
            user = userRepository.save(user);
        }

        // 5. Generate JWT
        UserDetails userDetails = (UserDetails) authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        null // No password required for JWT generation
                )
        ).getPrincipal(); // Get UserDetails from the authentication

        return jwtService.generateToken(userDetails); // Generate token based on phone (as email)
    }

    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }
}