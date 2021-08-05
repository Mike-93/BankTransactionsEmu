public class Main {

    private static final int THREAD_COUNT = 100;

    public static void main(String[] args) {

        Bank bank = new Bank();
        bank.fillAccounts();
        System.out.println("Сумма всех банковских счетов до совершения транзакций " + bank.getTotalBalance() + " $");

        for (int i = 0; i < THREAD_COUNT; i++) {
            new Thread(() -> {
                for (int j = 1; j < 100; j++) {
                    int amount = (int) (10000 + 45000 * Math.random());
                    try {
                        bank.transfer(j, j + 1, amount);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Сумма всех банковских счетов после совершения транзакций " + bank.getTotalBalance() + " $");
            }).start();
        }
    }
}