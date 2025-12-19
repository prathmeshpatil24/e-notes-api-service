package com.enotes.service.impl;

import com.enotes.entity.TokenDetails;
import com.enotes.repo.TokenRepo;
import com.enotes.security.JWTService;
import com.enotes.service.TokenBlockService;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class TokenBlockServiceImpl implements TokenBlockService {

    @Autowired
   private TokenRepo tokenRepo;

    @Autowired
    private JWTService jwtService;

    @Override
    public void blockToken(String token) {

        try {

            Date expiry = Jwts.parserBuilder()
                    .setSigningKey(jwtService.getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            TokenDetails tokenDetails = new TokenDetails();
            tokenDetails.setToken(token);
            tokenDetails.setExpiryDate(expiry);

            TokenDetails saved = tokenRepo.save(tokenDetails);
            System.out.println(saved.getToken());
            System.out.println(saved.getExpiryDate());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteExpiredToken() {
          tokenRepo.deleteByExpiryDateBefore(new Date());
    }

    @Override
    public boolean isTokenBlocked(String token) {
        return tokenRepo.findByToken(token).isPresent();
    }
}
