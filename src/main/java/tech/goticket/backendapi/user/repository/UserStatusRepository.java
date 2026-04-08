package tech.goticket.backendapi.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.goticket.backendapi.user.UserStatus;

@Repository
public interface UserStatusRepository extends JpaRepository<UserStatus, Long> {
    UserStatus findByName(String name);
}
