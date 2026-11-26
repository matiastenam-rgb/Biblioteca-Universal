package data;

import java.util.List;

public interface BaseDao<T> {
    List<T> select();
    T selectById(int id);
    void insert(T entity);
    void update(T entity);
    void delete(int id);
}
