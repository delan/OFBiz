package org.ofbiz.core.entity.transaction;

import javax.transaction.*;

/**
 * A dumb, non-working transaction manager.
 * 
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 */
public class DumbFactory implements TransactionFactoryInterface {
    public TransactionManager getTransactionManager() {
        return new TransactionManager() {
            public void begin() throws NotSupportedException, SystemException {
            }

            public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
            }

            public int getStatus() throws SystemException {
                return 0;
            }

            public Transaction getTransaction() throws SystemException {
                return null;
            }

            public void resume(Transaction transaction) throws InvalidTransactionException, IllegalStateException, SystemException {
            }

            public void rollback() throws IllegalStateException, SecurityException, SystemException {
            }

            public void setRollbackOnly() throws IllegalStateException, SystemException {
            }

            public void setTransactionTimeout(int i) throws SystemException {
            }

            public Transaction suspend() throws SystemException {
                return null;
            }
        };
    }

    public UserTransaction getUserTransaction() {
        return new UserTransaction() {
            public void begin() throws NotSupportedException, SystemException {
            }

            public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
            }

            public int getStatus() throws SystemException {
                return 0;
            }

            public void rollback() throws IllegalStateException, SecurityException, SystemException {
            }

            public void setRollbackOnly() throws IllegalStateException, SystemException {
            }

            public void setTransactionTimeout(int i) throws SystemException {
            }
        };
    }
}
