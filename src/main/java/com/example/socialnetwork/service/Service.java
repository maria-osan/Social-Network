package com.example.socialnetwork.service;

import com.example.socialnetwork.domain.FriendRequest;
import com.example.socialnetwork.domain.Friendship;
import com.example.socialnetwork.domain.Message;
import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.dto.FriendshipFilterDTO;
import com.example.socialnetwork.dto.UserFilterDTO;
import com.example.socialnetwork.repository.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Service implements ServiceInterface {
    private final UserRepository userRepo;
    private final FriendshipRepository friendshipRepo;
    private final Repository<Long, Message> messageRepo;

    public Service(UserRepository userRepo, FriendshipRepository friendshipRepo, Repository<Long, Message> messageRepo) {
        this.userRepo = userRepo;
        this.friendshipRepo = friendshipRepo;
        this.messageRepo = messageRepo;
    }

    public Repository<Long, User> getUserRepo() {
        return userRepo;
    }

    public Repository<Long, Friendship> getFriendshipRepo() {
        return friendshipRepo;
    }

    @Override
    public boolean addUser(String firstName, String lastName, String email) {
        try{
            if(email == null)
                throw new IllegalArgumentException("Invalid email - it is null!");
            else if (getUserByEmail(email) != null)
                throw new IllegalArgumentException("The email is already taken!");

           User user = new User(firstName, lastName, email);
           userRepo.save(user);
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    @Override
    public boolean addUser(User user) {
        try{
            if(user.getEmail() == null)
                throw new IllegalArgumentException("Invalid email - it is null!");
            else if (getUserByEmail(user.getEmail()) != null)
                throw new IllegalArgumentException("The email is already taken!");
            else if (user.getPassword() == null)
                throw new IllegalArgumentException("Invalid password - it is null!");

            userRepo.save(user);
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    @Override
    public boolean deleteUser(String email) {
        try{
            User user = getUserByEmail(email);

            if(user == null)
                throw new IllegalArgumentException("There is no user with this email!");

            for(Friendship f: friendList(user)) {
                friendshipRepo.delete(f.getId());
            }

            userRepo.delete(user.getId());
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }

    }

    @Override
    public boolean createFriendship(String email1, String email2) {
        try{
            if(email1 == null || email2 == null)
                throw new IllegalArgumentException("Invalid email - it is null!");
            else if(email1.equals(email2))
                throw new IllegalArgumentException("Error - the emails are identical!");
            else if(getFriendshipByEmail(email1, email2) != null)
                throw new IllegalArgumentException("Error - the friendship already exists!");

            User user1 = getUserByEmail(email1);
            User user2 = getUserByEmail(email2);
            if(user1 == null || user2 == null)
                throw new IllegalArgumentException("Error - user not found!");

            Friendship fr = new Friendship(user1.getId(), user2.getId());
            friendshipRepo.save(fr);
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    @Override
    public boolean createFriendRequest(String email1, String email2) {
        try{
            Friendship fr = getFriendshipByEmail(email1, email2);
            if(fr != null && fr.getFriendRequestStatus() != FriendRequest.REJECTED)
                throw new Exception("The friend request exists and it is not rejected");
            else if(fr != null && fr.getFriendRequestStatus() == FriendRequest.PENDING) {
                fr.setFriendRequestStatus(FriendRequest.PENDING);
                friendshipRepo.update(fr);
                return true;
            } else {
                User user1 = getUserByEmail(email1);
                User user2 = getUserByEmail(email2);

                Friendship f  = new Friendship(user1, user2);
                friendshipRepo.save(f);
                return true;
            }
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    @Override
    public boolean respondFriendRequest(Friendship friendshipRequest, FriendRequest response) {
        try{
            if (friendshipRepo.findOne(friendshipRequest.getId()) == null)
                throw new Exception("The friend request does not exist");
            else if (friendshipRequest.getFriendRequestStatus() != FriendRequest.PENDING)
                throw new Exception("The friend request is not pending");

            friendshipRequest.setFriendRequestStatus(response);
            friendshipRepo.update(friendshipRequest);
            return true;
        } catch (Exception e) {

            System.out.println(e);
            return false;
        }
    }

    @Override
    public  boolean deleteFriendship(String email1, String email2) {
        try{
            if(email1 == null || email2 == null)
                throw new IllegalArgumentException("Error - null email!");
            else if(email1.equals(email2))
                throw new IllegalArgumentException("Error - the emails are identical!");

            User user1 = getUserByEmail(email1);
            User user2 = getUserByEmail(email2);
            if(user1 == null || user2 == null)
                throw new IllegalArgumentException("Error - user not found!");

            Friendship f = getFriendshipByEmail(email1, email2);
            if(f == null)
                throw new IllegalArgumentException("Error - friendship not found!");

            friendshipRepo.delete(f.getId());
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    @Override
    public Iterable<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public Iterable<Friendship> getAllFriendships() {
        return friendshipRepo.findAll();
    }

    @Override
    public Iterable<Message> getAllMessages() {
        return messageRepo.findAll();
    }

    @Override
    public int numberOfCommunities() {
        List<List<User>> comunities = getAllCommunities();
        return comunities.size();
    }

    /**
     * DFS algorithm - get the component that includes a given vertex
     * @param user - the vertex where we start
     * @param visited - set of vertexes already visited
     * @param adjacencyList - the adjacency list
     * @param currentComponent - a list where we put thne visited vertexes
     */
    private static void dfs(User user, Set<User> visited, Map<User, List<User>> adjacencyList, List<User> currentComponent) {
        visited.add(user);
        currentComponent.add(user);

        adjacencyList.getOrDefault(user, Collections.emptyList()).forEach(friend -> {
            if(!visited.contains(friend))
                dfs(friend, visited, adjacencyList, currentComponent);
        });
    }

    @Override
    public List<List<User>> mostSociableCommunity() {
        List<List<User>> communities = getAllCommunities();
        List<List<User>> biggestCommunity = new ArrayList<>();
        final int[] maxCommunitySize = {0};

        communities.forEach(currentCommunity -> {
            int currentCommunitySize = currentCommunity.size();
            if (currentCommunitySize > maxCommunitySize[0]) {
                biggestCommunity.clear();
                biggestCommunity.add(currentCommunity);
                maxCommunitySize[0] = currentCommunitySize;
            } else if (currentCommunitySize == maxCommunitySize[0]) {
                biggestCommunity.add(currentCommunity);
            }
        });

        return biggestCommunity;
    }

    @Override
    public List<List<User>> getAllCommunities() {
        Map<User, List<User>> adjacencyList = new HashMap<>();

        // Populate adjacency list based on friendships
        for (Friendship friendship : getAllFriendships()) {
            User u1 = userRepo.findOne(friendship.getUser1_id());
            User u2 = userRepo.findOne(friendship.getUser2_id());
            adjacencyList.computeIfAbsent(u1, k -> new ArrayList<>()).add(u2);
            adjacencyList.computeIfAbsent(u2, k -> new ArrayList<>()).add(u1);
        }

        Set<User> visited = new HashSet<>();
        List<List<User>> components = new ArrayList<>();

        for (User user : getAllUsers()) {
            if (!visited.contains(user)) {
                List<User> currentComponent = new ArrayList<>();
                dfs(user, visited, adjacencyList, currentComponent);
                components.add(currentComponent);
            }
        }

        return components;
    }

    @Override
    public User getUserById(Long id) {
        Iterable<User> list = userRepo.findAll();

        for (User user : list)
            if(user.getId().equals(id))
                return user;

        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        Iterable<User> list = userRepo.findAll();

        for (User user : list)
            if(user.getEmail().equals(email))
                return user;

        return null;
    }

    @Override
    public Friendship getFriendshipByEmail(String email1, String email2) {
        Iterable<Friendship> list = friendshipRepo.findAll();
        User user1 = getUserByEmail(email1);
        User user2 = getUserByEmail(email2);

        for (Friendship fr : list)
            if(fr.getUser1_id().equals(user1.getId()) && fr.getUser2_id().equals(user2.getId()) ||
                    fr.getUser1_id().equals(user2.getId()) && fr.getUser2_id().equals(user1.getId()))
                return fr;

        return null;
    }

    @Override
    public ArrayList<Friendship> friendList(User user) {
        Collection<Friendship> friendships = (Collection<Friendship>) friendshipRepo.findAll();
        return friendships.stream()
                .filter(x -> x.getUser1_id().equals(user.getId()) || x.getUser2_id().equals(user.getId())).collect(Collectors.toCollection(ArrayList::new));

    }

    @Override
    public ArrayList<Friendship> friendRequestList(User user) {
        Collection<Friendship> friendships = (Collection<Friendship>) friendshipRepo.findAll();
        return friendships.stream().filter(x -> (x.getUser1_id().equals(user.getId()) || x.getUser2_id().equals(user.getId()))).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Map<LocalDateTime, User> friendListFrom(User user, Integer month) {
        ArrayList<Friendship> friendList = friendList(user);

        return friendList.stream()
                .filter(f -> f.getFriendsFrom().getMonthValue() == month)
                .collect(Collectors.toMap(Friendship::getFriendsFrom, f -> {
                    if (f.getUser1_id().equals(user.getId())) {
                        if(userRepo.findOne(f.getUser2_id()) == null)
                            throw new RuntimeException("User not found");
                        return userRepo.findOne(f.getUser2_id());

                    } else {
                        if(userRepo.findOne(f.getUser1_id()) == null)
                            throw new RuntimeException("User not found");
                        return userRepo.findOne(f.getUser1_id());
                    }
                }));
    }



    @Override
    public boolean addMessage(String email_from, List<String> emails_to, String message) {
        try {
            User from = getUserByEmail(email_from);
            List<User> toUsers = new ArrayList<>();
            for (String email : emails_to) {
                User u = getUserByEmail(email);
                if(u != null) {
                    toUsers.add(u);
                } else {
                    throw new Exception("Email does not exist");
                }
            }

            if (from == null || toUsers.isEmpty())
                throw new Exception("Email does not exist");
            if (Objects.equals(message, ""))
                throw new Exception("Message is empty");

            Message msg = new Message(from, toUsers, message);
            messageRepo.save(msg);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean addMessage(String email_from, List<String> emails_to, String message, Message replyTo) {
        try {
            User from = getUserByEmail(email_from);
            List<User> toUsers = new ArrayList<>();
            for (String email : emails_to) {
                User u = getUserByEmail(email);
                if(u != null) {
                    toUsers.add(u);
                } else {
                    throw new Exception("Email does not exist");
                }
            }

            if (from == null || toUsers.isEmpty())
                throw new Exception("Email does not exist");
            if (Objects.equals(message, ""))
                throw new Exception("Message is empty");

            Message msg = new Message(from, toUsers, message, replyTo);
            messageRepo.save(msg);

            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public ArrayList<Message> getMessagesBetweenTwoUsers(String email1, String email2) {
        if (getUserByEmail(email1) == null || getUserByEmail(email2) == null)
            return new ArrayList<>();

        Collection<Message> messages = (Collection<Message>) messageRepo.findAll();
        return messages.stream()
                .filter(x -> (x.getFrom().getEmail().equals(email1) && userFoundByEmail(x.getTo(), email2)) ||
                        (x.getFrom().getEmail().equals(email2) && userFoundByEmail(x.getTo(), email1)))
                .sorted(Comparator.comparing(Message::getDate))
                .collect(Collectors.toCollection(ArrayList::new));

    }

    @Override
    public boolean updateMessage(Message msg) {
        messageRepo.update(msg);
        return true;
    }

    boolean userFoundByEmail(List<User> users, String email) {
        for (User u: users) {
            if (u.getEmail().equals(email))
                return true;
        }

        return false;
    }

    public Page<User> findAllUsers(Pageable pageable) {
        return userRepo.findAll(pageable);
    }
    public Page<User> findAll(Pageable pageable, UserFilterDTO filter) {
        return userRepo.findAll(pageable, filter);
    }

    public Page<Friendship> findAllFriendship(Pageable pageable) {
        return friendshipRepo.findAll(pageable);
    }
    public Page<Friendship> findAll(Pageable pageable, FriendshipFilterDTO filter) {
        return friendshipRepo.findAll(pageable, filter);
    }
}
