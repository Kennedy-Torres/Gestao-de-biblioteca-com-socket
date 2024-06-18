# Projeto : Gestão de uma biblioteca utilizando sockets

## Recursos utilizados no projeto:
1. Linguagem de compilação da JVM > 1.8 no mín. Neste projeto foi utilizado a versão 17
2. projeto gerado em maven
3. Uso da biblioteca jackson com as depêndencias:
   3.1 - Databind para serialização e desserialização
   3.2 - Annotations utilizado nos getters da classe Livros para garantir que os nomes das propriedades nos dados JSON correspondam aos nomes dos atributos da classe.
   OBS: Essas depêndencias estão adicionadas no arquivo pom.xml gerado pelo MAVEN.

## Breve contexto sobre o código:
   Basicamente temos um servidor que consegue conectar com n clientes, para cada cliente conectado um objeto "GerenciadorDeCliente" é instânciado( sendo este um intermediador entre cliente e servidor, a classe gerenciadora possui todo o protocolo do servidor, ou seja, nela encontra-se as funcionalidades no qual o servidor disponibiliza para o cliente).
   Já a classe do cliente, quando conectada ao servidor, é responsável apenas por enviar e receber mensagens do servidor de forma paralela.
   A classe Livros tem o papel de definir, armazenar e listar os atributos dos livros. Através desses dados fizemos uma conexão dos objetos Java com o arquivo JSON para realizar a serialização e desserialização.
    
conclusão: servidor através de um gerenciador disponibiliza funcionalidades para o cliente interagir, para cada informação alterada pelo cliente, nos valores de livros, é salvo/atualizado no arquivo JSON.



## Autores

| [<img src="https://avatars.githubusercontent.com/u/128331199?v=4" width=115><br><sub>Kennedy Torres</sub>](https://github.com/Kennedy-Torres) |[<img src="https://avatars.githubusercontent.com/u/111468790?v=4" width=115><br><sub>Vinícius Leão</sub>](https://github.com/Viniciusleao99) |[<img src="https://avatars.githubusercontent.com/u/111469440?v=4" width=115><br><sub>Leonardo Ribeiro</sub>](https://github.com/Leoribeiro61) |[<img src="https://avatars.githubusercontent.com/u/158603640?v=4" width=115><br><sub>Nickolas Carvalho</sub>](https://github.com/Nickolaaas) |[<img src="https://avatars.githubusercontent.com/u/98848966?v=4" width=115><br><sub>Bruno Alves</sub>](https://github.com/motherlode777) |
| :---: | :---: | :---: | :---: | :---: |
