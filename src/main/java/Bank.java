import java.util.HashMap;
import java.util.Random;

public class Bank {
    private HashMap<Integer, Account> accounts;
    private final Random random = new Random();

    public synchronized boolean isFraud(int fromAccountNum, int toAccountNum, long amount)
            throws InterruptedException {
        Thread.sleep(1000);
        return random.nextBoolean();
    }

    public long getTotalBalance() {
        return accounts.values().stream().mapToLong(Account::getBalance).sum();
    }

    /**
     * Метод перевода денег между счетами.
     */
    public void transfer(int fromAccountNum, int toAccountNum, long amount)
            throws InterruptedException {
        Account fromAccount = accounts.get(fromAccountNum);
        Account toAccount = accounts.get(toAccountNum);

        if (fromAccount.isBlocked() || toAccount.isBlocked()) {
            return;
        }

        transaction(amount, fromAccount, toAccount);

        if (amount > 50000) {
            if (isFraud(fromAccountNum, toAccountNum, amount)) {
                transaction(amount, toAccount, fromAccount);
                fromAccount.blockAccount();
                toAccount.blockAccount();
            }
        }
    }

    private void transaction(long amount, Account fromAccount, Account toAccount) {
        //int fromAccountId = fromAccount.getAccNumber();
        //int toAccountId = toAccount.getAccNumber();
        synchronized (fromAccount) {
            synchronized (toAccount) {
                if (fromAccount.withdrawMoney(amount)) {
                    toAccount.putMoney(amount);
                }
            }
        }

    }

    /**
     * Метод возвращающий остаток средств на счёте.
     */
    public long getBalance(int accountNum) {
        Account account = accounts.get(accountNum);
        return account.getBalance();
    }

    /**
     * Метод заполняющий аккаунты.
     */
    public HashMap<Integer, Account> fillAccounts() {
        accounts = new HashMap<>();
        for (int i = 1; i <= 100; i++) {
            long initialValue = (long) (80000 + 20000 * Math.random());
            Account account = new Account(i, initialValue);
            accounts.put(i, account);
        }
        return accounts;
    }

    public void setAccounts(HashMap<Integer, Account> accounts) {
        this.accounts = accounts;
    }
}