package com.example.socialnetwork.domain.validators;

import com.example.socialnetwork.domain.User;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        validateFirstName(entity.getFirstName());
        validateLastName(entity.getLastName());
        validateEmail(entity.getEmail());
    }

    private void validateFirstName(String name) throws ValidationException {
        if(name == null)
            throw new ValidationException("Invalid name - first name is null!");
        else if(name.isEmpty())
            throw new ValidationException("Invalid name - first name is empty!");
        else if(name.length() >= 50)
            throw new ValidationException("Invalid name - first name too long!");
        else if(! name.matches("[a-zA-Z]+"))
            throw new ValidationException("Invalid firs name - first name must contain only letters!");
    }

    private void validateLastName(String name) throws ValidationException {
        if(name == null)
            throw new ValidationException("Invalid name - last name is null!");
        else if(name.isEmpty())
            throw new ValidationException("Invalid name - last name is empty!");
        else if(name.length() > 50)
            throw new ValidationException("Invalid name - last name too long!");
        else if(! name.matches("[a-zA-Z]+"))
            throw new ValidationException("Invalid firs name - last name must contain only letters!");
    }

    private void validateEmail(String email) throws ValidationException {
        if(email == null)
            throw new ValidationException("Invalid email - it is null!");
        else if(email.isEmpty())
            throw new ValidationException("Invalid email - it is empty!");
        else if(email.length() < 5)
            throw new ValidationException("Invalid email - it is too short!");
        else if(email.length() > 60)
            throw new ValidationException("Invalid email - it is too long!");
        else if(! email.matches("[a-zA-Z]+[a-zA-Z0-9._]*@[a-z]+.[a-z]+"))
            throw new ValidationException("Invalid email address!");
    }
}
