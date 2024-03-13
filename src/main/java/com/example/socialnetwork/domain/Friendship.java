package com.example.socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Friendship extends Entity<Long> {

    private Long id1;
    private Long id2;

    LocalDateTime friendsFrom;

    private FriendRequest friendRequestStatus;

    public Friendship(Long id1, Long id2) {
        this.id1 = id1;
        this.id2 = id2;
        this.friendsFrom = LocalDateTime.now();
        this.friendRequestStatus = FriendRequest.PENDING;
    }

    public Friendship(User user1, User user2) {
        this.id1 = user1.getId();
        this.id2 = user2.getId();
        this.friendsFrom = LocalDateTime.now();
        this.friendRequestStatus = FriendRequest.PENDING;
    }

    public Friendship(Long id1, Long id2, LocalDateTime friendsFrom) {
        this.id1 = id1;
        this.id2 = id2;
        this.friendsFrom = friendsFrom;
        this.friendRequestStatus = FriendRequest.PENDING;
    }

    public Friendship(Long id1, Long id2, FriendRequest friendRequestStatus) {
        this.id1 = id1;
        this.id2 = id2;
        this.friendsFrom = LocalDateTime.now();
        this.friendRequestStatus = friendRequestStatus;
    }

    public Friendship(Long id1, Long id2, LocalDateTime friendsFrom, FriendRequest friendRequestStatus) {
        this.id1 = id1;
        this.id2 = id2;
        this.friendsFrom = friendsFrom;
        this.friendRequestStatus = friendRequestStatus;
    }

    public Long getUser1_id() {
        return id1;
    }

    public Long getUser2_id() {
        return id2;
    }

    public void setUser1_id(Long id1) {
        this.id1 = id1;
    }

    public void setUser2_id(Long id2) {
        this.id2 = id2;
    }

    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }

    public void setFriendsFrom(LocalDateTime friendsFrom) {
        this.friendsFrom = friendsFrom;
    }

    public FriendRequest getFriendRequestStatus() {
        return friendRequestStatus;
    }

    public void setFriendRequestStatus(FriendRequest friendRequestStatus) {
        this.friendRequestStatus = friendRequestStatus;
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "user1_id=" + id1 +
                ", user2_id=" + id2 +
                ", friends_from=" + friendsFrom +
                ", status=" + friendRequestStatus +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        return Objects.equals(id1, that.id1) && Objects.equals(id2, that.id2) ||
                Objects.equals(id1, that.id2) && Objects.equals(id2, that.id1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id1, id2);
    }
}
