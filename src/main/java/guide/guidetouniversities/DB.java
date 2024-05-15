package guide.guidetouniversities;

    import java.sql.*;

    /**
     * Підключення до бд реєстрація і авторизація
     */
    public class DB {
        Connection dbConnection;

        /**
         * Підключення до бд
         * @return
         * @throws ClassNotFoundException
         * @throws SQLException
         */
        public Connection getDbConnection() throws ClassNotFoundException, SQLException {

            Class.forName("com.mysql.cj.jdbc.Driver");
            dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dovidnik_universiti?useUnicode=true&characterEncoding=utf8", "root", "");
            System.out.println(dbConnection);
            return dbConnection;

        }


}
