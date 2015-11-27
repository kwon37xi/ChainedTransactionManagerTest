package kr.pe.kwonnam.ctmtest.crosstransaction;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import static org.slf4j.LoggerFactory.getLogger;

/**
 *
 */
@Configuration
public class CrossTransactionContextConfig {
    private final Logger log = getLogger(CrossTransactionContextConfig.class);

    @Autowired
    private DataSource firstDataSource;

    @Autowired
    private DataSource secondDataSource;

    @Bean
    public BookForFirstDao bookForFirstDao() {
        log.info("bookForFirstDao created");
        return new BookForFirstDaoImpl(firstDataSource);
    }

    @Bean
    public PersonForSecondDao personForSecondDao() {
        log.info("personForSecondDao created");
        return new PersonForSecondDaoImpl(secondDataSource);
    }

    @Bean
    public CrossTransactionService crossTransactionService(BookForFirstDao bookForFirstDao, PersonForSecondDao personForSecondDao) {

        return new CrossTransactionServiceImpl(bookForFirstDao, personForSecondDao);
    }
}
