package com.nathanlucas.nscommerce.utils;

import com.nathanlucas.nscommerce.dtos.UserDTO;
import com.nathanlucas.nscommerce.entities.Role;
import com.nathanlucas.nscommerce.entities.User;

import java.time.LocalDate;

public class UserFactory {

    public static User createClient() {
        User result = new User(1L,"Maria Brown",
                "maria@gmail.com","988888888",
                LocalDate.of(2001,7,25),"$2a$10$bfdMGwRrYmxLqt/PK73GieHr2dc1uUv/gaBz2IQuJdChIEmATPYOW");
        result.getRoles().add(new Role(1L,"ROLE_CLIENT"));
        return result;
    }

    public static User createAdmin() {
        User result = new User(2L,"Alex Green",
                "alex@gmail.com","977777777",
                LocalDate.of(1987,12,13),"$2a$10$bfdMGwRrYmxLqt/PK73GieHr2dc1uUv/gaBz2IQuJdChIEmATPYOW");
        result.getRoles().add(new Role(1L,"ROLE_CLIENT"));
        result.getRoles().add(new Role(2L,"ROLE_ADMIN"));
        return result;
    }
    public static UserDTO createClientDTO() {
        return new UserDTO(createClient());
    }
    public static UserDTO createAdminDTO() {
        return new UserDTO(createAdmin());
    }
}
