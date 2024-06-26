package inflearn.interview.repository;

import inflearn.interview.domain.FCM;
import inflearn.interview.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FCMRepository extends JpaRepository<FCM, Long> {
    Optional<FCM> findByUserUserId(Long userId);

    Optional<FCM> findByUser(User user);
}