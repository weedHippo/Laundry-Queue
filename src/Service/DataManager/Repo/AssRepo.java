package Service.DataManager.Repo;

import java.util.List;

public interface AssRepo<T> {
    void addEntry(T item);
    void updateEntry(T item);
    void updateEntryState(T item);
    void deleteEntry(int id);
    List<T> findAll();
}