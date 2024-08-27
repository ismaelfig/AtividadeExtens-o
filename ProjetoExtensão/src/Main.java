import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Barbearia barbearia = new Barbearia();
        Scanner scanner = new Scanner(System.in);
        int opcao = 0;

        do {
            System.out.println("\n=== Barbearia ===");
            System.out.println("1. Cadastrar Cliente");
            System.out.println("2. Agendar Serviço");
            System.out.println("3. Realizar Pagamento");
            System.out.println("4. Listar Clientes");
            System.out.println("5. Listar Agendamentos");
            System.out.println("6. Listar Pagamentos");
            System.out.println("7. Sair");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine();  // Consumir nova linha

            switch (opcao) {
                case 1:
                    System.out.print("Nome do Cliente: ");
                    String nome = scanner.nextLine();
                    System.out.print("Telefone do Cliente: ");
                    String telefone = scanner.nextLine();
                    barbearia.cadastrarCliente(nome, telefone);
                    break;
                case 2:
                    System.out.print("Nome do Cliente: ");
                    String nomeCliente = scanner.nextLine();
                    System.out.print("Data e Hora (dd/MM/yyyy HH:mm): ");
                    String dataHora = scanner.nextLine();
                    barbearia.agendarServico(nomeCliente, dataHora);
                    break;
                case 3:
                    System.out.print("Nome do Cliente: ");
                    String nomePag = scanner.nextLine();
                    System.out.print("Valor do Pagamento: ");
                    double valor = scanner.nextDouble();
                    scanner.nextLine();  // Consumir nova linha
                    System.out.print("Data do Pagamento (dd/MM/yyyy): ");
                    String dataPagamento = scanner.nextLine();
                    barbearia.realizarPagamento(nomePag, valor, dataPagamento);
                    break;
                case 4:
                    barbearia.listarClientes();
                    break;
                case 5:
                    barbearia.listarAgendamentos();
                    break;
                case 6:
                    barbearia.listarPagamentos();
                    break;
                case 7:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } while (opcao != 7);

        scanner.close();
    }
}
