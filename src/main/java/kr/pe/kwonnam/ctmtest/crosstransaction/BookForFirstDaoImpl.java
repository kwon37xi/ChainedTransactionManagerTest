package kr.pe.kwonnam.ctmtest.crosstransaction;

import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class BookForFirstDaoImpl extends JdbcDaoSupport implements BookForFirstDao {

    public BookForFirstDaoImpl(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public List<Map<String, Object>> findAll() {
        return getJdbcTemplate().queryForList("select id, title, author from books");
    }

    @Transactional(value = "firstTransactionManager", readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public void insertWithFirstTransactionManager(int id, String title, String author) {
        getJdbcTemplate().update("insert into books (id, title, author) values(?,?,?)", id, title, author);
    }

    @Transactional(value = "doubleChainedTransactionManager", readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public void insertWithDoubleChainedTransactionManager(int id, String title, String author) {
        getJdbcTemplate().update("insert into books (id, title, author) values(?,?,?)", id, title, author);

    }
}
