package com.puppynoteserver.user.user.adapter.out.persistence;

import com.puppynoteserver.user.user.application.port.out.persistence.UserRepository;
import com.puppynoteserver.user.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

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
	public List<User> findAllByIds(List<Long> ids) {
		return userJpaRepository.findAllById(ids);
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

    @Override
    public List<User> findAllByEmailLike(String email) {
        return userJpaRepository.findAllByEmailLike(email);
    }
}
