package socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    private static int PORTA = 8888;

    public static void main(String[] args) {
        try (ServerSocket servidor = new ServerSocket(PORTA)) {
            System.out.println("Servidor rodando na porta:"+ PORTA);

            while (true) {
                Socket cliente = servidor.accept();
                System.out.println("Cliente - IP conectado: " + cliente.getInetAddress().getHostAddress());
                GerenciadorDeCliente GerenciadorDeCliente = new GerenciadorDeCliente(cliente);
                GerenciadorDeCliente.start();
            }
        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        }
    }
}
