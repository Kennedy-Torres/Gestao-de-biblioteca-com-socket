package socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Servidor {
    private static int PORTA = 8888;

    public static void main(String[] args) {
        try (ServerSocket servidor = new ServerSocket(PORTA)) {
            System.out.println("Servidor rodando na porta:"+ PORTA);

            Scanner consoleDoServidor = new Scanner(System.in);
            
            while (true) {
            	/* ACEITA A CONEXÃO DO CLIENTE*/
                Socket cliente = servidor.accept();
                System.out.println("Cliente - IP conectado: " + cliente.getInetAddress().getHostAddress());
                
                /* PARA TODO CLIENTE CONECTADO, UM NOVO OBJETO "GerenciadorDeCliente" É INSTÂNCIADO*/
                GerenciadorDeCliente GerenciadorDeCliente = new GerenciadorDeCliente(cliente);
                GerenciadorDeCliente.start();
                
                /* ENCERRA O SERVIDOR*/
                System.out.println("Caso precise encerrar o servidor digite: ::sair");
                String desconectarServidor = consoleDoServidor.nextLine();
                if(desconectarServidor.equalsIgnoreCase("::sair")) {
                	System.out.println("Desligando o servidor...");
                	servidor.close();
                	cliente.close();
                	break;
                }
            }
            
            
            consoleDoServidor.close();
        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        }
    }
}
