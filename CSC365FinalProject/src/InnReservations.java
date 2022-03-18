import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.*;
import java.lang.System;

import java.util.Scanner;
import java.time.LocalDate;

public class InnReservations {
    public static void main(String[] args) {
        try {
            Scanner reader = new Scanner(System.in);
            InnReservations ir = new InnReservations();
            System.out.println("Hello! We are the Inn Managers. Please refer to the following options:");
            int optionNum = Integer.parseInt(args[0]);
            while(optionNum != -1) {
                System.out.println("\n");
                System.out.println("1: Rooms and Rates");
                System.out.println("2: Make a Reservation");
                System.out.println("3: Change Reservation");
                System.out.println("4: Cancel Reservation");
                System.out.println("5: Inn Revenue");
                System.out.println("6: Exit");
                System.out.print("Please Choose an Option: ");

                optionNum = reader.nextInt();
                reader.nextLine();

                //Switch statement for different options
                switch(optionNum) {
                    case 1:
                        System.out.println("Rooms and Rates...");
                        ir.demo1();
                        break;
                    case 2:
                        System.out.println("Make a Reservation...");
                        ir.demo1();
                        break;
                    case 3:
                        System.out.println("Change Reservation...");
                        ir.fr3(reader);
                        break;
                    case 4:
                        System.out.println("Proceeding to Cancel Reservation...");
                        ir.demo1();
                        break;
                    case 5:
                        System.out.println("Proceeding to Inn Revenue...");
                        ir.demo1();
                        break;
                    case 6:
                        System.out.println("Thank you for using our service. Good bye!");
                        optionNum = -1;
                        System.exit(0);
                        break;
                }
            }

        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        } catch (Exception e2) {
            System.err.println("Exception: " + e2.getMessage());
        }
    }

