package kr.pe.kwonnam.ctmtest.crosstransaction;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Chained와 Chained가 포함하는 요소들이 서로 트랜잭션이 얽혀 있을 경우의 반응은?
 */
public class CrossTransactionServiceImpl implements CrossTransactionService {
    private BookForFirstDao bookForFirstDao;

    private PersonForSecondDao personForSecondDao;

    public CrossTransactionServiceImpl(BookForFirstDao bookForFirstDao, PersonForSecondDao personForSecondDao) {
        this.bookForFirstDao = bookForFirstDao;
        this.personForSecondDao = personForSecondDao;
    }

    /**
     * 밖에서 트랜잭션을 안 걸었으므로 내부의 first, second transaction manager는 올바로 commit이 돼야 한다.
     */
    @Transactional(propagation = Propagation.NEVER)
    @Override
    public void noTransactionWithException() {
        bookForFirstDao.insertWithFirstTransactionManager(1, "FirstBook", "Spring");
        personForSecondDao.insertWithFirstTransactionManager(1, "John", new Date());

        throw new IllegalStateException("Exception for noTransactionWithException.");
    }

    /**
     * 밖에서 chained로 트랜잭션을 거고, 내부에 first, second 트랜잭션을 걸면 어찌되나?
     */
    @Transactional(value = "doubleChainedTransactionManager", propagation = Propagation.REQUIRED)
    @Override
    public void crossWithSpecificTransactionManagerException() {
        bookForFirstDao.insertWithFirstTransactionManager(2, "FirstBook2", "Spring2");
        personForSecondDao.insertWithFirstTransactionManager(2, "John2", new Date());

        throw new IllegalStateException("Exception for crossWithSpecificTransactionManagerException.");
    }

    @Override
    @Transactional(value = "tripleChainedTransactionManager", propagation = Propagation.REQUIRED)
    public void crossWithSubChainedTransactionManagerException() {
        bookForFirstDao.insertWithDoubleChainedTransactionManager(3, "FirstBook3", "Spring3");
        personForSecondDao.insertWithDoubleChainedTransactionManager(3, "John3", new Date());

        throw new IllegalStateException("Exception for crossWithSubChainedTransactionManagerException.");
    }
}
