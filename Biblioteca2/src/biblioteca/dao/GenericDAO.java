package biblioteca.dao;

import java.util.List;

public interface GenericDAO<T> { // Adicionado <T> para declarar o tipo genérico
    T findById(int id);

    List<T> findAll();

    void save(T entity);

    void update(T entity);

    void delete(int id);
}
