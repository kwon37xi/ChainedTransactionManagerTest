package kr.pe.kwonnam.ctmtest;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ChainedTransactionManagerTester {

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
