
        package com.example.demo.service;

        import com.example.demo.Student;
        import com.example.demo.model.User;
        import com.example.demo.repo.UserRepo;
        import org.hibernate.StaleObjectStateException;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.security.authentication.AuthenticationManager;
        import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
        import org.springframework.security.core.Authentication;
        import org.springframework.security.crypto.password.PasswordEncoder;
        import org.springframework.stereotype.Service;
        import org.springframework.transaction.annotation.Transactional;

        import java.util.ArrayList;
        import java.util.List;

        @Service
        public class UserService {
            private static final Logger logger = LoggerFactory.getLogger(UserService.class);

            @Autowired
            private UserRepo repo;

            @Autowired
            private PasswordEncoder passwordEncoder;

            @Autowired
            AuthenticationManager authManager;

            @Autowired
            private JWTService jwtService;

            @Transactional
            public User create(User user) {
                if (user.getId() != null) {
                    logger.warn("Ignoring client-provided ID: {}", user.getId());
                    user.setId(null);
                }
                user.setVersion(null);
                if (user.getPassword() != null) {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                } else {
                    logger.error("Password cannot be null");
                    throw new IllegalArgumentException("Password cannot be null");
                }
                if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                    logger.error("Username cannot be null or empty");
                    throw new IllegalArgumentException("Username cannot be null or empty");
                }
                if (user.getRoles() == null || user.getRoles().trim().isEmpty()) {
                    user.setRoles("USER");
                }
                logger.debug("Creating user with username: {}", user.getUsername());
                try {
                    return repo.save(user);
                } catch (StaleObjectStateException e) {
                    logger.error("Concurrency issue while saving user: {}", e.getMessage());
                    throw new RuntimeException("Failed to save user due to concurrent modification");
                } catch (org.springframework.dao.DataIntegrityViolationException e) {
                    logger.error("Data integrity issue, possibly duplicate username: {}", user.getUsername());
                    throw new IllegalArgumentException("Username already exists: " + user.getUsername());
                }
            }


            public String verify(User user) {


                Authentication authentication = authManager
                        .authenticate(new UsernamePasswordAuthenticationToken(
                                user.getUsername(), user.getPassword()));

                if (authentication.isAuthenticated()){
                    return jwtService.generateToken(user.getUsername());
                }

                return "Failed";
            }





        }
