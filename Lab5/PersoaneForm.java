import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PersoaneForm extends JFrame {
    private JTextField numeField;
    private JTextField varstaField;
    private JTextField cnpField;

    // Conexiunea la baza de date utilizând variabilele specificate
    private final String DB_URL = "jdbc:mysql://localhost/persoane";
    private final String USERNAME = "root";
    private final String PASSWORD = "";

    public PersoaneForm() {
        setTitle("Formular Persoane");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2));

        JLabel numeLabel = new JLabel("Nume:");
        numeField = new JTextField();
        JLabel varstaLabel = new JLabel("Varsta:");
        varstaField = new JTextField();
        JLabel cnpLabel = new JLabel("CNP:");
        cnpField = new JTextField();

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String nume = numeField.getText();
                    int varsta = Integer.parseInt(varstaField.getText());
                    String cnp = cnpField.getText();

                    if (nume.isEmpty() || cnp.length() != 13) {
                        JOptionPane.showMessageDialog(null, "Date invalide!");
                        return;
                    }

                    insertIntoDatabase(nume, varsta, cnp);
                    JOptionPane.showMessageDialog(null, "Inregistrare adaugata cu succes!");

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Varsta trebuie sa fie un numar valid.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Eroare la conectarea la baza de date.");
                }
            }
        });

        add(numeLabel);
        add(numeField);
        add(varstaLabel);
        add(varstaField);
        add(cnpLabel);
        add(cnpField);
        add(okButton);
    }

    private void insertIntoDatabase(String nume, int varsta, String cnp) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "INSERT INTO Persoane (nume, varsta, cnp) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nume);
            stmt.setInt(2, varsta);
            stmt.setString(3, cnp);

            stmt.executeUpdate();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            PersoaneForm form = new PersoaneForm();
            form.setVisible(true);
        });
    }
}
