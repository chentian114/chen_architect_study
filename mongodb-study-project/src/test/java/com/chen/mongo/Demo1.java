package com.chen.mongo;

import cn.hutool.json.JSONUtil;
import com.chen.MongoDBServiceApplication;
import com.chen.entity.Book;
import com.chen.mongodb.IMongodbService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author ChenTian
 * @date 2020/6/28
 */
@SpringBootTest(classes = MongoDBServiceApplication.class)
@RunWith(SpringRunner.class)
public class Demo1 {

    @Autowired
    private IMongodbService mongodbService;

    @Test
    public void save1(){
        Book book = new Book();
        book.setId("1");
        book.setName("book01");
        book.setInfo("info");
        String result =mongodbService.save(book);
        System.out.println(result);
    }

    @Test
    public void findAll(){
        List<Book> all = mongodbService.findAll();
        System.out.println(JSONUtil.toJsonPrettyStr(all));
    }

    @Test
    public void getById(){
        Book bookById = mongodbService.getBookById("1");
        System.out.println(JSONUtil.toJsonPrettyStr(bookById));
    }

    @Test
    public void getBookByName(){
        Book book01 = mongodbService.getBookByName("book01");
        System.out.println(JSONUtil.toJsonPrettyStr(book01));
    }

    @Test
    public void updateBook(){
        Book book = mongodbService.getBookById("1");
        book.setPublish("publish");
        book.setInfo("newInfo");
        String s = mongodbService.updateBook(book);
        System.out.println(s);
    }

    @Test
    public void deleteBook(){
        Book book = mongodbService.getBookByName("book01");
        mongodbService.deleteBook(book);
    }

    @Test
    public void deleteById(){
        mongodbService.deleteBookById("1");
    }


}
