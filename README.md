# Pós Tech - Fase 4

Para a entrega do 4º tech challenge, foi desenvolvido um site para upload de vídeos.

Foram desenvolvidas as seguintes funcionalidades:

1. Cadastro de usuário
2. Upload de vídeos
3. Listagem e visualização de vídeos
4. Vídeos recomendados
5. Estatísticas da conta
6. Testes unitário e de integração, com cobertura de 90% do código
   
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

## Listagem e visualização de vídeos

Na tela listar vídeos, é possível visualizar todos os vídeos carregados, seja pelo usuário logado ou pelos demais usuários da plataforma:

![image](https://github.com/RMorelloS/fase4/assets/32580031/dd40bc52-a3e1-4320-9406-d0c2a1fb22f9)

É possível filtrar os vídeos por título, categoria ou data de publicação:

Filtro por título:
![image](https://github.com/RMorelloS/fase4/assets/32580031/76523931-bf21-4eca-a5ec-d1e95f389e0f)

Filtro por data de publicação:
![image](https://github.com/RMorelloS/fase4/assets/32580031/a7e9dc2d-065c-4182-8803-92a0a70cb149)

É possível favoritar/desfavoritar os vídeos, que aparecerão com o botão "Favoritar" em amarelo caso estejam favoritados:

![image](https://github.com/RMorelloS/fase4/assets/32580031/700fee13-ca2d-4c70-963c-0198a3194185)

Por fim, é possível visualizar o vídeo, que será carregado a partir do S3, selecionando-se a opção "assistir":
![image](https://github.com/RMorelloS/fase4/assets/32580031/ba33f772-8c62-4103-9e44-8b85441ec070)

## Vídeos recomendados

No menu superior, é possível ter acesso aos vídeos recomendados:

![image](https://github.com/RMorelloS/fase4/assets/32580031/ae8050d5-141a-4137-ba67-6e3718945959)

Os vídeos recomendados são gerados a partir dos vídeos que o usuário favoritou. A lógica baseia-se em obter as duas categorias que o usuário mais favoritou em todos os vídeos, e obter todos os vídeos que possuem aquelas categorias.

No exemplo abaixo, o usuário favoritou as categorias "Cavalos" e "Praia" duas vezes:

![image](https://github.com/RMorelloS/fase4/assets/32580031/810ffa55-c5a5-414a-8eeb-86401a43dc3c)

Portanto, os vídeos recomendados incluirão qualquer vídeo que se enquadre nessas duas categorias:

![image](https://github.com/RMorelloS/fase4/assets/32580031/de67374e-3a2f-472f-ae02-d13a3eee1e2e)

## Estatísticas da conta

No menu superior, é possível ter acesso às estatísticas da conta:

![image](https://github.com/RMorelloS/fase4/assets/32580031/6f672443-9763-4f46-8fa4-c7ef7f800d7f)

Nela, é possível ver todos os vídeos que o usuário carregou, bem como quantidade de usuários que favoritaram e visualizações (cada click de qualquer usuário gera uma visualização)

![image](https://github.com/RMorelloS/fase4/assets/32580031/814b26ee-870c-41fb-97be-77d0f67fcf8b)

Nesta tela, também é possível deletar os vídeos carregados.

## Testes unitário e de integração, com cobertura de 90% do código

Por fim, foram desenvolvidos 66 testes, dentre testes unitários e de integração, para garantir as funcionalidades desenvolvidas:

![image](https://github.com/RMorelloS/fase4/assets/32580031/d3c49b50-d056-4780-a3ed-3deb87dfac37)

Foram desenvolvidos testes para controlador, service e repository:

![image](https://github.com/RMorelloS/fase4/assets/32580031/83178317-dce2-4db8-b299-4071c40fda44)

Ao fim, atingiu-se a cobertura de 90% do código:

![image](https://github.com/RMorelloS/fase4/assets/32580031/85795a9d-0edf-49ed-88a2-222f4d4db294)

