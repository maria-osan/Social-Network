package com.example.socialnetwork;

import com.example.socialnetwork.domain.Message;
import com.example.socialnetwork.domain.validators.FriendshipValidator;
import com.example.socialnetwork.domain.validators.UserValidator;
import com.example.socialnetwork.repository.*;
import com.example.socialnetwork.service.Service;
import com.example.socialnetwork.ui.ConsoleUI;

public class Main {

    public static void main(String[] args) {
        UserRepository userRepo = new UserDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "postgres", new UserValidator());
        FriendshipRepository friendshipRepo = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "postgres", new FriendshipValidator());
        Repository<Long, Message> messageRepo = new MessageDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "postgres", userRepo);
        //InMemoryRepository<UUID, User> userRepo = new InMemoryRepository<>(new UserValidator());
        //InMemoryRepository<UUID, Friendship> friendshipRepo = new InMemoryRepository<>(new FriendshipValidator());

        Service srv = new Service(userRepo, friendshipRepo, messageRepo);
        ConsoleUI console = new ConsoleUI(srv);

        console.run();
    }
}
