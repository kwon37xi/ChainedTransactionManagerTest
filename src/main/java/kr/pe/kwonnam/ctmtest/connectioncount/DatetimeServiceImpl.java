package kr.pe.kwonnam.ctmtest.connectioncount;

import org.springframework.transaction.annotation.Transactional;

@Transactional(value = "doubleChainedTransactionManager")
public class DatetimeServiceImpl implements  DatetimeService {

    private DatetimeDao firstDatetimeDao;
    private DatetimeDao secondDatetimeDao;

    public DatetimeServiceImpl(DatetimeDao firstDatetimeDao, DatetimeDao secondDatetimeDao) {
        this.firstDatetimeDao = firstDatetimeDao;
        this.secondDatetimeDao = secondDatetimeDao;
    }

    @Override
    @Transactional(value = "doubleChainedTransactionManager")
    public void getDatetimeFromFirst() {
        System.out.println("First datetime : " + firstDatetimeDao.getDatetime());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional(value = "doubleChainedTransactionManager")
    public void getDatetimeFromSecond() {
        System.out.println("Second datetime : " + secondDatetimeDao.getDatetime());
    }
}
