Este repositório contém uma interface desktop em Java Swing para gerenciar tabelas de um banco MySQL (pessoas, pacientes, profissionais) . O projeto segue a organização em pacotes com.meuprojeto.
e inclui um painel genérico (GenericTablePanel) que monta CRUD dinâmico para tabelas do banco, além de telas de login e principal.

Abaixo há instruções para instalar, configurar, executar e observações importantes sobre o banco de dados e como as tabelas devem estar estruturadas para o aplicativo funcionar corretamente.

**Requisitos**

- Java JDK 11+ (recomendado JDK 17+), JDK 24 (funciona também, desde que compatível com sua IDE).

- MySQL (ex.: MySQL 5.7 / 8.x)

- MySQL JDBC Driver (mysql-connector-java, por ex. mysql-connector-j-8.x.x.jar), adicione ao classpath/projeto (IntelliJ: Project Structure → Libraries).

- IDE: IntelliJ IDEA (ou Eclipse) para rodar e debugar.

 **Configuração do banco MySQL (schema sugerido)**

**Importante**: O app pressupõe que exista uma tabela person com id INT PRIMARY KEY AUTO_INCREMENT. As tabelas que dependem de person (ex.: patient, profissionais) não devem ter AUTO_INCREMENT na coluna id,
 elas usam id como foreign key para person(id).

**Exemplo básico de criação (SQL):**

CREATE DATABASE IF NOT EXISTS hospdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hospdb;

CREATE TABLE person (
  id INT PRIMARY KEY AUTO_INCREMENT,
  nome VARCHAR(200),
  cpf VARCHAR(30),
  data_nasc DATE,
  sexo VARCHAR(10),
  contato VARCHAR(80),
  endereco VARCHAR(300)
);

CREATE TABLE patient (
  id INT PRIMARY KEY, -- NOT AUTO_INCREMENT: será igual ao person.id
  num_pront VARCHAR(80),
  alergias VARCHAR(255),
  observacoes TEXT,
  FOREIGN KEY (id) REFERENCES person(id) ON DELETE CASCADE
);

CREATE TABLE profissionais (
  id INT PRIMARY KEY, -- NOT AUTO_INCREMENT
  matricula VARCHAR(80),
  funcao VARCHAR(120),
  FOREIGN KEY (id) REFERENCES person(id) ON DELETE CASCADE
);

**Configurar JDBC / Database.java**

No projeto há a classe com.meuprojeto.Database. Verifique que as credenciais e URL apontam para seu MySQL:

public class Database {
    private static final String URL = "jdbc:mysql://host:3306/hospdb?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "root";

    static {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); }
        catch (ClassNotFoundException e) { e.printStackTrace(); }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}

 **Como executar**

- Certifique-se de ter criado o schema e as tabelas no MySQL conforme acima.

- Adicione o driver MySQL (mysql-connector-java-8.x.x.jar) nas dependências da sua IDE / projeto.

- Ajuste Database.java com URL, USER e PASS.

- Em IntelliJ: marque com.meuprojeto.ui.LoginDialog (ou TelaPrincipalHosp) como main e run.

- Fluxo recomendado: execute LoginDialog → efetue login → abre TelaPrincipalHosp.


**Credenciais de teste de login**

- Usuário: admin

- Senha: admin

(Se você usou a tabela users e um UserDAOImpl.authenticate() que checa DB, insira esse usuário no banco. Caso a autenticação seja hard-coded no LoginDialog, ele usará admin/admin.)



**Comportamento esperado do app**

- GenericTablePanel tenta ler metadata diretamente pelo DatabaseMetaData.

- Para tabelas filhas de person (pacientes e profissionais), o formulário e a listagem exibem primeiro os campos de person (ex.: nome, cpf, endereço) e depois os campos da tabela filha (matrícula, num_pront etc).

  **Inserção de um paciente/profissional faz:**

- Inserção em person (gera id auto_increment)

- Inserção em patient/profissionais com id igual ao person.id

- Inserções diretas em tabelas filhas sem existir person correspondente -> vai falhar por FK (erro Cannot add or update a child row).

Portanto use o botão Novo no painel correspondente, o código deve criar person e a linha filha em transação.


**Soluções para erros comuns**

**No suitable driver found**

- Adicione mysql-connector ao classpath; carregue driver Class.forName("com.mysql.cj.jdbc.Driver") (já incluído em Database.java).

**Field 'id' doesn't have a default value / Multiple primary key defined**

- Verifique esquema: person.id deve ser AUTO_INCREMENT PRIMARY KEY. patient.id e profissionais.id não devem ser AUTO_INCREMENT.

Altere tabela filha para id INT PRIMARY KEY e adicionar FOREIGN KEY (id) REFERENCES person(id).

**Cannot add or update a child row: a foreign key constraint fails**

- Você está tentando inserir na tabela filha sem existir a linha correspondente em person. O código correto insere em person e recupera generated key, em seguida insere na tabela filha com esse id.

**Data/datetime parsing errors**

- Use formatos consistentes: o formulário aceita datas em yyyy-MM-dd ou dd/MM/yyyy (o código tenta ambos). Ao inserir, verifique o valor antes e normalize.

**java.lang.NullPointerException ao acessar table**

- Certifique-se que o GenericTablePanel foi instanciado com o construtor correto e que getTable() não é chamado antes do painel ter sido inicializado.
- 

  **Próximas melhorias**

- Hash de senhas (BCrypt/Argon2) ao invés de texto plano.

- Validação de formulários (CPF, data, campos obrigatórios).

- Melhor UI/UX: diálogos menores e consistentes (já ajustados no código enviado).

- Adicionar mais tabelas com outras estruturas (Agendamentos, Internações, Prescrição, etc)

- Logs e tratamento de erros mais amigável.

- Separar DAO/Service: o painel hoje faz operações diretas no DB para projeto maior, mover lógica para DAOs e services.
  
**Estrutura de pacotes (resumo)**

src/
└─ com.meuprojeto
   ├─ Database.java           
   ├─ model/                  
   ├─ dao/                    
   ├─ dao/impl/               
   └─ ui/
      ├─ panels/
      │  └─ GenericTablePanel.java
      ├─ LoginDialog.java
      ├─ TelaPrincipalHosp.java
      └─ DynamicTelaPrincipal.java

Ao entrar, as abas/painéis carregam as tabelas e permitem Atualizar, Novo, Editar, Excluir. FOREIGN KEY (paciente_id) REFERENCES patient(id)
);
