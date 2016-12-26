package test.coreer.config;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;

import org.springframework.transaction.jta.JtaTransactionManager;

public class AtomikosJtaPlatform extends AbstractJtaPlatform {
    private static final long serialVersionUID = 1L;

    public static JtaTransactionManager jtaTransactionManager;

    @Override
    protected TransactionManager locateTransactionManager() {
        return jtaTransactionManager.getTransactionManager();
    }

    @Override
    protected UserTransaction locateUserTransaction() {
        return jtaTransactionManager.getUserTransaction();
    }
}
