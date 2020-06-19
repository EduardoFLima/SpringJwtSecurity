package edu.learn.jwt.auth.repositories;

import edu.learn.jwt.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

}