    // Demo1 - Establish JDBC connection, execute DDL statement
    private void demo1() throws SQLException {

        System.out.println("demo1: Add AvailUntil column to hp_goods table\r\n");

        // Step 1: Establish connection to RDBMS
        try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                System.getenv("HP_JDBC_USER"),
                System.getenv("HP_JDBC_PW"))) {
            // Step 2: Construct SQL statement
            String sql = "ALTER TABLE hp_goods ADD COLUMN AvailUntil DATE";

            // Step 3: (omitted in this example) Start transaction

            try (Statement stmt = conn.createStatement()) {

                // Step 4: Send SQL statement to DBMS
                boolean exRes = stmt.execute(sql);

                // Step 5: Handle results
                System.out.format("Result from ALTER: %b %n", exRes);
            }

            // Step 6: (omitted in this example) Commit or rollback transaction
        }
        // Step 7: Close connection (handled by try-with-resources syntax)
    }

    private void fr3(Scanner reader) throws SQLException {
        String firstname;
        String lastname;
        String checkin;
        String checkout;
        String children;
        String adults;
        System.out.println("Please enter your reservation code: ");
        int resCode = reader.nextInt();
        reader.nextLine(); //throw away \n
        // Step 1: Establish connection to RDBMS
        try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                System.getenv("HP_JDBC_USER"),
                System.getenv("HP_JDBC_PW"))) {
            // Step 2: Construct SQL statement
            StringBuilder sb = new StringBuilder();
            sb.append("select CODE from snrietke.lab7_reservations WHERE CODE =");
            sb.append(resCode);
            sb.append(";");

            // Step 3: (omitted in this example) Start transaction

            try (Statement stmt = conn.createStatement()) {

                // Step 4: Send SQL statement to DBMS
                ResultSet rs = stmt.executeQuery(sb.toString());
                if(!rs.next()) {
                    // if code not in system
                    System.out.println("Invalid reservation code:\r\n" + resCode);
                }
                else {
                    //switch to preparedstatement?
                    //Map<String, String> reservationChange = new HashMap<>();
                    System.out.println("Valid reservation code:\r\n" + resCode);

                    System.out.println("Please fill out the following form: " + resCode);
                    System.out.println("New First Name or no change?");
                    firstname = reader.nextLine();
                    //reservationChange.put("FirstName", firstname);
                    System.out.println("New Last Name or no change?");
                    lastname = reader.nextLine();
                    //reservationChange.put("LastName", lastname);
                    System.out.println("New Check In Date (YYYY-MM-DD) or no change?");
                    checkin = reader.nextLine();
                    //reservationChange.put("CheckIn", checkin);
                    System.out.println("New Check Out Date (YYYY-MM-DD) or no change?");
                    checkout = reader.nextLine();
                    System.out.println("New Number of Adults or no change?");
                    adults = reader.nextLine();
                    System.out.println("New Number of Children or no change? ");
                    children  = reader.nextLine();
                    System.out.println("Thank you for your input! Processing update... \r\n");

                    //String sb2 = "update lab7_reservations set CODE = CODE";

                    if(!firstname.equalsIgnoreCase("no change")) {
                        //sb2 += (", FirstName = '" + firstname + "'");
                        try(PreparedStatement ps = conn.prepareStatement("update snrietke.lab7_reservations set firstname=? where code=?;")) {
                            ps.setString(1, firstname);
                            ps.setInt(2, resCode);
                            ps.executeUpdate();
                            System.out.println("Successfully updated first name.");
                        }
                        catch(SQLException e) {
                            System.out.println("Error updating first name");
                            e.printStackTrace();
                        }
                    }
                    if(!lastname.equalsIgnoreCase("no change")) {
                        //sb2 += (", LastName = '" + lastname + "'");
                        try(PreparedStatement ps = conn.prepareStatement("update snrietke.lab7_reservations set lastname=? where code=?;")) {
                            ps.setString(1, lastname);
                            ps.setInt(2, resCode);
                            ps.executeUpdate();
                            System.out.println("Successfully updated Last name.");
                        }
                        catch(SQLException e) {
                            System.out.println("Error updating Last name.");
                            e.printStackTrace();
                        }
                    }
                    if(!checkin.equalsIgnoreCase("no change")) {
                        // need to query and see if this room is available on that check in date (trigger?)
                        //sb2 += (", CheckIn = '" + checkin + "'");

                        try (PreparedStatement ps = conn.prepareStatement("update snrietke.lab7_reservations set CheckIn=? where code = ?;")) {
                            ps.setDate(1, java.sql.Date.valueOf(checkin));
                            ps.setInt(2, resCode);
                            ps.executeUpdate();
                            System.out.println("Successfully updated Check In Date");
                        }
                        catch (SQLException e) {
                            System.out.println("Error updating check in date");
                            e.printStackTrace();
                        }
                    }
                    if(!checkout.equalsIgnoreCase("no change")) {
                        // need to query and see if this room is available on that new check out date
                        //sb2 += (", CheckOut = '" + checkout + "'");
                        try(PreparedStatement ps = conn.prepareStatement("update snrietke.lab7_reservations set Checkout=? where code=?;")) {
                            ps.setDate(1, java.sql.Date.valueOf(checkout));
                            ps.setInt(2, resCode);
                            ps.executeUpdate();
                            System.out.println("Successfully updated Check Out Date");
                        }
                        catch (SQLException e) {
                            System.out.println("Error updating check out date");
                            e.printStackTrace();
                        }
                    }
                    if(!adults.equalsIgnoreCase("no change")) {
                        //sb2 += (", Adults = '" + adults + "'");
                        try(PreparedStatement ps = conn.prepareStatement("update snrietke.lab7_reservations set Adults=? where code=?;")) {
                            ps.setInt(1, Integer.parseInt(adults));
                            ps.setInt(2, resCode);
                            ps.executeUpdate();
                            System.out.println("Successfully updated number of adults.");
                        }
                        catch(SQLException e) {
                            System.out.println("Error updating number of adults");
                            e.printStackTrace();
                        }
                    }
                    if(!children.equalsIgnoreCase("no change")) {
                        //sb2 += (", Kids = '" + children + "'");
                        try(PreparedStatement ps = conn.prepareStatement("update snrietke.lab7_reservations set Kids=? where code=?;")) {
                            ps.setInt(1, Integer.parseInt(children));
                            ps.setInt(2, resCode);
                            ps.executeUpdate();
                            System.out.println("Successfully updated number of children.");
                        }
                        catch(SQLException e) {
                            System.out.println("Error updating number of children");
                            e.printStackTrace();
                        }
                    }
                }

                // Step 5: Handle results

            } catch(SQLException e) {
                e.printStackTrace();
            }

            // Step 6: (omitted in this example) Commit or rollback transaction
        } catch(SQLException e) {
            e.printStackTrace();
        }
        // Step 7: Close connection (handled by try-with-resources syntax)
    }
}
