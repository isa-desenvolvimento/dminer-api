<h1 align="center">

👾 INTRANET D.MINER 👾

</h1>
<p align="center">🚀  Sistema comunicação para colaboradores internos. 
</p>

### 🏆 Aplicação [Demo](https://dminer.herokuapp.com/)

### Pré-requisitos
```
Antes de começar, você vai precisar ter instalado em sua máquina as seguintes ferramentas:
[Git](https://git-scm.com/), 
[Maven 3.6.3](http://charlesmms.azurewebsites.net/2017/09/04/instalando-maven-no-windows-10/), 
[Java 15](https://mauriciogeneroso.medium.com/configurando-java-4-como-configurar-as-vari%C3%A1veis-java-home-path-e-classpath-no-windows-46040950638f),
[Docker](https://www.docker.com/products/docker-desktop),
[VSCode](https://code.visualstudio.com/) .
```

### Instale as seguintes extensões do vscode
    👉 Language Support for Java(TM) by Red Hat
    👉 Extension Pack for Java
    👉 Spring Initializr Java Support

### Se preferir pode seguir o seguinte tutorial para configurar o vscode
(https://medium.com/@vinicius.b.martinez/microsoft-vscode-para-desenvolvedores-java-f1e9f69e6fa6)


### 🎲 Rodando o Back

# Clone este repositório
```
git git@github.com:isa-desenvolvimento/dminer-api.git
```

# Abra o projeto no vscode
# Execute o maven update, clicando com o botão direito do mouse no arquivo pom.xml e selecionando a opção "Update Project"
# Abra o item [MAVEN] no painel esquerdo e clique com o botão direito sobre o projeto [dminer] e selecione a opção "install"
![alt text](/resources-readme/maven.jpg) .

### Configurando o application.properties

# configure o caminho da variável [diretorio-uploads] com a pasta onde será salvo os arquivos de uploads pertinentes ao projeto
![alt text](/resources-readme/diretorio.jpg)

# configure a conexão com o banco de dados conforme sua necessidade e perfil de projeto (prod, test, default)
# na imagem a seguir um exemplo de conexão com o banco sql server
![alt text](/resources-readme/diretorio.jpg)


### Iniciando o projeto

# Inicie o projeto na aba [SPRING BOOT DASHBOARD]
![alt text](/resources-readme/spring-start.jpg)

# O servidor inciará na porta:8081 - acesse <http://localhost:8081/api/swagger-ui.html>


### 🛠 Tecnologias

As seguintes ferramentas foram usadas na construção do projeto:

- [Java](https://www.oracle.com/java/technologies/javase/jdk15-archive-downloads.html)
- [Maven](https://maven.apache.org/docs/3.6.3/release-notes.html) 
- [Docker](https://www.docker.com/products/docker-desktop)
- [Spring Boot](https://spring.io/projects/spring-boot)