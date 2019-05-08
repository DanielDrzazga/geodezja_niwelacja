package drzazga.daniel.geodezja.services.impl;

import drzazga.daniel.geodezja.Dtos.UserDto;
import drzazga.daniel.geodezja.Dtos.UserPasswordChangeDto;
import drzazga.daniel.geodezja.Dtos.UserRegistrationDto;
import drzazga.daniel.geodezja.model.Role;
import drzazga.daniel.geodezja.model.User;
import drzazga.daniel.geodezja.repositories.RoleRepository;
import drzazga.daniel.geodezja.repositories.UserRepository;
import drzazga.daniel.geodezja.services.UserService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MapperFacade mapperFacade;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, MapperFacade mapperFacade) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.mapperFacade = mapperFacade;
    }

    @Override
    public boolean isEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserDto findByEmail(String email) {
        return mapperFacade.map(userRepository.findByEmail(email), UserDto.class);
    }

    @Override
    public UserPasswordChangeDto findByEmailPassChanger(String email) {
        return mapperFacade.map(userRepository.findByEmail(email), UserPasswordChangeDto.class);
    }

    @Override
    public void saveUser(UserRegistrationDto userRegistrationDto) {
        User user = mapperFacade.map(userRegistrationDto, User.class);
        user.setPassword(passwordEncoder.encode(userRegistrationDto.getPassword()));
        user.setActive(1);

        Role role = roleRepository.findByRole("ROLE_USER");
        user.setRoles(new HashSet<Role>(Arrays.asList(role)));

        userRepository.save(user);
    }

    @Override
    public void updateUserPassword(String newPassword, String email) {
        userRepository.updateUserPassword(passwordEncoder.encode(newPassword), email);
    }

    @Override
    public void updateUserProfile(String newName, String newLastName, String newEmail, Long id) {
        userRepository.updateUserProfile(newName, newLastName, newEmail, id);
    }

    @Override
    public Collection<UserDto> findAll() {
        Iterable<User> users = userRepository.findAll();

        for (User user : users) {
            System.out.println(user.getRoles());
        }

        return StreamSupport.stream(users.spliterator(),true)
                .map(user -> mapperFacade.map(user,UserDto.class))
                .collect(Collectors.toList());
    }


}
