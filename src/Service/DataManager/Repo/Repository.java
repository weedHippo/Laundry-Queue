package Service.DataManager.Repo;

import java.util.List;

public interface Repository<T> {
    void addEntry(T item);
    void updateEntry(T item);
    void deleteEntry(int id);
    T findById(int id);
    List<T> findAll();
}