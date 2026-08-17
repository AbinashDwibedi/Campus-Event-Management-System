package com.abinash.campus_management.services;

import com.abinash.campus_management.dto.MyUserDto;
import com.abinash.campus_management.entity.MyUser;
import com.abinash.campus_management.exception.ApiException;
import com.abinash.campus_management.repository.MyUserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserService {
    private final MyUserRepository myUserRepository;
    private final ModelMapper modelMapper;

    public MyUserDto registerUser(MyUserDto userDto) {
        MyUser myUser = modelMapper.map(userDto, MyUser.class);
        MyUser responseUser = myUserRepository.save(myUser);
        return modelMapper.map(responseUser, MyUserDto.class);
    }

    @Transactional
    public void deleteUser(String name) {
        MyUser myUser = myUserRepository.findByName(name)
                .orElseThrow(() -> new ApiException(HttpStatus.OK, "Failed to find the user"));
        myUserRepository.delete(myUser);
    }
}
