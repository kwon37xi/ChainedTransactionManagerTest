package kr.pe.kwonnam.ctmtest.connectioncount;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.sql.DataSource;
import java.util.Date;

public class DatetimeDao extends JdbcDaoSupport {

    public DatetimeDao(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public Date getDatetime() {
        return getJdbcTemplate().queryForObject("select current_timestamp();", Date.class);
    }
}
