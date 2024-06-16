package socket;


import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class GerenciadorDeCliente extends Thread {
    private Socket cliente;
    private String nomeDoCliente;
    private DataInputStream input;
    private DataOutputStream output;
    private static List<Livro> livros;
    private static Map<Socket, List<Livro>> livrosAlugadosPorCliente = new HashMap<>();
    final private static String filePath = "livros.json";

public GerenciadorDeCliente(Socket cliente) {
        this.cliente = cliente;
        try {
            this.input = new DataInputStream(cliente.getInputStream());
            this.output = new DataOutputStream(cliente.getOutputStream());

            // Carregar livros apenas uma vez
            if (livros == null) {
                synchronized (GerenciadorDeCliente.class) {
                    /* LEITURA DO ARQUIVO JSON LINHA A LINHA */
                    if (livros == null) {
                        StringBuilder jsonContent = new StringBuilder();
                        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                /* ARMAZENA AS LINHAS LIDAS*/
                                jsonContent.append(line);
                            }
                        }

                        desserializaOsLivros(jsonContent);
                    }
                }
            }

            // Inicializar a lista de livros alugados para este cliente
            livrosAlugadosPorCliente.put(cliente, new ArrayList<>());
        } catch (IOException e) {
            System.err.println("Erro ao inicializar o gerenciador do cliente.");
            e.printStackTrace();
        }
    }

  
}
