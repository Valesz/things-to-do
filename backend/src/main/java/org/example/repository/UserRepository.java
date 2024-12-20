package org.example.repository;

import java.util.Optional;
import org.example.model.User;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("userRepository")
public interface UserRepository extends CrudRepository<User, Long>
{
	@Query("SELECT * FROM \"user\" WHERE username = :username")
	Optional<User> findByUsername(@Param("username") String username);

	@Query("SELECT * FROM \"user\" LIMIT :LIMIT OFFSET :OFFSET")
	Iterable<User> getAllUsers(@Param("OFFSET") long offset, @Param("LIMIT") long limit);
}
