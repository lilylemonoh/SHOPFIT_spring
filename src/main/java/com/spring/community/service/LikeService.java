package com.spring.community.service;

import com.spring.community.DTO.LikeRequestDTO;

public interface LikeService {

    void saveLike(LikeRequestDTO likeRequestDTO);

    void deleteLike(LikeRequestDTO likeRequestDTO);
}
