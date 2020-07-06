package com.chen.mongodb;

import com.chen.entity.Book;

import java.util.List;

public interface IMongodbService {
    String save(Book book);

    List<Book> findAll();

    Book getBookById(String id);

    Book getBookByName(String name);

    String updateBook(Book book);

    String deleteBook(Book book);

    String deleteBookById(String id);
}
