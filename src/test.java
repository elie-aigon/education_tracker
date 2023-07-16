import java.sql.*;

public class test {
    public static void main(String[] args) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Établir la connexion à la base de données
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/education_tracker", "random", "Azerty13!");

            // Créer un objet Statement
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            // Exécuter la requête SQL pour récupérer les données
            String query = "SELECT * FROM students";
            resultSet = statement.executeQuery(query);

            // Récupérer les métadonnées pour obtenir le nombre de colonnes
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Déterminer la largeur maximale pour chaque colonne
            int[] columnWidths = new int[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnWidths[i - 1] = metaData.getColumnName(i).length();
            }

            // Parcourir les résultats et mettre à jour les largeurs maximales
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String value = resultSet.getString(i);
                    if (value != null && value.length() > columnWidths[i - 1]) {
                        columnWidths[i - 1] = value.length();
                    }
                }
            }

            // Afficher les titres des colonnes
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                System.out.print(String.format("%-" + (columnWidths[i - 1] + 2) + "s", columnName));
            }
            System.out.println();

            // Afficher les séparateurs
            for (int i = 1; i <= columnCount; i++) {
                int width = columnWidths[i - 1] + 2;
                for (int j = 0; j < width; j++) {
                    System.out.print("-");
                }
            }
            System.out.println();

            // Afficher les données du tableau
            resultSet.beforeFirst();
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String value = resultSet.getString(i);
                    System.out.print(String.format("%-" + (columnWidths[i - 1] + 2) + "s", value));
                }
                System.out.println();
            }
        } catch (SQLException e) {
            // Gérer les exceptions
            e.printStackTrace();
        } finally {
            // Fermer les ressources
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
