package com.tips.InventoryManagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tips.InventoryManagement.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
	Optional<User> findByEmail (String email);
	@Query("SELECT u FROM User u WHERE u.createdBy.id = :createdById")
	List<User> findByCreatedBy(@Param("createdById") Integer createdById);

	 User findByUserType(String userType);
	Page<User> findByCreatedBy(User createdBy, PageRequest of);
}
