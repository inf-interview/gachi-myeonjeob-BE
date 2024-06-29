package inflearn.interview.fcm.infrastructure;

import inflearn.interview.fcm.domain.Fcm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FcmJpaRepository extends JpaRepository<FcmEntity, Long> {

    Optional<FcmEntity> findByUserEntityId(Long userId);
}
