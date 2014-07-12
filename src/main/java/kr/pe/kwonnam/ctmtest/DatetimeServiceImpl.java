package kr.pe.kwonnam.ctmtest;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DatetimeServiceImpl implements  DatetimeService {

    private DatetimeDao firstDatetimeDao;
    private DatetimeDao secondDatetimeDao;

    public DatetimeServiceImpl(DatetimeDao firstDatetimeDao, DatetimeDao secondDatetimeDao) {
        this.firstDatetimeDao = firstDatetimeDao;
        this.secondDatetimeDao = secondDatetimeDao;
    }

    @Override
    public void getDatetimeFromFirst() {
        System.out.println("First datetime : " + firstDatetimeDao.getDatetime());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getDatetimeFromSecond() {
        System.out.println("Second datetime : " + secondDatetimeDao.getDatetime());
    }
}
