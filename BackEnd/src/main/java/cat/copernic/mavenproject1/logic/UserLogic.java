/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.mavenproject1.logic;

import java.util.ArrayList;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cat.copernic.mavenproject1.Entity.User;
import cat.copernic.mavenproject1.repository.UserRepo;

/**
 *
 * @author carlo
 */
@Service
public class UserLogic {
    
    @Autowired
    UserRepo userRepo;
    
     //PasswordEncoder instance(BCrypt)
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public User getUser(Long login)
    {
        User ret = userRepo.findById(login).orElse(null);
        
        return ret;
    }
    
    public List<User> findAllUsers(){
        
        List<User> ret = new ArrayList<>();
        
        ret = userRepo.findAll();
        
        return ret;
        
    }
    
    /***
     * Verifica si un determinat usuari existeix a la BBDD
     * @param id
     * @return 
     */
    public boolean existsById(Long id)
    {
        User p = userRepo.findById(id).orElse(null);
        
        return (p != null);
    }
    
    public void deleteUserById(Long id){
        
        //TODO: validacions de negoci
        
        userRepo.deleteById(id);
        
    }
    
    public boolean userIsUnique (User user){
        
        boolean ret = true;
        
        try {
            userRepo.save(user);
        } catch (Exception E) {
            ret = false;
        }
        
        return ret;
    }
    
    public void tryCreation (User user) {
        if (userIsUnique(user)){
            createUser(user);
        }
    }
    
    public Long createUser(User user) {
        
        // Ecrypts the password
        user.setWord(passwordEncoder.encode(user.getWord()));
        User ret = userRepo.save(user);

        
        return ret.getId();

    }
    
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    public Long saveUser(User user){
        
        //TODO: validacions de negoci
        
        User ret = userRepo.save(user);
        
        return ret.getId();
        
    }
    
    public User getUserById(Long id){
        
        //TODO: validacions de negoci
        
        return userRepo.findById(id).orElse(null);
        
    }
    
    
}
