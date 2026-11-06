package Service.DataManager.Repo;

public interface Repository<T> {
    void addEntry(T item);
    void updateEntry(T item);
    void deleteEntry(int id);
    T findById(int id);
}
