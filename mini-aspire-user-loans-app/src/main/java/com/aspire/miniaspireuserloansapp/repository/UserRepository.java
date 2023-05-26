package com.aspire.miniaspireuserloansapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aspire.miniaspireuserloansapp.model.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	
	@Query(value = "SELECT * FROM USERS WHERE USER_NAME = :username AND PASSWORD = :password", nativeQuery = true)
	Optional<User> findUser(String username, String password);
	
	@Query(value = "SELECT * FROM USERS WHERE USER_NAME = :username", nativeQuery = true)
	Optional<User> findUser(String username);

}
