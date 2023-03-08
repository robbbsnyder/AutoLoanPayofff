import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.io.*;
import java.sql.*;

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
    double Payment[];

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
        Payment = new double[days];

        double DailyInterest = 1 + (Rate / 100) / 365;

        double Balance = Amount;

        double MonthlyPaymentMin = Amount / NumberPayments;
        double MonthlyPaymentMax = Amount * Math.pow(DailyInterest, days) / NumberPayments;
        MonthlyPayment = (MonthlyPaymentMin + MonthlyPaymentMax) / 2;

        System.out.println("Opening Balance = " + Math.round(Balance * 100) / 100d);

        do {

            LocalDate d = BeginDate;
            Balance = Amount;

            TranDate[0] = d;
            DailyBalance[0] = Balance;

            for (int i = 1; i < days; i++) {
                d = d.plusDays(1);
                TranDate[i] = d;
                Balance *= DailyInterest;
                if (d.getDayOfMonth() == PayDayOfMonth) {
                    Balance -= MonthlyPayment;
                    Payment[i] = MonthlyPayment;
                }
                DailyBalance[i] = Balance;
            }
            System.out.println("mPmt = " + Math.round(MonthlyPayment * 100) / 100d + " -> Balance = "
                    + Math.round(Balance * 100) / 100d);

            if (Balance > 0)
                MonthlyPaymentMin = MonthlyPayment;
            else
                MonthlyPaymentMax = MonthlyPayment;

            MonthlyPayment = (MonthlyPaymentMin + MonthlyPaymentMax) / 2;
            MonthlyPayment = Math.ceil(MonthlyPayment * 100) / 100d;

        } while (Math.round(Balance * 100) / 100d >= (NumberPayments / 100d) || Math.round(Balance * 100) / 100d <= -1 * (NumberPayments / 100d));

    }


    /**
     * balance over time for a given monthly payment
     */
    Loan(LocalDate b, LocalDate e, double r, double p, int pd, double pmt) {

        BeginDate = b;
        EndDate = e;
        Rate = r;
        Amount = p;
        PayDayOfMonth = pd;

        int days = (int) ChronoUnit.DAYS.between(b, e) + 1;
        NumberPayments = 1 + (int) ChronoUnit.MONTHS.between(b, e);

        TranDate = new LocalDate[days];
        DailyBalance = new double[days];
        Payment = new double[days];

        double DailyInterest = 1 + (Rate / 100) / 365;

        double Balance = Amount;

        double MonthlyPayment = pmt;

        System.out.println("Opening Balance = " + Math.round(Balance * 100) / 100d);

            LocalDate d = BeginDate;
            Balance = Amount;

            TranDate[0] = d;
            DailyBalance[0] = Balance;

            for (int i = 1; i < days; i++) {
                d = d.plusDays(1);
                TranDate[i] = d;
                Balance *= DailyInterest;
                if (d.getDayOfMonth() == PayDayOfMonth) {
                    Balance -= MonthlyPayment;
                    Payment[i] = MonthlyPayment;
                }
                DailyBalance[i] = Balance;
            }
            System.out.println("mPmt = " + Math.round(MonthlyPayment * 100) / 100d + " -> Balance = "
                    + Math.round(Balance * 100) / 100d);

    }

    int getDays() {

        return TranDate.length - 1;

    }

    int getNumberPayments() {

        return NumberPayments;
    }

    double getMonthlyPayment() {
        return Math.round(Payment[Payment.length - 1] * 100) / 100d;
    }

    void getDetail(String fName) throws IOException {

        FileWriter outputStream = null;

        try {
            double AccumulatedPayments = 0;
            double AccumulatedInterest = 0;
            double AccumulatedInterestYesterday = 0;

            outputStream = new FileWriter(fName);

            outputStream.write("TranDate, DailyBalance, Payment, AccumulatedPayments, AccumulatedInterest, DailyInterest" + "\n");
            for (int i = 0; i < TranDate.length; i++) {
                AccumulatedPayments += Payment[i];
                AccumulatedInterest = DailyBalance[i] - (DailyBalance[0] - AccumulatedPayments);
                outputStream.write(TranDate[i] + ", " + DailyBalance[i] + ", " + Payment[i] + ", " + AccumulatedPayments
                + ", " + AccumulatedInterest + ", " + (AccumulatedInterest - AccumulatedInterestYesterday) + "\n");
                AccumulatedInterestYesterday = AccumulatedInterest;
            }
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }

        }

    }
}

public class App {

    public static void main(String[] args) throws Exception {

        // old local master?
        // after checkout devissue01
        LocalDate FirstPaymentDate = LocalDate.of(2023, 4, 15); // day after credit card payment
        LocalDate LoanDueDate = LocalDate.of(2026, 4, 30); // total payoff before this date
        double Rate = 2.0;
        
        double QuickenBalance = 0;
        LocalDate QuickenDate = LoanDueDate;

        int PayDayOfMonth = FirstPaymentDate.getDayOfMonth();

        DBConnection dbConn = new DBConnection();
        Connection con = dbConn.getConnection();

        try  {
            Statement stmt = con.createStatement();
            String SQL = "select max(TranDate) TranDate, sum(Amount) Amount from vTransaction where Account = 'Summer - Loan'";
            ResultSet rs = stmt.executeQuery(SQL);

            while (rs.next()) {
                QuickenDate = rs.getDate("TranDate").toLocalDate();
                QuickenBalance = rs.getDouble("Amount");  
            }
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
        }

        LocalDate BeginDate = FirstPaymentDate.minusDays(1);
        LocalDate EndDate = LocalDate.of(LoanDueDate.getYear(), LoanDueDate.getMonth(), PayDayOfMonth);
        double Principal = QuickenBalance * Math.pow((1 + (Rate / 100) / 365), QuickenDate.until(BeginDate, ChronoUnit.DAYS));

        Loan l = new Loan(BeginDate, EndDate, Rate, Principal, PayDayOfMonth);
        System.out.println("Remaining Days: " + l.getDays());
        System.out.println("Remaining Months: " + l.getNumberPayments());
        System.out.println("Monthly Payment: " + l.getMonthlyPayment());

        l.getDetail("Loan.csv");

        Loan l2 = new Loan(BeginDate, EndDate, Rate, Principal, PayDayOfMonth, 460);
        System.out.println("Remaining Days: " + l2.getDays());
        System.out.println("Remaining Months: " + l2.getNumberPayments());
        System.out.println("Monthly Payment: " + l2.getMonthlyPayment());

        l2.getDetail("Loan2.csv");


    }
}
