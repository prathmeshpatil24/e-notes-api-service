package com.enotes.service.impl;

import com.enotes.entity.TokenDetails;
import com.enotes.repo.JwtTokenRepo;
import com.enotes.service.TokenBlockService;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.enotes.security.JWTService.SECRET_KEY;

@Service
public class TokenBlockServiceImpl implements TokenBlockService {

    @Autowired
   private JwtTokenRepo jwtTokenRepo;

    @Override
    public void blockToken(String token) {

        try {

            Date expiry = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            TokenDetails tokenDetails = new TokenDetails();
            tokenDetails.setToken(token);
            tokenDetails.setExpiryDate(expiry);

            TokenDetails saved = jwtTokenRepo.save(tokenDetails);
            System.out.println(saved.getToken());
            System.out.println(saved.getExpiryDate());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteExpiredToken() {
          jwtTokenRepo.deleteByExpiryDateBefore(new Date());
    }

    @Override
    public boolean isTokenBlocked(String token) {
        return jwtTokenRepo.findByToken(token).isPresent();
    }
}
