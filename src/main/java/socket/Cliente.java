package socket;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Cliente {
	public static void main(String[] args) {
		Socket cliente = null;
		try {
			/* 1. CONEXAO */
			cliente = new Socket("127.0.0.1", 8888);

		} catch (UnknownHostException e) {
			System.out.println("Problema no host");
			System.err.println("Erro: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Problema na porta (Porta está oculpada ou fora do ar)");
			System.err.println("Erro: " + e.getMessage());
		}
	}
}
