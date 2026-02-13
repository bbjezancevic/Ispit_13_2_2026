import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        int odabir;
        do {
            ispisiIzbornik();
            odabir = Integer.parseInt(sc.nextLine());

            switch (odabir) {
                case 1:
                    unesiPolaznika();
                    break;
                case 2:
                    unesiProgramObrazovanja();
                    break;
                case 3:
                    upisiPolaznika();
                    break;
                case 4:
                    prebaciPolaznika();
                    break;
                case 5:
                    printProgramObrazovanja();
                    break;
                case 6:
                    System.out.println("Izlazim iz izbornika!");
                    break;
                default:
                    System.out.println("Krivo unesen odabir, pokusajte ponovno!");
                    break;
            }

        } while (odabir !=6);
    }

    private static void ispisiIzbornik() {
        System.out.println("++++++++++++++++++++++++++++++++");
        System.out.printf("Unesite broj ispred naredbe koju zelite izvrsiti:\n1 - Unesi polaznika\n2 - Unesi program obrazovanja\n3 - Upisi polaznika\n4 - Prebaci polaznika u drugi program obrazovanja\n5 - Ispisi polaznike programa obrazovanja\n6 - Izlaz iz aplikacije\n");
    }

    private static void unesiPolaznika() {
        try (Connection connection = createDataSource().getConnection();
             CallableStatement cs = connection.prepareCall("{call UnesiPolaznika(?,?)}")) {
            System.out.println("Unesite ime polaznika:");
            String ime = sc.nextLine();
            System.out.println("Unesite prezime polaznika:");
            String prezime = sc.nextLine();
            cs.setString(1, ime);
            cs.setString(2, prezime);
            cs.executeUpdate();

            System.out.println("Polaznik: " + ime + " " + prezime + "uspjesno unesen u bazu!");
        } catch (SQLException e) {
            System.err.println("Greska pri spajanju na bazu!");
            e.printStackTrace();
        }
    }

    private static void unesiProgramObrazovanja () {
        try (Connection connection = createDataSource().getConnection();
        CallableStatement cs = connection.prepareCall("{call UnesiProgramObrazovanja(?,?)}")) {
            System.out.println("Unesite naziv programa obrazovanja:");
            String naziv = sc.nextLine();
            System.out.println("Unesite broj CSVET bodova programa obrazovanja:");
            int CSVET = Integer.parseInt(sc.nextLine());
            cs.setString(1, naziv);
            cs.setInt(2, CSVET);
            cs.executeUpdate();

            System.out.println("Program obrazovana: " + naziv + " koji nosi: " + CSVET + " CSVET bodova uspjesno unesenu bazu!");
        } catch (SQLException e) {
            System.err.println("Greska pri spajanju na bazu!");
            e.printStackTrace();
        }
    }

    private static void upisiPolaznika() {
        try (Connection connection = createDataSource().getConnection();
        CallableStatement cs = connection.prepareCall("{call UpisiPolaznika(?,?)}")) {
            System.out.println("Unesite ID polaznika kojeg zelite upisati:");
            int polaznikID = Integer.parseInt(sc.nextLine());
            System.out.println("Unesite ID programa obrazovanja u kojeg zelite upisati polaznika:");
            int programID = Integer.parseInt(sc.nextLine());
            cs.setInt(1, polaznikID);
            cs.setInt(2, programID);
            cs.executeUpdate();

            System.out.println("Polaznik s ID-em: " + polaznikID + " upisan na program obrazovanja s ID-em: " + programID);
        } catch (SQLException e) {
            System.err.println("Greska pri spajanju na bazu!");
            e.printStackTrace();
        }
    }

    private static void prebaciPolaznika () {
        try (Connection connection = createDataSource().getConnection()) {
            try (CallableStatement cs = connection.prepareCall("{call PrebaciPolaznika(?,?,?)}")) {
                connection.setAutoCommit(false);
                System.out.println("Unesite ID polaznika kojeg zelite prebaciti:");
                int polaznikID = Integer.parseInt(sc.nextLine());
                System.out.println("Unesite ID trenutno upisanog programa obrazovanja:");
                int stariProgramID = Integer.parseInt(sc.nextLine());
                System.out.println("Unesite ID novog programa obrazovanja u koji zelite upisati korisnika:");
                int noviProgramID = Integer.parseInt(sc.nextLine());
                cs.setInt(1, polaznikID);
                cs.setInt(2, stariProgramID);
                cs.setInt(3, noviProgramID);
                cs.executeUpdate();
                connection.commit();

                System.out.println("Polaznik s ID-em: " + polaznikID + " prebacen sa programa s ID-em: " + stariProgramID + " na program s ID-em: " + noviProgramID);
            } catch (SQLException e) {
                System.err.println("Greska pri prebacivanju polaznika s jednog na drugi program obrazovanja.");
                connection.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.err.println("Greska pri spajanju na bazu!");
            e.printStackTrace();
        }
    }

    private static void printProgramObrazovanja() {
        try (Connection connection = createDataSource().getConnection();
        CallableStatement cs = connection.prepareCall("{call PrintProgramObrazovanja(?)}")) {
            System.out.println("Unesite ID programa obrazovanja za kojeg zelite ispisati upisane polaznike:");
            int programID = Integer.parseInt(sc.nextLine());
            cs.setInt(1, programID);
            ResultSet rs = cs.executeQuery();

            while(rs.next()) {
                System.out.printf("%s %s - %s - %d%n", rs.getString("Ime"), rs.getString("Prezime"), rs.getString("Naziv"), rs.getInt("CSVET"));
            }

        } catch (SQLException e) {
            System.err.println("Greska pri spajanju na bazu!");
            e.printStackTrace();
        }
    }

    private static DataSource createDataSource() {
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setServerName("localhost");
        ds.setDatabaseName("JavaAdv");
        ds.setUser("sa");
        ds.setPassword("SQL");
        ds.setEncrypt(false);
        return ds;
    }
}