import java.sql.Connection;
import java.sql.DriverManager;

public class Test {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/sim_plane_db";
        String user = "sim_plane";
        String password = "1234";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("✅ MySQL 연결 성공!");
        } catch (Exception e) {
            System.out.println("❌ MySQL 연결 실패...");
            e.printStackTrace();
        }
    }
}
