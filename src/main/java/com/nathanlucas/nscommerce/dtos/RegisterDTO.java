package com.nathanlucas.nscommerce.dtos;

import java.time.LocalDate;

public class RegisterDTO {

    private String name;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private String password;

    public RegisterDTO() {
    }

    public RegisterDTO(String name, String email, String phone, LocalDate birthDate, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.birthDate = birthDate;
        this.password = password;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
