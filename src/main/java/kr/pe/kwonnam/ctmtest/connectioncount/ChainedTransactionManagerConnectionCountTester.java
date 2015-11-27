package kr.pe.kwonnam.ctmtest.connectioncount;

import kr.pe.kwonnam.ctmtest.ApplicationContextConfig;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * ChainedTransactionManager 사용시 chain 상태의 모든 트랜잭션 커넥션이 확보되는지 여부 확인하기.
 * 다한뒤에 각 TransactionManager가 바라보는 dataSource를 Lazy버전으로 바꾼뒤에 다시 해본다.
 *
 */
public class ChainedTransactionManagerConnectionCountTester {

    public static void main(String[] args) throws InterruptedException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ApplicationContextConfig.class);

        final DatetimeService datetimeService = applicationContext.getBean(DatetimeService.class);

        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    datetimeService.getDatetimeFromFirst();
                }
            }).start();
        }

        Thread.sleep(500);
        BasicDataSource firstDataSource = applicationContext.getBean("firstDataSource", BasicDataSource.class);
        BasicDataSource secondDataSource = applicationContext.getBean("secondDataSource", BasicDataSource.class);

        System.out.println("firstDataSource connection count : " + firstDataSource.getNumActive());
        System.out.println("secondDataSource connection count : " + secondDataSource.getNumActive());

        datetimeService.getDatetimeFromSecond();

        System.out.println("firstDataSource connection count after second job : " + firstDataSource.getNumActive());
        System.out.println("secondDataSource connection count after second job : " + secondDataSource.getNumActive());
    }
}
