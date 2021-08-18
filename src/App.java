import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

class Loan {

    LocalDate BeginDate;
    LocalDate EndDate;
    double Rate;
    double Amount;
    int PayDayOfMonth;

    int NumberPayments;
    double MonthlyPayment;
    LocalDate TranDate[];
    double DailyBalance[];

    Loan(LocalDate b, LocalDate e, double r, double p, int pd) {

        BeginDate = b;
        EndDate = e;
        Rate = r;
        Amount = p;
        PayDayOfMonth = pd;

        int days = (int) ChronoUnit.DAYS.between(b, e) + 1;
        NumberPayments = 1 + (int) ChronoUnit.MONTHS.between(b, e);

        TranDate = new LocalDate[days];
        DailyBalance = new double[days];

        double DailyInterest = 1 + (Rate / 100) / 365;

        double Balance = Amount;

        double MonthlyPaymentMin = Amount / NumberPayments;
        double MonthlyPaymentMax = Amount * Math.pow(DailyInterest, days) / NumberPayments;
        MonthlyPayment = (MonthlyPaymentMin + MonthlyPaymentMax) / 2;

        while (Balance > 0.01 || Balance < -0.01) {

            System.out.println("MonthlyPayment = " + MonthlyPayment + ", Balance = " + Balance);
            //System.out.println("MonthlyPayment = " + Math.round(MonthlyPayment * 100)/100d + ", Balance = " + Balance);

            LocalDate d = BeginDate;
            Balance = Amount;

            TranDate[0] = d;
            DailyBalance[0] = Balance;

            for (int i = 1; i < days; i++) {
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

    int getNumberPayments() {

        return NumberPayments;
    }

    double getMonthlyPayment() {
        return Math.round(MonthlyPayment * 100)/100d;
    }

    void getDetail() {
        for (int i = 0; i < TranDate.length; i++)
            System.out.println(TranDate[i] + ", " + DailyBalance[i]);

    }

}

public class App {

    public static void main(String[] args) throws Exception {

        double Balance20210731 = 22028.65;
        //double Balance20210731 = 22028.645736;

        double Rate = 2.0;
        int PayDayOfMonth = 3; // after credit card payment

        double Balance20210831 = Balance20210731 * Math.pow((1 + (Rate / 100) / 365), 31);

        LocalDate BeginDate = LocalDate.of(2021, 8, 31);
        LocalDate EndDate = LocalDate.of(2026, 4, PayDayOfMonth);

        Loan l = new Loan(BeginDate, EndDate, Rate, Balance20210831, PayDayOfMonth);
        System.out.println("Remaining Days: " + l.getDays());
        System.out.println("Remaining Months: " + l.getNumberPayments());
        System.out.println("Monthly Payment: " + l.getMonthlyPayment());

        //l.getDetail();

    }
}
