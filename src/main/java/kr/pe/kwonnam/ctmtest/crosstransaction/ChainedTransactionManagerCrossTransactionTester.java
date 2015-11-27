package kr.pe.kwonnam.ctmtest.crosstransaction;

import kr.pe.kwonnam.ctmtest.ApplicationContextConfig;
import org.slf4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * ChainedTransactionManager를 사용하는 상황에서, 상위에서 ChainedTransaction으로 트랜잭션을시작하고
 * 하위에서 Chained에 포함된 단일 트랜잭션으로 트랜잭션을 걸었을 때 상위의 Chained가 함께 트랜잭션이 걸릴것인가? 테스트
 * <p/>
 * 1. 상위에서 Chained 트랜잭션 시작
 * 2. 그 메소드 안에서 단일 트랜잭션 걸린 하위 메소드1 호출 - 여기서 insert
 * 2. 그 메소드 안에서 단일 트랜잭션 걸린 하위 메소드2 호출 - 여기서 insert
 * 4. insert 직후 exception 발생
 * 5. 하위 메소드의 insert와 상위 메소드의 insert가 둘다 rollback 됐는지 확인.
 */
public class ChainedTransactionManagerCrossTransactionTester {
    private static final Logger log = getLogger(ChainedTransactionManagerCrossTransactionTester.class);

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ApplicationContextConfig.class, CrossTransactionContextConfig.class);

        CrossTransactionService crossTransactionService = applicationContext.getBean(CrossTransactionService.class);

        BookForFirstDao bookForFirstDao = applicationContext.getBean(BookForFirstDao.class);
        PersonForSecondDao personForSecondDao = applicationContext.getBean(PersonForSecondDao.class);

        noTransactionTest(crossTransactionService, bookForFirstDao, personForSecondDao);
        crossWithSpecificTransactionManagerTest(crossTransactionService, bookForFirstDao, personForSecondDao);
        crossWithSubChainedTransactionManagerTest(crossTransactionService, bookForFirstDao, personForSecondDao);
    }

    private static void noTransactionTest(CrossTransactionService crossTransactionService, BookForFirstDao bookForFirstDao, PersonForSecondDao personForSecondDao) {
        try {
            crossTransactionService.noTransactionWithException();
        } catch (Exception ex) {
            log.info("예외 발생 - " + ex.getMessage());
            assert ex.getMessage().equals("Exception for noTransactionWithException.");
        }

        List<Map<String, Object>> books = bookForFirstDao.findAll();
        log.info("books after noTransactionWithException() : {}", books);

        List<Map<String, Object>> people = personForSecondDao.findAll();
        log.info("people after noTransactionWithException() : {}", books);
        assert books.size() == 1;
        assert people.size() == 1;
    }

    private static void crossWithSpecificTransactionManagerTest(CrossTransactionService crossTransactionService, BookForFirstDao bookForFirstDao, PersonForSecondDao personForSecondDao) {
        try {
            crossTransactionService.crossWithSpecificTransactionManagerException();
        } catch (Exception ex) {
            log.info("예외 발생 - " + ex.getMessage());
            assert ex.getMessage().equals("Exception for crossWithSpecificTransactionManagerException.");
        }

        List<Map<String, Object>> books = bookForFirstDao.findAll();
        log.info("books after crossTransactionWithException() : {}", books);

        List<Map<String, Object>> people = personForSecondDao.findAll();
        log.info("people after crossTransactionWithException() : {}", books);
        assert books.size() == 1;
        assert people.size() == 1;
    }

    private static void crossWithSubChainedTransactionManagerTest(CrossTransactionService crossTransactionService, BookForFirstDao bookForFirstDao, PersonForSecondDao personForSecondDao) {
        try {
            crossTransactionService.crossWithSubChainedTransactionManagerException();
        } catch (Exception ex) {
            log.info("예외 발생 - " + ex.getMessage());
            assert ex.getMessage().equals("Exception for crossWithSubChainedTransactionManagerException.");
        }

        List<Map<String, Object>> books = bookForFirstDao.findAll();
        log.info("books after crossTransactionWithException() : {}", books);

        List<Map<String, Object>> people = personForSecondDao.findAll();
        log.info("people after crossTransactionWithException() : {}", people);
        assert books.size() == 1;
        assert people.size() == 1;
    }
}
