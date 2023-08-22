package com.spring.community.repository;

import com.spring.community.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostJPARepository extends JpaRepository<Post, Long> {


}