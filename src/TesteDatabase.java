import com.meuprojeto.Database;
import java.sql.Connection;

public class TesteDatabase {
    public static void main(String[] args) {
        try (Connection conn = Database.getConnection()) {
            System.out.println("Conexão bem-sucedida! Banco: " + conn.getCatalog());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


