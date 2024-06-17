package socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Servidor {
	private static int PORTA = 8888;
	private static boolean isDisponivel = true;
	private static final List<Socket> clientes = new ArrayList<>();

	public static void main(String[] args) {
		try (ServerSocket servidor = new ServerSocket(PORTA)) {
			System.out.println("Servidor rodando na porta:" + PORTA);
			System.out.println("Caso precise encerrar o servidor com clientes conectados digite: '::sair'.");

			// Thread para monitorar a entrada do console do servidor
			new Thread(() -> {
				Scanner consoleDoServidor = new Scanner(System.in);
				String comando;
				while (isDisponivel) {
					comando = consoleDoServidor.nextLine();
					if (comando.equalsIgnoreCase("::sair")) {
						System.out.println("Desligando o servidor...");
						isDisponivel = false;
						try {
							servidor.close();
							synchronized (clientes) {
								for (Socket cliente : clientes) {
									if (!cliente.isClosed()) {
										cliente.close();
									}
								}
							}
						} catch (IOException e) {
							System.err.println("Erro ao fechar o servidor: " + e.getMessage());
						}
						consoleDoServidor.close();
						break;
					}
				}
			}).start();

			while (isDisponivel) {
				try {
					/* ACEITA A CONEXÃO DO CLIENTE */
					Socket cliente = servidor.accept();
					synchronized (clientes) {
						clientes.add(cliente);
					}

					System.out.println("Cliente - IP conectado: " + cliente.getInetAddress().getHostAddress());

					/*
					 * PARA TODO CLIENTE CONECTADO, UM NOVO OBJETO "GerenciadorDeCliente" É
					 * INSTÂNCIADO
					 */
					GerenciadorDeCliente GerenciadorDeCliente = new GerenciadorDeCliente(cliente);
					GerenciadorDeCliente.start();
				} catch (IOException e) {
					if (!isDisponivel) {
						System.out.println("Servidor foi desligado.");
					} else {
						System.err.println("Erro ao aceitar conexão de cliente: " + e.getMessage());
					}
				}

			}

		} catch (IOException e) {
			System.err.println("Erro no servidor: " + e.getMessage());
		}
	}
}
