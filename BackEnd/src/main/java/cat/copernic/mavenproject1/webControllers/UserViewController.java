/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.mavenproject1.webControllers;


import cat.copernic.mavenproject1.Entity.User;
import cat.copernic.mavenproject1.logic.UserLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 *
 * @author carlo
 */

@Controller
public class UserViewController {

    @Autowired
    private UserLogic userLogic;

    @GetMapping("/users")
    public String showUsers(Model model) {
        List<User> users = userLogic.findAllUsers();
        model.addAttribute("users", users);
        return "users"; // Mapea al template users.html
    }
}
