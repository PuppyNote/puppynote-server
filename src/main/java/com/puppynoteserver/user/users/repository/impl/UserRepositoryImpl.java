package com.puppynoteserver.user.users.repository.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.repository.UserJpaRepository;
import com.puppynoteserver.user.users.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

	private final UserJpaRepository userJpaRepository;

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll();
    }

    @Override
    public List<User> findAllWithPushes() {
        return userJpaRepository.findAllWithPushes();
    }

    @Override
	public User save(User user) {
		return userJpaRepository.save(user);
	}

	@Override
	public Optional<User> findByEmail(String email) {
		return userJpaRepository.findByEmail(email);
	}

	@Override
	public Optional<User> findByEmailAndNickName(String email, String nickName) {
		return userJpaRepository.findByEmailAndNickName(email, nickName);
	}

	@Override
	public Optional<User> findById(Long id) {
		return userJpaRepository.findById(id);
	}

	@Override
	public void deleteAllInBatch() {
		userJpaRepository.deleteAllInBatch();
	}

	@Override
	public void saveAll(List<User> users) {
		userJpaRepository.saveAll(users);
	}

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }
}
