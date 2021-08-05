import junit.framework.TestCase;

import java.util.HashMap;

public class BankTest extends TestCase {

    private Bank bank;
    private HashMap<Integer, Account> accounts = new HashMap<>();
    private Account a1;
    private Account a2;
    private Account a3;
    private Account a4;
    private Account a5;

    @Override
    public void setUp() {

        bank = new Bank();
        a1 = new Account(1, 100000);
        a2 = new Account(2, 100000);
        a3 = new Account(3, 50000);
        a4 = new Account(4, 400000);
        a5 = new Account(5, 60000);

        accounts.put(1, a1);
        accounts.put(2, a2);
        accounts.put(3, a3);
        accounts.put(4, a4);
        accounts.put(5, a5);

        bank.setAccounts(accounts);

    }

    public void testTransferOneThread() throws InterruptedException {
        bank.transfer(1, 2, 1000);
        long actualFrom = a1.getBalance();
        long expectedFrom = 99000;
        long actualTo = a2.getBalance();
        long expectedTo = 101000;
        assertEquals(expectedFrom, actualFrom);
        assertEquals(expectedTo, actualTo);
    }

    public void testTransferManyThread() {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    bank.transfer(1, 2, 1000);
                    bank.transfer(1, 3, 1000);
                    bank.transfer(3, 1, 1000);
                    bank.transfer(3, 2, 1000);
                    bank.transfer(2, 1, 1000);
                    bank.transfer(2, 3, 1000);
                    bank.transfer(1, 3, 1000);
                    bank.transfer(2, 3, 1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        long actualA1 = a1.getBalance();
        long expectedA1 = 90000;
        long actualA2 = a2.getBalance();
        long expectedA2 = 90000;
        long actualA3 = a3.getBalance();
        long expectedA3 = 70000;

        assertEquals(expectedA1, actualA1);
        assertEquals(expectedA2, actualA2);
        assertEquals(expectedA3, actualA3);
    }


    public void testTransferBlock() throws InterruptedException {
        long balance = a1.getBalance();
        a1.setIsBlocked(true);
        bank.transfer(1, 2, 1000);
        bank.transfer(1, 2, 1000);
        bank.transfer(1, 2, 1000);
        long actualA1 = bank.getBalance(1);

        assertEquals(balance, actualA1);
    }

    public void testIfFraud() throws InterruptedException {
        for (int i = 0; i < 4; i++) {
            Thread t = new Thread(() -> {
                try {
                    bank.transfer(4, 5, 51000);
                    bank.transfer(5, 4, 51000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            t.start();
            t.join();
        }
        boolean actualFrom = a4.isBlocked();
        boolean actualTo = a5.isBlocked();

        assertTrue(actualFrom);
        assertTrue(actualTo);
    }

    public void testTransferOverLimit() throws InterruptedException {
        bank.transfer(1, 2, 150000);
        long actualFrom = a1.getBalance();
        long expectedFrom = 100000;
        long actualTo = a2.getBalance();
        long expectedTo = 100000;
        assertEquals(expectedFrom, actualFrom);
        assertEquals(expectedTo, actualTo);
    }

}


