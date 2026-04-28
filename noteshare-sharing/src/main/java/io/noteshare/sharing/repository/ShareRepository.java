package io.noteshare.sharing.repository;

import io.noteshare.sharing.model.Share;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShareRepository extends JpaRepository<Share, Long> {
    Optional<Share> findByToken(String token);
}
