package com.puppynoteserver.user.user.application.port.out.persistence;


import com.puppynoteserver.user.user.domain.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();

    List<User> findAllWithPushes();

	User save(User user);

	Optional<User> findByEmail(String email);

	Optional<User> findByEmailAndNickName(String email, String nickName);

	Optional<User> findById(Long id);

	void deleteAllInBatch();

	void saveAll(List<User> users);

    boolean existsByEmail(String email);

    List<User> findAllByEmailLike(String email);
}
