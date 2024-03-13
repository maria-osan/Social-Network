package com.example.socialnetwork.repository;

import com.example.socialnetwork.domain.Friendship;
import com.example.socialnetwork.dto.FriendshipFilterDTO;

public interface FriendshipRepository extends PagingRepository<Long, Friendship> {
    Page<Friendship> findAll(Pageable pageable, FriendshipFilterDTO filter);
}
