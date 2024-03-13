package com.example.socialnetwork.dto;

import java.util.Optional;

public class UserFilterDTO {
    private Optional<String> firstName = Optional.empty();
    private Optional<String> lastName = Optional.empty();
    private Optional<String> email = Optional.empty();

    public Optional<String> getFirstName() {
        return firstName;
    }

    public void setFirstName(Optional<String> firstName) {
        this.firstName = firstName;
    }

    public Optional<String> getLastName() {
        return lastName;
    }

    public void setLastName(Optional<String> lastName) {
        this.lastName = lastName;
    }

    public Optional<String> getEmail() {
        return email;
    }

    public void setEmail(Optional<String> email) {
        this.email = email;
    }
}
