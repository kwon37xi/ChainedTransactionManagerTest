package kr.pe.kwonnam.ctmtest.crosstransaction;

/**
 *
 */
public interface CrossTransactionService {
    void noTransactionWithException();

    void crossWithSpecificTransactionManagerException();

    void crossWithSubChainedTransactionManagerException();
}
