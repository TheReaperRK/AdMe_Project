/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package cat.copernic.mavenproject1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;


/**
 *
 * @author carlo
 */
@SpringBootApplication
@ComponentScan
public class AdMeApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(AdMeApplication.class, args);    }
}