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

     /* CONVERTENDO O JSON EM UM OBJETO MAP - LÊ AS INFORMAÇÕES DO ARQUIVO JSON */
    private void desserializaOsLivros(StringBuilder jsonContent) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, List<Livro>> map = objectMapper.readValue(jsonContent.toString(), new TypeReference<Map<String, List<Livro>>>() {});

        /* 'books' é a chave do objeto map que mapeia uma lista de objeto livros(fornecidos no JSON) */
        livros = map.get("books");
    }


    private void cadastrarNovoLivro() throws IOException {
        output.writeUTF("Para cadastrar um novo livro é preciso informar:");
        output.writeUTF("	- Título: ");
        output.flush();
        String titulo = input.readUTF();

        output.writeUTF("	- Autor: ");
        output.flush();
        String autor = input.readUTF();

        output.writeUTF("	- Gênero: ");
        output.flush();
        String genero = input.readUTF();

        output.writeUTF("	- Número de exemplares: ");
        output.flush();
        int numeroDeExemplares = Integer.parseInt(input.readUTF());

        /* CADASTRA E ADD À LISTA DE LIVROS */
        Livro novoLivro = new Livro(titulo, autor, genero, numeroDeExemplares);
        livros.add(novoLivro);

        serializaOsLivros();

        output.writeUTF("Livro cadastrado com sucesso!");
        output.flush();
    }

    private void incrementarExemplaresDeLivroNoEstoque() throws IOException {
        listaTodosOsLivrosPeloTituloEEnviaParaCliente();
        output.writeUTF("Escolha um dos livros disponiveis para incrementar os exemplares: ");
        output.flush();

        while (true) {
            String indiceEscolhidoEmString = input.readUTF();
            int indiceEscolhido = Integer.parseInt(indiceEscolhidoEmString);

            if (indiceEscolhido >= 0 && indiceEscolhido < livros.size()){
                Livro livroEscolhido = livros.get(indiceEscolhido);
                output.writeUTF("Digite o número de exemplares a adicionar:");
                output.flush();

                String exemplaresAdicionaisEmString = input.readUTF();
                int exemplaresAdicionais = Integer.parseInt(exemplaresAdicionaisEmString);

                /*INCREMENTA MAIS ESTOQUE NO LIVRO SELECIONADO*/
                livroEscolhido.setNumeroDeExemplares(livroEscolhido.getNumeroDeExemplares() + exemplaresAdicionais);

                serializaOsLivros();

                output.writeUTF("Exemplares incrementados com sucesso!");
                output.flush();
                break;
            }
            output.writeUTF("Indice inválido. Tente novamente!");
            output.flush();
        }
    }

    /*CADASTRA NOVOS LIVROS OU INCREMENTA EXEMPLARES(CASO JÁ TENHA O LIVRO CADASTRADO)...VOLTA AO MENU*/
    private void cadastrarLivros()throws IOException{
        output.writeUTF("Deseja cadastrar um:\n1: Novo livro\n2: Livro em estoque");
        output.flush();
        while (true){
            String EscolheQualLivroCadastrar = input.readUTF();
            if(EscolheQualLivroCadastrar.equals("1")){
                cadastrarNovoLivro();
                voltarParaOMenu();
                break;
            } else if (EscolheQualLivroCadastrar.equals("2")) {
                incrementarExemplaresDeLivroNoEstoque();
                voltarParaOMenu();
                break;
            }else{
                output.writeUTF("Opção inválida. Tente novamente!\n1: Novo livro" +
                        "\n2: Livro em estoque");
                output.flush();
            }
        }
    }

    private List<Livro> alugarLivro() throws IOException {
        listaTodosOsLivrosPeloTituloEEnviaParaCliente();

        output.writeUTF("Escolha qual dos livros deseja alugar: ");
        output.flush();

        List<Livro> livrosAlugados = livrosAlugadosPorCliente.get(cliente);

        while (true) {
            int indiceDoLivroEscolhidoParaAlugar = Integer.parseInt(input.readUTF());
            if(indiceDoLivroEscolhidoParaAlugar>=0 && indiceDoLivroEscolhidoParaAlugar < livros.size()){
                Livro livroEscolhido = livros.get(indiceDoLivroEscolhidoParaAlugar);

                if(livroEscolhido.getNumeroDeExemplares()>0){
                    livroEscolhido.setNumeroDeExemplares(livroEscolhido.getNumeroDeExemplares() -1);

                    serializaOsLivros();

                    // Adiciona o livro à lista de livros alugados pelo cliente
                    livrosAlugados.add(livroEscolhido);

                    output.writeUTF("Livro alugado com sucesso!");
                    output.flush();
                    desejaAlugarOutroLivro();
                    break;
                }else{
                    output.writeUTF("Atualmente todos os exemplares deste livro estão alugados.");
                    output.flush();
                    desejaAlugarOutroLivro();
                    String opcaoDeSairDaAbaAlugarLivro = input.readUTF();
                    if(opcaoDeSairDaAbaAlugarLivro.equalsIgnoreCase("S")){
                        alugarLivro();
                    }else{
                        voltarParaOMenu();
                    }
                }
            }else{
                output.writeUTF("Opção inválida. Tente novamente!");
                output.flush();
            }
        }
        return livrosAlugados;
    }

    /* PODE ALUGAR OUTRO LIVRO, VOLTAR PARA O MENU OU SAIR*/
    private void desejaAlugarOutroLivro()throws IOException{
        output.writeUTF("Deseja alugar outro livro? (S/N)");
        output.flush();
        String opcaoDeSairDaAbaAlugarLivro = input.readUTF();
        if(opcaoDeSairDaAbaAlugarLivro.equalsIgnoreCase("S")){
            alugarLivro();
        }else{
            voltarParaOMenu();
        }
    }
    private void devolverLivroAlugado() throws IOException {
        List<Livro> livrosAlugados = livrosAlugadosPorCliente.get(cliente);

        if (livrosAlugados.isEmpty()) {
            output.writeUTF("Você não tem livros alugados.");
            output.flush();
            voltarParaOMenu();
        }else{
            for (int i = 0; i < livrosAlugados.size(); i++) {
                output.writeUTF(i + ": " + livrosAlugados.get(i).getTitulo());

            }

            output.writeUTF("Escolha qual livro gostaria de devolver: ");
            output.flush();

            while(true){
                int indiceDoLivroDevolvido;
                try{
                    indiceDoLivroDevolvido = Integer.parseInt(input.readUTF());
                }catch (NumberFormatException e){
                    output.writeUTF("Entrada inválida. Por favor, insira um dos índices listados.");
                    output.flush();
                    continue;
                }

                if(indiceDoLivroDevolvido>=0 && indiceDoLivroDevolvido < livrosAlugados.size()){

                    Livro livroDevolvido = livrosAlugados.remove(indiceDoLivroDevolvido);
                    livroDevolvido.setNumeroDeExemplares(livroDevolvido.getNumeroDeExemplares() + 1);

                    serializaOsLivros();

                    output.writeUTF("Livro devolvido com sucesso!");
                    output.flush();
                    desejaDevolverOutroLivro();
                    break;
                }
                output.writeUTF("Indice inválido. Tente novamente!");
                output.flush();
            }
        }
    }

    /* PODE DEVOLVER OUTRO LIVRO, VOLTAR PARA O MENU OU SAIR */
    private void desejaDevolverOutroLivro()throws IOException{
        output.writeUTF("Deseja devolver outro livro? (S/N)");
        output.flush();
        String opcaoDeSairDaAbaDevolverLivro = input.readUTF();
        if(opcaoDeSairDaAbaDevolverLivro.equalsIgnoreCase("S")){
            devolverLivroAlugado();
        }else{
            voltarParaOMenu();
        }
    }

    /*VOLTA AO MENU OU SAI*/
    public  void voltarParaOMenu(){
        try{
            output.writeUTF("\nDeseja voltar ao menu?(S/N)");
            output.flush();
            while (true){
                String escolheOpcoes = input.readUTF();
                if(escolheOpcoes.equalsIgnoreCase("S") || escolheOpcoes.equalsIgnoreCase("SIM")){
                    menu();
                } else if (escolheOpcoes.equalsIgnoreCase("N") || escolheOpcoes.equalsIgnoreCase("NAO")) {
                    output.writeUTF("Conexão do cliente com o servidor será cortada...");
                    output.flush();
                    break;
                }else{
                    output.writeUTF("Entrada inválida. Tente novamente! (S/N)");
                    output.flush();
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
