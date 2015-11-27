package kr.pe.kwonnam.ctmtest.crosstransaction;

import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PersonForSecondDaoImpl extends JdbcDaoSupport implements PersonForSecondDao {
    public PersonForSecondDaoImpl(DataSource dataSource) {
        setDataSource(dataSource);
    }

    @Override
    public List<Map<String, Object>> findAll() {
        return getJdbcTemplate().queryForList("select id, name, birthdate from people");
    }


    @Transactional(value = "secondTransactionManager", readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public void insertWithFirstTransactionManager(int id, String name, Date birthdate) {
        getJdbcTemplate().update("insert into people (id, name, birthdate) values(?,?,?)", id, name, birthdate);
    }

    @Transactional(value = "doubleChainedTransactionManager", readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public void insertWithDoubleChainedTransactionManager(int id, String name, Date birthdate) {
        getJdbcTemplate().update("insert into people (id, name, birthdate) values(?,?,?)", id, name, birthdate);
    }
}
