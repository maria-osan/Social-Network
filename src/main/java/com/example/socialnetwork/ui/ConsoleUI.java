package com.example.socialnetwork.ui;

import com.example.socialnetwork.domain.Friendship;
import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.service.ServiceInterface;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI extends AbstractUI{
    private Scanner scanner;
    public ConsoleUI(ServiceInterface srv) {
        super(srv);
        scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        scanner = new Scanner(System.in);

        while(true) {
            printMenu();

            System.out.println("Command: ");
            String command = scanner.next();
            System.out.println();

            User user;
            String firstName, lastName;
            String email1, email2, email;
            Integer month;
            Iterable<User> userList;
            Iterable<Friendship> friendshipList;
            List<List<User>> communityList;

            switch (command) {
                case "1":
                    // Add user
                    try {
                        firstName = readFirstName();
                        lastName = readLastName();
                        email = readEmail();
                        srv.addUser(firstName, lastName, email);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    break;
                case "2":
                    // Delete user
                    try{
                        email = readEmail();
                        srv.deleteUser(email);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    break;
                case "3":
                    // Create a friendship
                    try{
                        email1 = readEmail();
                        email2 = readEmail();
                        srv.createFriendship(email1, email2);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    break;
                case "4":
                    // Delete friendship
                    try{
                        email1 = readEmail();
                        email2 = readEmail();
                        srv.deleteFriendship(email1, email2);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    break;
                case "5":
                    // Number of communities
                    System.out.println(srv.numberOfCommunities());
                    break;
                case "6":
                    // The most sociable community
                    communityList = srv.mostSociableCommunity();

                    for(List<User> community : communityList) {
                        System.out.println("Community: ");
                        community.forEach(System.out::println);
                        System.out.println();
                    }
                    break;
                case "pu":
                    // Print all users
                    userList = srv.getAllUsers();
                    userList.forEach(System.out::println);
                    break;
                case "pf":
                    // Print all friendships
                    friendshipList = srv.getAllFriendships();
                    friendshipList.forEach(System.out::println);
                    break;
                case "pm":
                    // Print all friends of a user from a specific month of the year
                    try{
                        email = readEmail();
                        user = srv.getUserByEmail(email);
                        if(user == null)
                            throw new IllegalArgumentException("There is no user with this email");
                        month = readMonth();
                        if (month >0 && month < 13) {
                            srv.friendListFrom(user, month).forEach((date, friend) ->
                                    System.out.println("Surname: " + friend.getLastName() + "| Forename: " + friend.getFirstName()
                                            + "| Date: " + date));
                        } else System.out.println("There is no such month");
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    break;
                case "x":
                    return;
                default:
                    break;
            }
        }
    }

    public void printMenu() {
        System.out.println();
        System.out.println("------------------------------");
        System.out.println("-            MENU            -");
        System.out.println("-                            -");
        System.out.println("- 1. Add user                -");
        System.out.println("- 2. Delete user             -");
        System.out.println("- 3. Add friendship          -");
        System.out.println("- 4. Remove friendship       -");
        System.out.println("- 5. Number of communities   -");
        System.out.println("- 6. Most sociable community -");
        System.out.println("- pu. Print all users        -");
        System.out.println("- pf. Print all friendships  -");
        System.out.println("- pm. Print by month         -");
        System.out.println("- x. Exit                    -");
        System.out.println("------------------------------");
        System.out.println();
    }

    public String readFirstName() {
        System.out.println("First name: ");
        return scanner.next();
    }

    public String readLastName() {
        System.out.println("Last name: ");
        return scanner.next();
    }

    public String readEmail() {
        System.out.println("Email: ");
        return scanner.next();
    }

    public Integer readMonth() {
        System.out.println("Month - number:");
        return scanner.nextInt();
    }

}
