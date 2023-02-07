# Spring ChainedTransactionManager Test

**ChainedTransactionManager 는 Spring Data 2.5 부터 Deprecated 되었다. 사용하지 말 것.**

Spring data commons 에 추가된 [ChainedTransactionManager](http://docs.spring.io/spring-data/commons/docs/1.6.2.RELEASE/api/org/springframework/data/transaction/ChainedTransactionManager.html)에 관한 테스트를 해본다.

관련 블로그 글 : [Spring ChainedTransactionManager 어떻게 사용해야 하나?](http://kwon37xi.egloos.com/4886947)

## ChainedTransactionManagerTester
### 테스트 조건
* firstDataSource는 최대 커넥션 갯수를 __5개__로 제한한다.
* secondDataSource는 최대 커넥션 갯수를 __10개__로 제한한다.
* 이 둘에 관한 TransactionManager를 ChainedTransactionManager로 묶어서 함께 트랜잭션을 관리한다.
* ChainedTransactionManagerTester 에서 firstDataSource에 대한 요청을 날리고, 응답을 10초간 지연시키는 쓰레드를 5개(firstDataSource의 최대 커넥션 갯수만큼) 만든다.
* 쓰레드를 실행한 직후의 커넥션 갯수를 찍고 곧바로 secondDataSource 를 호출하는 쿼리를 날린다.
* 추후 ApplicationcContextConfig.java에서 [LazyConnectionDataSourceProxy](http://docs.spring.io/spring/docs/3.2.9.RELEASE/javadoc-api/org/springframework/jdbc/datasource/LazyConnectionDataSourceProxy.html) 사용하도록 각 DataSource를 감싸서 다시 테스트를 진행한다.
* [ChainedTransactionManagerTester](https://github.com/kwon37xi/ChainedTransactionManagerTest/blob/master/src/main/java/kr/pe/kwonnam/ctmtest/connectioncount/ChainedTransactionManagerConnectionCountTester.java)를 실행한다.

### 결과 LazyConnectionDataSourceProxy 사용 안 할 때
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

### 결과 LazyConnectionDataSourceProxy 사용 할 때

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

### 결론
* [ChainedTransactionManager](http://docs.spring.io/spring-data/commons/docs/1.6.2.RELEASE/api/org/springframework/data/transaction/ChainedTransactionManager.html)를
사용할 때는 __함께 묶인 데이터 소스의 최대 커넥션 갯수를 동일__하게 맞춰야 한다.
* 가능하면 [LazyConnectionDataSourceProxy](http://docs.spring.io/spring/docs/3.2.9.RELEASE/javadoc-api/org/springframework/jdbc/datasource/LazyConnectionDataSourceProxy.html) 를 사용하여 실제로는 사용되지도 않으면서 트랜잭션 시작시 확보되는 커넥션 갯수를 줄여주는 것이 좋다.
* javadoc에 따라, 가장 에러 확률이 높은 Transaction Manager를 가장 마지막에 지정하여 커밋/롤백을 제일 먼저 일어나게 해줘야 좋다. 그래야 해당 TM에 롤백될 때 다른 TM들도 함께 롤백된다.

## ChainedTransactionManagerCrossTransactionTester

`ChainedTransactionManager`로 묶은 트랜잭션을 시작한 뒤에 그 안에서 상위 트랜잭션 매니저에 포함되지만 범위가 더 작은 `ChainedTransactionManager` 
혹은 단일 트랜잭션 매니저를 `propagation=REQUIRES`로 걸어준 상태일 때 상위의 `ChainedTransactionManager`의 트랜잭션과 동기화가 된 상태인지
혹은 완전히 별개의 트랜잭션으로 간주하는지 테스트 한다.

### 테스트 설정
* `firstTransactionManager`, `secondTransactionManager`, `thirdTransactionManager` 는 각각의 독립된 `DataSource`를 바라보게 설정한다.
* `doubleChainedTransactionManager`는 `first/secondTransactionManager`를 chain 한다.
* `tripleChainedTransactionManager`는 `first/second/thirdTransactionManager`를 chain 한다.
* [ChainedTransactionManagerCrossTransactionTester](https://github.com/kwon37xi/ChainedTransactionManagerTest/blob/master/src/main/java/kr/pe/kwonnam/ctmtest/crosstransaction/ChainedTransactionManagerCrossTransactionTester.java)를 실행한다.

### 테스트 실행 결과
* `propagation=NEVER`(트랜잭션없음)로 트랜잭션이 걸린 상위 메소드에서 `first/secondTransactionManager`가 걸린 insert 수행 dao 메소드를 호출하고, 그 뒤에 예외를 발생시킨다.
  * insert 된 데이터가 롤백되지 않는다. -> 당연하거지.
* `doubleChainedTransactionManager`로 트랜잭션이 걸린 상위 메소드에서 `first/secondTransactionManager`가 걸린 insert 수행 dao 메소드를 호출하고, 그 뒤에 예외를 발생시킨다.
  * insert 된 데이터가 롤백된다. chained와 독립 트랜잭션 매니저는 동기화된 상태이다.
* `tripleChainedTransactionManager`로 트랜잭션이 걸린 상위 메소드에서 `doubleChainedTransactionManager`가 걸린 insert 수행 dao 메소드를 호출하고, 그 뒤에 예외를 발생시킨다.
  * insert 된 데이터가 롤백된다. chained와 그보다 작은 범위로 chained인 트랜잭션 매니저는 동기화된 상태이다.

### 결론
* **`ChainedTransactionManager`로 트랜잭션이 시작되면 그 내부에서 Chained 된 범위내의 다른 트랜잭션 매니저가 시작되어도 상위의 트랜잭션 매니저로 트랜잭션이 동기화**되어 있다.
