import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class Barbearia {
    private Connection conexao;

    public Barbearia() {
        try {
            conexao = DriverManager.getConnection("jdbc:derby:barbeariaDB;create=true");
            criarTabelas();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void criarTabelas() throws SQLException {
        Statement stmt = conexao.createStatement();

        try {
            stmt.executeUpdate("CREATE TABLE clientes (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                    "nome VARCHAR(255), " +
                    "telefone VARCHAR(20))");
        } catch (SQLException e) {
            if (e.getSQLState().equals("X0Y32")) {
                System.out.println("Tabela 'clientes' já existe. Continuando...");
            } else {
                throw e;
            }
        }

        try {
            stmt.executeUpdate("CREATE TABLE agendamentos (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                    "cliente_id INT, " +
                    "data_hora VARCHAR(50), " +
                    "FOREIGN KEY (cliente_id) REFERENCES clientes(id))");
        } catch (SQLException e) {
            if (e.getSQLState().equals("X0Y32")) {
                System.out.println("Tabela 'agendamentos' já existe. Continuando...");
            } else {
                throw e;
            }
        }

        try {
            stmt.executeUpdate("CREATE TABLE pagamentos (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                    "agendamento_id INT, " +
                    "valor DOUBLE, " +
                    "data_pagamento VARCHAR(50), " +
                    "FOREIGN KEY (agendamento_id) REFERENCES agendamentos(id))");
        } catch (SQLException e) {
            if (e.getSQLState().equals("X0Y32")) {
                System.out.println("Tabela 'pagamentos' já existe. Continuando...");
            } else {
                throw e;
            }
        }

        stmt.close();
    }

    public void cadastrarCliente(String nome, String telefone) {
        try {
            PreparedStatement stmt = conexao.prepareStatement("INSERT INTO clientes (nome, telefone) VALUES (?, ?)");
            stmt.setString(1, nome);
            stmt.setString(2, telefone);
            stmt.executeUpdate();
            stmt.close();
            System.out.println("Cliente cadastrado com sucesso: " + nome);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void agendarServico(String nomeCliente, String dataHora) {
        try {
            int clienteId = buscarClienteId(nomeCliente);
            if (clienteId != -1) {
                PreparedStatement stmt = conexao.prepareStatement("INSERT INTO agendamentos (cliente_id, data_hora) VALUES (?, ?)");
                stmt.setInt(1, clienteId);
                stmt.setString(2, dataHora);
                stmt.executeUpdate();
                stmt.close();
                System.out.println("Serviço agendado com sucesso para " + nomeCliente + " na data/hora " + dataHora);
            } else {
                System.out.println("Cliente não encontrado.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void realizarPagamento(String nomeCliente, double valor, String dataPagamento) {
        try {
            int agendamentoId = buscarAgendamentoId(nomeCliente);
            if (agendamentoId != -1) {
                PreparedStatement stmt = conexao.prepareStatement("INSERT INTO pagamentos (agendamento_id, valor, data_pagamento) VALUES (?, ?, ?)");
                stmt.setInt(1, agendamentoId);
                stmt.setDouble(2, valor);
                stmt.setString(3, dataPagamento);
                stmt.executeUpdate();
                stmt.close();
                System.out.println("Pagamento realizado com sucesso para " + nomeCliente);
            } else {
                System.out.println("Agendamento não encontrado para este cliente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int buscarClienteId(String nome) {
        try {
            PreparedStatement stmt = conexao.prepareStatement("SELECT id FROM clientes WHERE nome = ?");
            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                rs.close();
                stmt.close();
                return id;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private int buscarAgendamentoId(String nomeCliente) {
        try {
            int clienteId = buscarClienteId(nomeCliente);
            if (clienteId != -1) {
                PreparedStatement stmt = conexao.prepareStatement("SELECT id FROM agendamentos WHERE cliente_id = ?");
                stmt.setInt(1, clienteId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int id = rs.getInt("id");
                    rs.close();
                    stmt.close();
                    return id;
                }
                rs.close();
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void listarClientes() {
        try {
            Statement stmt = conexao.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM clientes");
            System.out.println("\nLista de Clientes:");
            System.out.println("+----+---------------------------+------------------+");
            System.out.println("| ID | Nome                      | Telefone         |");
            System.out.println("+----+---------------------------+------------------+");
            while (rs.next()) {
                System.out.printf("| %2d | %-25s | %-16s |%n", rs.getInt("id"), rs.getString("nome"), rs.getString("telefone"));
            }
            System.out.println("+----+---------------------------+------------------+");
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listarAgendamentos() {
        try {
            Statement stmt = conexao.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT a.id, c.nome, a.data_hora FROM agendamentos a JOIN clientes c ON a.cliente_id = c.id");
            System.out.println("\nLista de Agendamentos:");
            System.out.println("+----+---------------------------+---------------------+");
            System.out.println("| ID | Cliente                   | Data/Hora           |");
            System.out.println("+----+---------------------------+---------------------+");
            while (rs.next()) {
                System.out.printf("| %2d | %-25s | %-19s |%n", rs.getInt("id"), rs.getString("nome"), rs.getString("data_hora"));
            }
            System.out.println("+----+---------------------------+---------------------+");
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listarPagamentos() {
        try {
            Statement stmt = conexao.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT p.id, c.nome, p.valor, p.data_pagamento FROM pagamentos p JOIN agendamentos a ON p.agendamento_id = a.id JOIN clientes c ON a.cliente_id = c.id");
            System.out.println("\nLista de Pagamentos:");
            System.out.println("+----+---------------------------+---------+-----------------+");
            System.out.println("| ID | Cliente                   | Valor   | Data Pagamento  |");
            System.out.println("+----+---------------------------+---------+-----------------+");
            while (rs.next()) {
                System.out.printf("| %2d | %-25s | %-7.2f | %-15s |%n", rs.getInt("id"), rs.getString("nome"), rs.getDouble("valor"), rs.getString("data_pagamento"));
            }
            System.out.println("+----+---------------------------+---------+-----------------+");
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
