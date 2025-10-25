package com.org.AirBnB.dto;

import com.org.AirBnB.entities.Guest;
import com.org.AirBnB.entities.enums.Role;
import jakarta.persistence.*;

import java.util.Set;

public class UserDTO {

    private Long userId;

    private String email;

    private String password;

    private String name;

  //  private Set<Role> roles;

//    private Set<Guest> guestList;


}
