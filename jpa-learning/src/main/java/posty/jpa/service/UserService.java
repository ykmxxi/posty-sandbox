package posty.jpa.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import posty.jpa.domain.example.User;
import posty.jpa.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public int count() {
        return userRepository.findAll().stream()
            .map(User::getPosts)
            .filter(List::isEmpty)
            .toList()
            .size();
    }
}
