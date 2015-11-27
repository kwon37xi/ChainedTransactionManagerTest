package kr.pe.kwonnam.ctmtest.crosstransaction;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 */
public interface PersonForSecondDao {
    List<Map<String, Object>> findAll();

    void insertWithFirstTransactionManager(int id, String name, Date birthday);

    void insertWithDoubleChainedTransactionManager(int id, String name, Date birthday);
}
