# Spring ChainedTransactionManager Test

Spring data commons 에 추가된 [ChainedTransactionManager](http://docs.spring.io/spring-data/commons/docs/1.6.2.RELEASE/api/org/springframework/data/transaction/ChainedTransactionManager.html)에 관한 테스트를 해본다.

## 테스트 조건
* firstDataSource는 최대 커넥션 갯수를 5개로 제한한다.
* secondDataSource는 최대 커넥션 갯수를 10개로 제한한다.
* 이 둘에 관한 TransactionManager를 ChainedTransactionManager로 묶어서 함께 트랜잭션을 관리한다.
* ChainedTransactionManagerTester 에서 firstDataSource에 대한 요청을 날리고, 응답을 10초간 지연시키는 쓰레드를 5개(firstDataSource의 최대 커넥션 갯수만큼) 만든다.
* 쓰레드를 실행한 직후의 커넥션 갯수를 찍고 곧바로 secondDataSource 를 호출하는 쿼리를 날린다.
* 추후 ApplicationcContextConfig.java에서 [LazyConnectionDataSourceProxy](http://docs.spring.io/spring/docs/3.2.9.RELEASE/javadoc-api/org/springframework/jdbc/datasource/LazyConnectionDataSourceProxy.html) 사용하도록 각 DataSource를 감싸서 다시 테스트를 진행한다.

## 결과 첫번째
    First datetime : 2020-12-32 12:30:12.691
    First datetime : 2020-12-32 12:30:12.687
    First datetime : 2020-12-32 12:30:12.69
    First datetime : 2020-12-32 12:30:12.69
    First datetime : 2020-12-32 12:30:12.691
    firstDataSource connection count : 5
    secondDataSource connection count : 5 ==> 실제로 위에서는 사용도 안한 커넥션이지만 트랜잭션 매니저에 의해 커넥션을 확보함
    Second datetime : 2020-12-32 12:30:22.706 ==> First datetime과 10초간의 시간차
    firstDataSource connection count after second job : 2 ==> firstDataSource를 사용하는 쓰레드가 완료되어 몇개 반환됨
    secondDataSource connection count after second job : 0

## 결과 LazyConnectionDataSourceProxy

    First datetime : 2020-12-32 12:35:30.165
    First datetime : 2020-12-32 12:35:30.167
    First datetime : 2020-12-32 12:35:30.167
    First datetime : 2020-12-32 12:35:30.167
    First datetime : 2020-12-32 12:35:30.167
    firstDataSource connection count : 5
    secondDataSource connection count : 0 ==> 사용하지 않는 커넥션은 LazyConnectionDataSourceProxy에 의해 무시됨
    Second datetime : 2020-12-32 12:35:30.636 ==> First datetime과 시간차가 거의 없음
    firstDataSource connection count after second job : 5 ==> firstDataSource를 사용하는 쓰레드는 여전히 작동중이므로 커넥션이 남아있음
    secondDataSource connection count after second job : 0

## 결론
[ChainedTransactionManager](http://docs.spring.io/spring-data/commons/docs/1.6.2.RELEASE/api/org/springframework/data/transaction/ChainedTransactionManager.html)를
사용할 때는 __함께 묶인 데이터 소스의 최대 커넥션 갯수를 동일__하게 맞춰야 한다.
또한 가능하면 [LazyConnectionDataSourceProxy](http://docs.spring.io/spring/docs/3.2.9.RELEASE/javadoc-api/org/springframework/jdbc/datasource/LazyConnectionDataSourceProxy.html) 를 사용하여 실제로는 사용되지도 않으면서 트랜잭션 시작시 확보되는 커넥션 갯수를 줄여주는 것이 좋다.
