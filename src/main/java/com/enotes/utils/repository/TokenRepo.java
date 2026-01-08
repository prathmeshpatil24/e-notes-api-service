package com.enotes.utils.repository;

import com.enotes.utils.entity.TokenDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface TokenRepo extends JpaRepository<TokenDetails, Integer> {

    Optional<TokenDetails>findByToken(String token);

    void deleteByExpiryDateBefore(Date date);


}
