package com.example.socialnetwork.repository;

import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.dto.UserFilterDTO;

public interface UserRepository extends PagingRepository<Long, User>{
    Page<User> findAll(Pageable pageable, UserFilterDTO filter);
}
