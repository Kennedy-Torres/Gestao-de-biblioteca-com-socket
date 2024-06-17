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

    @Override
    public void run() {
        try {
            output.writeUTF("Digite seu nome: ");
            output.flush();
            this.nomeDoCliente = input.readUTF();
            // log p/ servidor da conexao do cliente
            System.err.println(this.nomeDoCliente + " conectou ao servidor.");

            menu();

        }catch(EOFException e){
            System.out.println("Conexão encerrada pelo cliente de forma inesperada.");
        } catch (IOException  e) {
            System.err.println("Erro na comunicação com o cliente "+this.nomeDoCliente);
        } finally {
            try {
                /* FECHANDO STREAMS*/
                output.close();
                input.close();
                cliente.close();
                // log p/ servidor da desconexao do cliente
                System.err.println(this.nomeDoCliente + " fechou a conexão");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void listaTodosOsLivrosPeloTituloEEnviaParaCliente() throws IOException {
        output.writeUTF("=========================\n" +
                "|| Livros Disponíveis: ||\n" +
                "=========================");
        for (int i = 0; i < livros.size(); i++) {
            output.writeUTF(i + ": " + livros.get(i).getTitulo());
        }
    }

    /*LISTA LIVROS E DA A CARACTERISTICA DO LIVRO ESCOLHIDO PELO CLIENTE*/
    private void listaOsLivrosEExibeDetalhes() throws IOException {
        listaTodosOsLivrosPeloTituloEEnviaParaCliente();

        output.writeUTF("Escolha um livro para ver os detalhes: ");
        output.flush();

        /* RECEBE O INDICE ESCOLHIDO PELO CLIENTE E DETALHA O LIVRO*/
        while (true) {
            int indiceEscolhido;
            try{
                indiceEscolhido = Integer.parseInt(input.readUTF());
            }catch (NumberFormatException e){
                output.writeUTF("Entrada inválida. Por favor, insira um dos índices listados.");
                output.flush();
                continue;
            }
//            indiceEscolhido = Integer.parseInt(input.readUTF());
            if(indiceEscolhido>= 0 && indiceEscolhido < livros.size()){
                Livro livroEscolhido = livros.get(indiceEscolhido);
                output.writeUTF("Detalhes do livro escolhido:\n" + livroEscolhido.toString());
                output.flush();
                break;
            }
            output.writeUTF("Índice inválido. Tente novamente.");
            output.flush();
        }
    }

    private void menu() throws IOException {
        output.writeUTF("Menu:\n1. Listar livros\n2. Cadastro de livros\n3. Alugar livros" +
                "\n4. Devolução de livros\n5. Sair");
        output.flush();
        while (true) {
            String escolhaDoCliente = input.readUTF();
            if (escolhaDoCliente.equals("1")) {
                listaOsLivrosEExibeDetalhes();
                voltarParaOMenu();
                break;
            } else if (escolhaDoCliente.equals("2")) {
                cadastrarLivros();
                break;
            } else if (escolhaDoCliente.equals("3")) {
                alugarLivro();
                break;
            } else if (escolhaDoCliente.equals("4")) {
                devolverLivroAlugado();
                break;
            } else if (escolhaDoCliente.equals("5")) {
                output.writeUTF("Conexão do cliente com o servidor será cortada...");
                output.flush();
                break;
            } else {
                output.writeUTF("Opção inválida. Tente novamente!\nMenu:\n1. Listar livros" +
                        "\n2. Cadastro de livros\n3. Alugar livros\n4. Devolução de livros\n5. Sair");
                output.flush();
            }
        }
    }

    /* ESCREVE/SOBRESCREVE AS INFORMAÇÕES DO LIVRO NO ARQUIVO JSON*/
    private void serializaOsLivros(){
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, List<Livro>> map = Map.of("books", livros);
        try (FileWriter writer = new FileWriter(filePath)) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, map);
        }catch (IOException e){
            System.out.println("Nao foi possivel a escrita no arquivo JSON");
            e.printStackTrace();
        }
    }
