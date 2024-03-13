package com.example.socialnetwork.dto;

import com.example.socialnetwork.domain.FriendRequest;

import java.time.LocalDateTime;
import java.util.Optional;

public class FriendshipFilterDTO {
    private Optional<Long> id1 = Optional.empty();
    private Optional<Long> id2 = Optional.empty();

    private Optional<LocalDateTime> friendsFrom = Optional.empty();

    private Optional<FriendRequest> friendRequestStatus = Optional.empty();


    public Optional<Long> getId1() {
        return id1;
    }

    public void setId1(Optional<Long> id1) {
        this.id1 = id1;
    }

    public Optional<Long> getId2() {
        return id2;
    }

    public void setId2(Optional<Long> id2) {
        this.id2 = id2;
    }

    public Optional<LocalDateTime> getFriendsFrom() {
        return friendsFrom;
    }

    public void setFriendsFrom(Optional<LocalDateTime> friendsFrom) {
        this.friendsFrom = friendsFrom;
    }

    public Optional<FriendRequest> getFriendRequestStatus() {
        return friendRequestStatus;
    }

    public void setFriendRequestStatus(Optional<FriendRequest> friendRequestStatus) {
        this.friendRequestStatus = friendRequestStatus;
    }
}
