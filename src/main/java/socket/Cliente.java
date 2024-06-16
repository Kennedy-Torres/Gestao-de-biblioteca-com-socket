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

			/* 2. FLUXO DE MENSAGENS */
			DataOutputStream output = new DataOutputStream(cliente.getOutputStream());
			final DataInputStream input = new DataInputStream(cliente.getInputStream());


			/**
			 * Utilizamos Thread para garantir o paralelismo entre cliente e servidor:
			 * 1. Ler mensagens enviadas do servidor
			 * 2. Ao mesmo tempo enviar mensagem ao servidor
			 */
			/* 3. RECEBENDO MENSAGEM DO SERVIDOR */
			final Socket finalCliente = cliente; //a referência ao objeto Socket não mudará
			new Thread(() -> {
				try {
					while (!finalCliente.isClosed()) {
						try{
							String mensagemDoServidor = input.readUTF();
							System.out.println(mensagemDoServidor);
						}catch(EOFException e){
							// conexao do servidor foi encerrada enquanto o cliente tentava enviar uma mensagem
							System.out.println("O cliente perdeu a conexão com o servidor." +
									"\nPara encerrar digite '::sair'");
							break;
						}catch (SocketException e){ // ---> lida com desconexões inesperadas do servidor
							System.out.println("Conexão do servidor foi encerrada de forma inesperada.");
							break;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}finally {
					try {
						finalCliente.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();

		} catch (UnknownHostException e) {
			System.out.println("Problema no host");
			System.err.println("Erro: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Problema na porta (Porta está oculpada ou fora do ar)");
			System.err.println("Erro: " + e.getMessage());
		}
	}
}
