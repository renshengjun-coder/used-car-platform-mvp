package com.usedcar.auth;

import com.usedcar.auth.dto.AuthResponse;
import com.usedcar.auth.dto.LoginRequest;
import com.usedcar.auth.dto.RegisterRequest;
import com.usedcar.common.ApiException;
import com.usedcar.domain.Seller;
import com.usedcar.mapper.SellerMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final SellerMapper sellerMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(SellerMapper sellerMapper, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.sellerMapper = sellerMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest req) {
        if (sellerMapper.findByEmail(req.email()) != null) {
            throw ApiException.conflict("EMAIL_TAKEN", "Email already registered");
        }
        Seller seller = new Seller();
        seller.setEmail(req.email());
        seller.setPasswordHash(passwordEncoder.encode(req.password()));
        seller.setDisplayName(req.displayName());
        sellerMapper.insert(seller);
        String token = jwtService.generateToken(seller.getId(), seller.getEmail());
        return new AuthResponse(seller.getId(), seller.getEmail(), token);
    }

    public AuthResponse login(LoginRequest req) {
        Seller seller = sellerMapper.findByEmail(req.email());
        if (seller == null || !passwordEncoder.matches(req.password(), seller.getPasswordHash())) {
            throw ApiException.unauthorized("Invalid credentials");
        }
        String token = jwtService.generateToken(seller.getId(), seller.getEmail());
        return new AuthResponse(seller.getId(), seller.getEmail(), token);
    }
}
