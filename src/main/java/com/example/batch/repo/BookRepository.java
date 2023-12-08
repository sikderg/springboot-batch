package com.example.batch.repo;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.batch.model.Book;

public interface BookRepository extends JpaRepository<Book, Serializable> {

}
