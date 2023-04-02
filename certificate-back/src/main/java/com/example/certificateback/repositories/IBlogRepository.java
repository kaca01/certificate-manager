package com.example.certificateback.repositories;

import com.example.certificateback.domains.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBlogRepository extends JpaRepository<Blog, Long> {

}
