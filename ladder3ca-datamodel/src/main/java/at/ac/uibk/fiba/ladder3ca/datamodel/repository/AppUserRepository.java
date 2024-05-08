package at.ac.uibk.fiba.ladder3ca.datamodel.repository;

import at.ac.uibk.fiba.ladder3ca.datamodel.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    @Query("SELECT appUser FROM AppUser appUser WHERE appUser.emailAddress = ?1")
    Optional<AppUser> findByEmail(String addr);
}
