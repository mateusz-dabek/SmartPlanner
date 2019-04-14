package com.smartplanner.service.implementation;

import com.smartplanner.model.dto.UserDto;
import com.smartplanner.model.entity.Role;
import com.smartplanner.model.entity.User;
import com.smartplanner.repository.RoleRepository;
import com.smartplanner.repository.UserRepository;
import com.smartplanner.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service(value = "UserService")
public class UserServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private ModelMapper modelMapper;

    @Autowired
    private BCryptPasswordEncoder bcryptEncoder;

    @Autowired
    UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this(userRepository, roleRepository, new BCryptPasswordEncoder(), new ModelMapper());
    }

    public UserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            BCryptPasswordEncoder bcryptEncoder,
            ModelMapper modelMapper
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bcryptEncoder = bcryptEncoder;
        this.modelMapper = modelMapper;
    }

    public boolean findUserById(int id) {
        return userRepository.existsById(id);
    }

    public User getUserById(int id) {
        return userRepository.getOne(id);
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                getAuthority(user)
        );
    }

    private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role ->
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()))
        );

        return authorities;
    }

    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        userRepository.findAll().iterator().forEachRemaining(list::add);

        return list;
    }

    @Override
    public void delete(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findById(int id) {
        return userRepository.findById(id).get();
    }

    @Override
    public User saveUser(UserDto userDto) {
        User newUser = modelMapper.map(userDto, User.class);
        newUser.setPassword(bcryptEncoder.encode(newUser.getPassword()));

        List<Role> availableRoles = roleRepository.findAll();
        Role role = availableRoles.stream()
                .filter(x -> "USER".equals(
                        x.getName().name()
                ))
                .findAny()
                .orElse(null);

        if (newUser.getRoles() == null) {
            Set<Role> newUserRoles = new HashSet<>();
            newUserRoles.add(role);
            newUser.setRoles(newUserRoles);
        } else {
            newUser.getRoles().add(role);
        }

        return userRepository.save(newUser);
    }
}
