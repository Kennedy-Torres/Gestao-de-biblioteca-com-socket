# Projeto : Gestão de uma biblioteca utilizando sockets

## Como rodar o projeto?
1. Faça a importação dos arquivos para um projeto MAVEN.
2. Inicialize a classe do servidor e posteriormente a classe do cliente.

## Recursos utilizados no projeto:
1. Linguagem de compilação da JVM > 1.8 no mín. Neste projeto foi utilizado a versão 17
2. projeto gerado em maven
3. Uso da biblioteca jackson com as depêndencias:
   3.1 - Databind para serialização e desserialização
   3.2 - Annotations utilizado nos getters da classe Livros para garantir que os nomes das propriedades nos dados JSON correspondam aos nomes dos atributos da classe.
   OBS: Essas depêndencias estão adicionadas no arquivo pom.xml gerado pelo MAVEN.

## Breve Contexto Sobre o Código

### Visão Geral

Basicamente, temos um servidor que consegue se conectar com n clientes. Para cada cliente conectado, um objeto `GerenciadorDeCliente` é instanciado, sendo este um intermediador entre o cliente e o servidor. A classe `GerenciadorDeCliente` possui todo o protocolo do servidor, ou seja, nela encontram-se as funcionalidades que o servidor disponibiliza para o cliente.

### Classe Cliente

A classe do `Cliente`, quando conectada ao servidor, é responsável apenas por enviar e receber mensagens do servidor de forma paralela.

### Classe Livro

A classe `Livro` tem o papel de definir, armazenar e listar os atributos dos livros. Através desses dados, fazemos uma conexão dos objetos Java com o arquivo JSON para realizar a serialização e desserialização.

### Conclusão

O servidor, através de um gerenciador, disponibiliza funcionalidades para o cliente interagir. Para cada informação alterada pelo cliente nos valores de livros, o estado é salvo/atualizado no arquivo JSON.



## Autores

| [<img src="https://avatars.githubusercontent.com/u/128331199?v=4" width=115><br><sub>Kennedy Torres</sub>](https://github.com/Kennedy-Torres) |[<img src="https://avatars.githubusercontent.com/u/111468790?v=4" width=115><br><sub>Vinícius Leão</sub>](https://github.com/Viniciusleao99) |[<img src="https://avatars.githubusercontent.com/u/111469440?v=4" width=115><br><sub>Leonardo Ribeiro</sub>](https://github.com/Leoribeiro61) |[<img src="https://avatars.githubusercontent.com/u/158603640?v=4" width=115><br><sub>Nickolas Carvalho</sub>](https://github.com/Nickolaaas) |[<img src="https://avatars.githubusercontent.com/u/98848966?v=4" width=115><br><sub>Bruno Alves</sub>](https://github.com/motherlode777) |
| :---: | :---: | :---: | :---: | :---: |
