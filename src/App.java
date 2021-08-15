import java.time.LocalDate;

public class App {

    public static void main(String[] args) throws Exception {

        int TotalPayments = 60; // post testing 60
        int PayDayOfMonth = 3; // after credit card payment

        double DailyInterest = 1 + .02 / 365;

        LocalDate OriginationDate = LocalDate.of(2021, 3, 29);
        LocalDate FirstPaymentDate = LocalDate.of(2021, 4, PayDayOfMonth); // start right away
        LocalDate FinalPaymentDate = FirstPaymentDate.plusMonths(TotalPayments); //
        LocalDate LastBalanceDated = LocalDate.of(2021, 7, 31);

        double LastBalance = 22028.65;
        double Balance = LastBalance;

        double MonthlyPaymentMin = LastBalance / TotalPayments;
        double MonthlyPaymentMax = MonthlyPaymentMin + 200;
        double MonthlyPayment = (MonthlyPaymentMin + MonthlyPaymentMax) / 2;

        LocalDate TranDate[] = new LocalDate[TotalPayments * 31];
        double DailyBalance[] = new double[TotalPayments * 31];

        while (Balance > 0.05 || Balance < -0.05) {
    //        for(int j = 0; j<10; j++){

            System.out.println("Balance = " + Balance);

            LocalDate d = LastBalanceDated;
            Balance = LastBalance;


            System.out.println("MonthlyPayment = " + MonthlyPayment);

            int i = 0;
            TranDate[i] = d;
            DailyBalance[i] = Balance;

            while (d.isBefore(FinalPaymentDate) || d.isEqual(FinalPaymentDate)) {

                ++i;
                d = d.plusDays(1);
                TranDate[i] = d;
                Balance *= DailyInterest;
                if (d.getDayOfMonth() == PayDayOfMonth)
                    Balance -= MonthlyPayment;
                DailyBalance[i] = Balance;

            }

            if (Balance > 0)
                MonthlyPaymentMin = MonthlyPayment;
            else
                MonthlyPaymentMax = MonthlyPayment;


            MonthlyPayment = (MonthlyPaymentMin + MonthlyPaymentMax) / 2;

        }

      //  for (int i = 0; i < TotalPayments * 31; i++)
         //   System.out.println(TranDate[i] + ", " + DailyBalance[i]);

    }
}
