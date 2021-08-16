import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

class Loan {

    LocalDate BeginDate;
    LocalDate EndDate;
    double Rate;
    double Amount;
    int PayDayOfMonth;

    int TotalPayments;
    LocalDate TranDate[];
    double DailyBalance[];

    Loan(LocalDate b, LocalDate e, double r, double p, int pd) {

        BeginDate = b;
        EndDate = e;
        Rate = r;
        Amount = p;
        PayDayOfMonth = pd;

        int days = (int) ChronoUnit.DAYS.between(b, e) + 1;
        TotalPayments = (int) ChronoUnit.MONTHS.between(b, e);

        TranDate = new LocalDate[days];
        DailyBalance = new double[days];

        double DailyInterest = 1 + (Rate / 100) / 365;

        double Balance = Amount;

        double MonthlyPaymentMin = Amount / TotalPayments;
        double MonthlyPaymentMax = Amount * Math.pow(DailyInterest, days) / TotalPayments;
        double MonthlyPayment = (MonthlyPaymentMin + MonthlyPaymentMax) / 2;

        while (Balance > 0.05 || Balance < -0.05) {

            System.out.println("Balance = " + Balance);

            LocalDate d = BeginDate;
            Balance = Amount;

            System.out.println("MonthlyPayment = " + MonthlyPayment);

            for (int i = 0; i < days; i++) {

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

    }

    int getDays() {

        return TranDate.length - 1;

    }

    int getPayments() {

        return TotalPayments;
    }

    void getDetail() {
        for (int i = 0; i < 2 * 31; i++)
            System.out.println(TranDate[i] + ", " + DailyBalance[i]);

    }

}

public class App {

    public static void main(String[] args) throws Exception {

        double Balance20210731 = 22028.65;
        double Rate = 2.0;
        int PayDayOfMonth = 3; // after credit card payment

        double Balance20210831 = Balance20210731 * Math.pow((1 + (Rate / 100) / 365),31);

        LocalDate BeginDate = LocalDate.of(2021, 8, 31);
        LocalDate EndDate = LocalDate.of(2026, 4, PayDayOfMonth);

        Loan l = new Loan(BeginDate, EndDate, Rate, Balance20210831, PayDayOfMonth);
        System.out.println("dayl: " + l.getDays());

        System.out.println("dayl: " + l.getPayments());

        l.getDetail();

    }
}
