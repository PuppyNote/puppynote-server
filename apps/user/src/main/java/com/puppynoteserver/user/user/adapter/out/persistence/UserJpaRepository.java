package com.puppynoteserver.user.user.adapter.out.persistence;


import com.puppynoteserver.user.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.useYn = 'Y'")
    List<User> findAll();

    @Query("select distinct u from User u left join fetch u.pushes where u.useYn = 'Y'")
    List<User> findAllWithPushes();

	@Query("select u from User u where u.email = :email and u.useYn = 'Y'")
	Optional<User> findByEmail(@Param("email") String email);

	@Query("select u from User u where u.email = :email and u.nickName = :nickName and u.useYn = 'Y'")
	Optional<User> findByEmailAndNickName(@Param("email") String email, @Param("nickName") String nickName);

	@Query("select u from User u where u.id = :id and u.useYn = 'Y'")
	Optional<User> findById(@Param("id") Long id);

    @Query("select count(u) > 0 from User u where u.email = :email and u.useYn = 'Y'")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.email LIKE %:email% AND u.useYn = 'Y'")
    List<User> findAllByEmailLike(@Param("email") String email);
}
