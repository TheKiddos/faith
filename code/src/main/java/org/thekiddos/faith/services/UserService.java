package org.thekiddos.faith.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.thekiddos.faith.dtos.UserDTO;
import org.thekiddos.faith.exceptions.UserAlreadyExistException;
import org.thekiddos.faith.mappers.UserMapper;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.repositories.UserRepository;

import java.util.List;

@Service
public class UserService implements UserDetailsService {
    private final UserMapper userMapper = UserMapper.INSTANCE;
    private final UserRepository userRepository;

    public UserService( UserRepository userRepository ) {
        this.userRepository = userRepository;
    }

    public User createUser( UserDTO userDTO ) {
        if ( userRepository.findById( userDTO.getEmail() ).isPresent() )
            throw new UserAlreadyExistException( "A user with this email already exists" );

        User user = userMapper.userDtoToUser( userDTO );
        return userRepository.save( user );
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername( String email ) throws UsernameNotFoundException {
        return userRepository.findById( email ).orElseThrow( () -> new UsernameNotFoundException( "No user with specified email was found" ) );
    }
}
