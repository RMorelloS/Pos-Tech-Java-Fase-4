# Pós Tech - Fase 4

Para a entrega do 4º tech challenge, foi desenvolvido um site para upload de vídeos.

Foram desenvolvidas as seguintes funcionalidades:

1. Cadastro de usuário
2. Upload de vídeos
3. Listagem e visualização de vídeos de outros usuários
4. Vídeos recomendados
5. Estatísticas da conta
6. Testes unitário e de integração, com cobertura de 91% do código
   
Foram utilizadas as seguintes tecnologias/técnicas:
 * **Java + Spring** - recebimento e processamento de requisições
 * **Banco de dados NoSQL DynamoDB** - armazenamento das informações gerais de cada vídeo/usuário, incluindo o link para o vídeo armazenado no S3
 * **Storage S3** - armazenamento dos arquivos mp4 de cada vídeo
 * **HTML/CSS/JS/Thymeleaf/Bootstrap** - front para visualização das telas/vídeos
 * **JUnit** - testes das funcionalidades
 * **Spring Security** - autenticação e permissionamento

A arquitetura do sistema é apresentada no fluxo a seguir:

![DiagramaFase4](https://github.com/RMorelloS/fase4/assets/32580031/3aae40e9-63f7-42a5-a585-22a0f84fbdf7)

# Funcionalidades

## Cadastro de usuário

Na tela inicial do sistema, selecionar a opção "Cadastre-se"

![image](https://github.com/RMorelloS/fase4/assets/32580031/7b5194f7-bac3-4c62-baca-18eefd7748f5)

Inserir um usuário, senha e e-mail e selecionar a opção "Criar conta"

![image](https://github.com/RMorelloS/fase4/assets/32580031/fb022278-5f93-4970-9b8d-64fb9d4b935b)

## Upload de vídeos

Realizar login na plataforma com o usuário criado previamente:

![image](https://github.com/RMorelloS/fase4/assets/32580031/5d06e850-d489-40af-a429-34f7a9df6200)

Na tela de listagem de vídeos, selecionar a opção "Carregar vídeos" no menu superior:

![image](https://github.com/RMorelloS/fase4/assets/32580031/70621ebf-09d8-419c-a7e4-e899b59dc096)

Inserir as informações sobre o vídeo. É possível adicionar quantas categorias forem desejadas, escrevendo cada categoria e selecionando o opção "Adicionar". As categorias são exibidas numa lista sobre o botão de Submit.

![image](https://github.com/RMorelloS/fase4/assets/32580031/72c5a36f-bb5e-4363-9a44-d278e1c56199)

Uma vez carregado o vídeo, ele estará disponível para outros usuários interagirem.

![image](https://github.com/RMorelloS/fase4/assets/32580031/7180c615-fcb5-4b56-a474-47c0c2004fc1)



