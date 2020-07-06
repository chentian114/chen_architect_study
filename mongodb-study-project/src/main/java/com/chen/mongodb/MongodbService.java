package com.chen.mongodb;

import com.chen.entity.Book;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author ChenTian
 * @date 2020/6/28
 */
@Service
public class MongodbService implements IMongodbService {

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public String save(Book book){
        mongoTemplate.save(book);
        return "success";
    }

    @Override
    public List<Book> findAll() {
        return mongoTemplate.findAll(Book.class);
    }

    @Override
    public Book getBookById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.findOne(query, Book.class);
    }

    @Override
    public Book getBookByName(String name) {
        Query query = new Query(Criteria.where("name").is(name));
        return mongoTemplate.findOne(query, Book.class);
    }

    @Override
    public String updateBook(Book book) {
        Query query = new Query(Criteria.where("_id").is(book.getId()));
        Update update = new Update().set("publish", book.getPublish())
                .set("info", book.getInfo())
                .set("updateTime", new Date());
        //updateFirst 更新查询返回结果集的第一条
        mongoTemplate.updateFirst(query, update, Book.class);
        return "success";
    }

    /***
     * 删除对象
     */
    @Override
    public String deleteBook(Book book) {
        mongoTemplate.remove(book);
        return "success";
    }

    /**
     * 根据id删除
     */
    @Override
    public String deleteBookById(String id) {
        Book book = getBookById(id);
        deleteBook(book);
        return "success";
    }
}
