package com.chen.zookeeper.register;

/**
 * @author ChenTian
 * @date 2020/7/10
 */
public class UserServiceImpl implements UserService{
    @Override
    public String get(Integer id) {
        return "user"+id;
    }
}
