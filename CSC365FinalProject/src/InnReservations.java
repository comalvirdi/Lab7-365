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
import java.util.stream.Collectors;

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
                        ir.fr1(reader);
                        break;
                    case 2:
                        System.out.println("Make a Reservation...");
                        ir.fr2(reader);
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
                        ir.fr6(reader);
                        break;
                    case 7:
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


    private void fr1(Scanner reader) throws SQLException {
        String room;
        String popularityScore;
        String nextAvailableCheckinDate;
        String lengthLastStay;
        String roomName;
        String beds;
        String bedType;
        String maxOcc;
        String basePrice;
        String decor;

        try(Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                System.getenv("HP_JDBC_USER"),
                System.getenv("HP_JDBC_PW")))  {

            StringBuilder sb = new StringBuilder();
            sb.append("with Popularity as (select room, sum(datediff(checkout, checkin)) / 180 RoomPopularityScore from kpinnipa.lab7_reservations join kpinnipa.lab7_rooms on Room = RoomCode where kpinnipa.lab7_reservations.checkin >= (SELECT CURRENT_DATE - INTERVAL 180 DAY) group by room order by RoomPopularityScore desc, room), " +
                    "NextAvailable as (select r2.Room, GREATEST(curdate(), MAX(r2.CheckOut)) as NextAvailableCheckIn from kpinnipa.lab7_reservations as r2 group by r2.Room), " +
                    "lastStay as (select T.Room, datediff(res.checkout, T.lastCheckin) stayLength from ((select r2.Room, Max(checkin) as lastCheckin from kpinnipa.lab7_reservations as r2 group by r2.Room) as T join kpinnipa.lab7_reservations as res on T.lastCheckin = res.Checkin and T.Room = res.Room) where datediff(res.checkout, T.lastCheckin) <> 0) " +
                    "select * from Popularity join NextAvailable using(room) join lastStay using(room) join kpinnipa.lab7_rooms on RoomCode = room order by RoomPopularityScore desc");

            System.out.println("Rooms Availability Information: ");

            System.out.format("\n|%-17s |%-10s |%-25s |%-10s |%-15s |%-15s |%-15s |%-15s |%-25s |%-25s\n", "Popularity Score", "Room Code", "Room Name", "Beds", "Bed Type", "Max Occupancy", "Base Price", "Decor", "Next Available Checkin", "Length of Last Stay");
            System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");


            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(sb.toString());
                if(!rs.next()) {
                    // if code not in system
                    System.out.println("Sorry Invalid Query" );
                }
                else {
                    while (rs.next()) {
                        room = rs.getString("room");
                        popularityScore = rs.getString("RoomPopularityScore");
                        nextAvailableCheckinDate = rs.getString("NextAvailableCheckin");
                        lengthLastStay = rs.getString("stayLength");
                        roomName = rs.getString("RoomName");
                        beds = rs.getString("Beds");
                        bedType = rs.getString("bedType");
                        maxOcc = rs.getString("maxOcc");
                        basePrice = rs.getString("basePrice");
                        decor = rs.getString("decor");

                        System.out.format("\n %-17s  %-10s  %-25s  %-10s  %-15s  %-15s  %-15s  %-15s  %-25s  %-25s\n", popularityScore, room, roomName, beds, bedType, maxOcc, basePrice, decor, nextAvailableCheckinDate, lengthLastStay);
                    }

                }
            }
            catch(SQLException e) {
                e.printStackTrace();
            }

        }

        catch(SQLException e) {
            e.printStackTrace();
        }

    }

    private void fr6(Scanner reader) throws SQLException {
        String Room;
        String Jan;
        String Feb;
        String March;
        String April;
        String May;
        String June;
        String July;
        String August;
        String Septmeber;
        String October;
        String November;
        String December;
        String RoomAnnualTotal;

        try(Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                System.getenv("HP_JDBC_USER"),
                System.getenv("HP_JDBC_PW")))  {

            StringBuilder sb = new StringBuilder();
            sb.append(    "with Years as (select Room, "+
                    "    round(sum(CASE when MONTH(CheckOut) = 1 then DATEDIFF(checkout, checkin) * rate else 0 end),0) as Jan, "+
                    "    round(sum(CASE when MONTH(CheckOut) = 2 then DATEDIFF(checkout, checkin) * rate  else 0 end),0) as Feb, "+
                    "    round(sum(CASE when MONTH(CheckOut) = 3 then DATEDIFF(checkout, checkin) * rate  else 0 end),0) as Mar, "+
                    "    round(sum(CASE when MONTH(CheckOut) = 4 then DATEDIFF(checkout, checkin) * rate else 0 end),0) as Apr, "+
                    "    round(sum(CASE when MONTH(CheckOut) = 5 then DATEDIFF(checkout, checkin) * rate  else 0 end),0) as May, "+
                    "    round(sum(CASE when MONTH(CheckOut) = 6 then DATEDIFF(checkout, checkin) * rate  else 0 end),0) as Jun, "+
                    "    round(sum(CASE when MONTH(CheckOut) = 7 then DATEDIFF(checkout, checkin) * rate  else 0 end),0) as Jul, "+
                    "    round(sum(CASE when MONTH(CheckOut) = 8 then DATEDIFF(checkout, checkin) * rate  else 0 end),0) as Aug, "+
                    "    round(sum(CASE when MONTH(CheckOut) = 9 then DATEDIFF(checkout, checkin) * rate  else 0 end),0) as Sep, "+
                    "    round(sum(CASE when MONTH(CheckOut) = 10 then DATEDIFF(checkout, checkin) * rate  else 0 end),0) as Oct, "+
                    "    round(sum(CASE when MONTH(CheckOut) = 11 then DATEDIFF(checkout, checkin) * rate  else 0 end),0) as Nov, "+
                    "    round(sum(CASE when MONTH(CheckOut) = 12 then DATEDIFF(checkout, checkin) * rate  else 0 end),0) as Dece "+
                    "from kpinnipa.lab7_reservations join kpinnipa.lab7_rooms on Room = RoomCode group by Room), "+
                    " RoomTotals as (select Room, sum(Jan + Feb + Mar + Apr + May + Jun + Jul + Aug + Sep + Oct + Nov + Dece) RoomAnnualTotal from Years group by ROom), "+
                    "monthTotals as (select \"Total\", sum(jan) janTotals, sum(feb) febTotals, sum(Mar) marTotals, sum(Apr) aprTotals, sum(May) mayTotals, sum(Jun) junTotals, sum(Jul) julTotals, sum(Aug) augTotals, sum(sep) sepTotals, sum(oct) octTotals, sum(nov) novTotals, sum(dece) decTotals, (sum(jan) + sum(feb) +sum(Mar) + sum(Apr) + sum(May) + sum(Jun)+ sum(Jul)+ sum(Aug)+ sum(sep)+ sum(oct)+ sum(nov)+ sum(dece)) allRev from Years where Room in (select room from Years)), " +
                    "appd as (select * from Years join RoomTotals using (room)) "+
                    "select * from appd union all select * from monthTotals");

            System.out.println("Rooms Revenue Information: ");

            System.out.format("\n %-15s  %-15s  %-15s  %-15s  %-15s  %-15s  %-15s  %-15s  %-15s  %-15s  %-15s  %-15s  %-15s  %-15s\n", "Room", "Jan", "Feb", "March", "April", "May", "June", "July", "August", "Septmeber", "November", "October", "December", "RoomAnnualTotal");
            System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");


            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(sb.toString());
                if(!rs.next()) {
                    // if code not in system
                    System.out.println("Sorry Invalid Query" );
                }
                else {
                    while (rs.next()) {
                        Room = rs.getString("Room");
                        Jan = rs.getString("Jan");
                        Feb= rs.getString("Feb");
                        March = rs.getString("Mar");
                        April = rs.getString("Apr");
                        May = rs.getString("May");
                        June = rs.getString("Jun");
                        July = rs.getString("Jul");
                        August = rs.getString("Aug");
                        Septmeber = rs.getString("Sep");
                        October = rs.getString("Oct");
                        November = rs.getString("Nov");
                        December = rs.getString("Dece");
                        RoomAnnualTotal = rs.getString("RoomAnnualTotal");


                        System.out.format("\n %-15s  %-15s  %-15s  %-15s  %-15s  %-15s  %-15s  %-15s  %-15s  %-15s  %-15s  %-15s  %-15s  %-15s\n", Room, Jan, Feb, March, April, May, June, July, August, Septmeber, November, October, December, RoomAnnualTotal);
                    }

                }
            }
            catch(SQLException e) {
                e.printStackTrace();
            }

        }

        catch(SQLException e) {
            e.printStackTrace();
        }

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


    private void fr2(Scanner reader) throws SQLException {
        String FN = "";
        String LN = "";
        String roomCode = "";
        String BedType = "";
        String begin = "";
        String end = "";
        String children = "";
        String adults = "";

        String RC = "";
        String RN = "";

        List<LocalDate> dates;
        try {

                // Step 4: Send SQL statement to DBMS
                    //switch to preparedstatement?
                    //Map<String, String> reservationChange = new HashMap<>()

                    System.out.println("Welcome to the Reservation booking system");
                    System.out.println("Enter your First Name");
                    FN = reader.nextLine();

                    System.out.println("Enter your Last Name");
                    LN = reader.nextLine();

                    System.out.println("Enter a preferred Room Code");
                    roomCode = reader.nextLine();

                    System.out.println("Enter a preferred Bed Type");
                    BedType = reader.nextLine();

                    System.out.println("Enter your requested checkin date (YYYY-MM-DD)");
                    begin = reader.nextLine();

                    System.out.println("Enter your requested checkout date (YYYY-MM-DD)");
                    end = reader.nextLine();

                    System.out.println("Enter the number of adults staying");
                    children = reader.nextLine();

                    System.out.println("Enter the number of children staying ");
                    adults = reader.nextLine();
                    System.out.println("Thank you for your input! Looking for available bookings... \r\n");
        } catch (Exception e) {
        }

        try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                System.getenv("HP_JDBC_USER"),
                System.getenv("HP_JDBC_PW"))) {

            StringBuilder sb = new StringBuilder();

            LocalDate checkin = LocalDate.parse(begin);
            LocalDate checkout = LocalDate.parse(end);
            dates = checkin.datesUntil(checkout).collect(Collectors.toList());
            System.out.println(dates);
            sb.append("select * from kpinnipa.lab7_rooms where kpinnipa.lab7_rooms.roomCode not in (select distinct roomcode from kpinnipa.lab7_rooms join kpinnipa.lab7_reservations on roomcode = room where "
        + begin + " >= checkin and " + begin + " < checkout or " + end + " >= checkin and " + end+ "< checkout");

            for (LocalDate date: dates){
                sb.append(" or " + date.toString() + " >= checkin and " + date.toString() + " < checkout");
            }

            sb.append("  order by RoomCode)");


            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(sb.toString());
                if(!rs.next()) {
                    // if code not in system
                    System.out.println("Invalid dates");
                }
                else {
                    while(rs.next()) {
                        RC = rs.getString("RoomCode");
                        RN = rs.getString("RoomName");
                        System.out.println(RC + " " + RN);

                }

            }
            }
        }


    }
}


