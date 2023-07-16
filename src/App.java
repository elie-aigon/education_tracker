import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class App {
    static Connecter connecter = new Connecter();

    public static void main(String[] args) {
        boolean run = true;
        while(run) {
            System.out.println("Systèmes de gestions d'étudiants");
            System.out.println("--------------------------------");
            System.out.println("1 - Afficher tous les étudiants");
            System.out.println("2 - Ajouter un étudiant");
            System.out.println("3 - Supprimer un étudiant");
            System.out.println("4 - Modifier les informations d'un étudiant");
            System.out.println("5 - Rechercher un étudiant");
            System.out.println("6 - Trier étudiants");
            System.out.println("10 - Quitter");
            System.out.print("Entrez le tag de l'action voulue : ");
            Scanner input = new Scanner(System.in);
            switch (input.next()) {
                case "1":
                    ShowAllStudents();
                    break;
                case "2":
                    GetInfo_AddStudents();
                    break;
                case "3":
                    System.out.print("Entrez l'ID de l'étudiant voulu : ");
                    Scanner inputDel = new Scanner(System.in);
                    DelStudent(inputDel.nextInt());
                    break;
                case "4":
                    System.out.print("Entrez l'ID de l'étudiant que vous voulez modifier : ");
                    Scanner scanner = new Scanner(System.in);
                    GetValuesModifStudent(scanner.nextInt());
                    break;
                case "5":
                    System.out.print("Entrez l'ID de l'étudiant voulu : ");
                    Scanner inputFind = new Scanner(System.in);
                    FindStudent(false, "age", "35");
                    break;
                case "6":
                    SortStudents(true);
                    break;
                case "7":
                    SortStudents(false);
                    break;
                case "10":
                    connecter.CloseConnection();
                    System.exit(0);

                default:
                    System.out.println("");
                    System.out.println("Mauvais input, veuillez réessayer");
                    break;
            }
            System.out.println("");
        }
        connecter.CloseConnection();
    }
//    Main methods
    public static void ShowAllStudents() {
        try {
            ResultSet resultSet = connecter.statement.executeQuery("SELECT * FROM students;");
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
            // Je créer une liste avec les noms de colonnes plus appropriés
            String[] columnNames = {"ID", "Prénom", "Nom", "Age", "Notes"};
            // Afficher les titres des colonnes
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(String.format("%-" + (columnWidths[i - 1] + 2) + "s", columnNames[i - 1]));
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
                for (int i = 1; i <= columnCount - 1; i++) {
                    String value = resultSet.getString(i);
                    System.out.print(String.format("%-" + (columnWidths[i - 1] + 2) + "s", value));
                }
                // Je get la liste de notes dans un liste plutot qu'en string
                List<String> notes = new ArrayList<>(Arrays.asList(resultSet.getString("grades").split(";")));
                System.out.print(notes);
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void GetInfo_AddStudents() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Entrez les infos nécessaires à l'ajout d'un étudiant");

        System.out.print("Prénom : ");
        String prenom = scanner.nextLine();

        System.out.print("Nom : ");
        String nom = scanner.nextLine();

        System.out.print("Age: ");
        int age = scanner.nextInt();
        boolean notesValid = false;
        String regex = "[0-9;]+";
        Pattern pattern = Pattern.compile(regex);
        String notes = "";
        while (!notesValid) {
            System.out.print("Notes (format 10;10;10 ) : ");
            notes = scanner.nextLine();
            Matcher matcher = pattern.matcher(notes);
            if (matcher.matches()) {
                notesValid = true;
            } else {
                System.out.println("Erreur lors de l'input, veuillez réessayer");
            }
        }
        AddStudents(prenom, nom, age, notes);
    }
    public static void AddStudents(String first_name, String last_name, int age, String grades) {
        String query = String.format("INSERT INTO students (first_name, last_name, age, grades) VALUES ('%s', '%s', '%d', '%s');",first_name, last_name, age, grades);
        try {
            int rowAffected = connecter.statement.executeUpdate(query);
            if (rowAffected > 0) {
                System.out.println("Le nouvel étudiant à bien été ajouté.");
            } else{
                System.out.println("Une erreur s'est produite");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void FindStudent(boolean isString, String tag, String value) {
        if (isString) {
            try {
                String query = String.format("SELECT * FROM students WHERE %s = '%s';", tag,value);
                ResultSet resultSet = connecter.statement.executeQuery(query);
                if (resultSet.next()) {
                    PrintTableLine(resultSet);
                } else {
                    System.out.println("Aucune conrespondance avec ce paramètre, veuillez reéssayer");
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                String query = String.format("SELECT * FROM students WHERE %s = %s;", tag, value);
                ResultSet resultSet = connecter.statement.executeQuery(query);
                if (resultSet.next()) {
                    PrintTableLine(resultSet);
                } else {
                    System.out.println("Aucune conrespondance avec ce paramètre, veuillez reéssayer");
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public static void GetValuesModifStudent(int id) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(String.format("Modifier les infos de étudiant numéro %d",id));

        System.out.print("Prénom : ");
        String prenom = scanner.nextLine();

        System.out.print("Nom : ");
        String nom = scanner.nextLine();

        System.out.print("Age: ");
        int age = scanner.nextInt();

        boolean notesValid = false;
        String regex = "[0-9;]+";
        Pattern pattern = Pattern.compile(regex);
        String notes = "";
        while (!notesValid) {
            System.out.print("Notes (format 10;10;10 ) : ");
            notes = scanner.next();
            Matcher matcher = pattern.matcher(notes);
            if (matcher.matches()) {
                notesValid = true;
                break;
            } else {
                System.out.println("Erreur lors de l'input, veuillez réessayer");
            }
        }
        ModifStudentQuery(id, prenom, nom, age, notes);
    }
    public static void ModifStudentQuery(int id, String first_name, String last_name, int age, String grades) {
        try {
            String query = String.format("UPDATE students SET first_name = '%s', last_name = '%s', age = %d, grades = '%s' WHERE id = %d;",first_name, last_name, age, grades, id);
            int rowAffected = connecter.statement.executeUpdate(query);
            if(rowAffected > 0) {
                System.out.println("L'étudiant a bien été modifié");
            } else {
                System.out.println("Une erreur s'est produite, veuillez réessayer");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void DelStudent(int id) {
        String a = String.format("DELETE FROM students WHERE id = %d;", id);
        try {
            int rowAffected = connecter.statement.executeUpdate(a);
            if (rowAffected > 0) {
                connecter.connection.commit();
                System.out.println(String.format("You delete the sutdent with an ID of : %d", id));
            } else{
                System.out.println(String.format("No Student found with an ID of : %d", id));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    Tools Methods
    public static void PrintTableLine(ResultSet resultSet) {
        try {
            int id = resultSet.getInt("id");
            String first_name = resultSet.getString("first_name");
            String last_name = resultSet.getString("last_name");
            int age = resultSet.getInt("age");
            List<String> grades = new ArrayList<>(Arrays.asList(resultSet.getString("grades").split(";")));
            System.out.println("ID: " + id + ", Prénom: " + first_name + ", Nom: " + last_name + ", Age: " + age + ", Notes: " + grades);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

// Bonus
    public static void SortStudents(boolean isAge) {
        if (isAge) {
            try {
                ResultSet resultSet = connecter.statement.executeQuery("SELECT * FROM students ORDER BY age;");
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
                // Je créer une liste avec les noms de colonnes plus appropriés
                String[] columnNames = {"ID", "Prénom", "Nom", "Age", "Notes"};
                // Afficher les titres des colonnes
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(String.format("%-" + (columnWidths[i - 1] + 2) + "s", columnNames[i - 1]));
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
                    for (int i = 1; i <= columnCount - 1; i++) {
                        String value = resultSet.getString(i);
                        System.out.print(String.format("%-" + (columnWidths[i - 1] + 2) + "s", value));
                    }
                    // Je get la liste de notes dans un liste plutot qu'en string
                    ArrayList<String> notes = new ArrayList<>(Arrays.asList(resultSet.getString("grades").split(";")));
                    System.out.print(notes);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                ResultSet resultSet = connecter.statement.executeQuery("SELECT * FROM students ORDER BY grades;");
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
                // Je créer une liste avec les noms de colonnes plus appropriés
                String[] columnNames = {"ID", "Prénom", "Nom", "Age", "Notes", "Moyennes"};
                // Afficher les titres des colonnes
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(String.format("%-" + (columnWidths[i - 1] + 2) + "s", columnNames[i - 1]));
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
                    for (int i = 1; i <= columnCount - 1; i++) {
                        String value = resultSet.getString(i);
                        System.out.print(String.format("%-" + (columnWidths[i - 1] + 2) + "s", value));
                    }
                    // Je get la liste de notes dans un liste plutot qu'en string
                    List<String> notes = new ArrayList<>(Arrays.asList(resultSet.getString("grades").split(";")));
                    System.out.print(notes);
                    float moy = 0;

                    for (int i = 0; i < notes.size(); i++) {
                        moy+= Float.parseFloat(notes.get(i));
                    }
                    moy/= notes.size();
                    System.out.print(moy);
                    System.out.println();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
