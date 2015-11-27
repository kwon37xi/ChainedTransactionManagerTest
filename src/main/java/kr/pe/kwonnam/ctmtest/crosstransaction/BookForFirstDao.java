package kr.pe.kwonnam.ctmtest.crosstransaction;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface BookForFirstDao {
    List<Map<String, Object>> findAll();

    void insertWithFirstTransactionManager(int id, String title, String author);

    void insertWithDoubleChainedTransactionManager(int id, String title, String author);
}
