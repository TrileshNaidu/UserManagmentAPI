package com.example.usermanagementapi.controller;

import com.example.usermanagementapi.model.User;
import com.example.usermanagementapi.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


import java.util.List;
import java.util.Optional;
@CrossOrigin(origins = {
        "http://localhost:8080",
        "http://127.0.0.1:5500"
})
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired //inject the UserRepository dependency
    private UserRepository userRepository;


    @PostMapping
    public ResponseEntity<List<User>> createUser(@Valid @RequestBody List<@Valid User> users){
        List<User> savedUsers=userRepository.saveAll(users);
        return  ResponseEntity.status(HttpStatus.CREATED).body(savedUsers);
    }

    @GetMapping
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id){
        Optional<User> user=userRepository.findById(id);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id,@Valid @RequestBody User userDetails){
        return userRepository.findById(id).map(existingUser ->{
            existingUser.clone(userDetails.getName());
            existingUser.setEmail(userDetails.getEmail());
            User updatedUser= userRepository.save(existingUser);

            return ResponseEntity.ok(updatedUser);
        }).orElse(ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        if(userRepository.existsById(id)){
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/page")
    public Page<User> getUsers(Pageable pageable){
        return  userRepository.findAll(pageable);
    }


}