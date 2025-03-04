# Aplicativo de Corrida com Reconciliação de Dados

## Descrição
Este projeto é um aplicativo de corrida desenvolvido com base no repositório [Aplicativo Corrida](https://github.com/sergioperess/aplicativo_corrida). Ele combina a simulação de corridas autônomas com a funcionalidade de reconciliação de dados, permitindo analisar e corrigir inconsistências geradas durante as simulações.

## Funcionalidades Principais

### Corrida Autônoma
- **Movimentação dos Carros**: Cada carro se move automaticamente, controlado por threads ou Runnables.
- **Sensoriamento da Pista**: A pista possui 10 sensores para a monitorar os dados de tempo entre os pontos.
- **Reconciliação de Dados**: Os tempos de passagem dos carros pelos sensores são reconsiliados para um tempo total esperado.
- **Gestão de Região Crítica**: Os carros respeitam regiões críticas delimitadas, simulando semáforos e atrasos nos tempos.

### Reconciliação de Dados
- **Correção de Inconsistências**: Identifica e corrige dados incorretos ou conflitantes gerados durante as simulações.
- **Relatórios de Reconciliação**: Gera relatórios detalhados sobre as correções realizadas.

## Tecnologias Utilizadas
- **Linguagem de Programação**: Java.
- **Plataforma**: Android Studio.

## Estrutura do Projeto
1. **MainActivity**: Ponto de entrada do aplicativo, gerencia a interface do usuário e interação com o backend.
2. **Classe Car**: Define as propriedades e o comportamento dos carros na corrida.
3. **Handler de Movimentação**: Coordena o movimento dos carros de forma assíncrona.
4. **Módulo de Reconciliação**: Contém as lógicas para identificação e correção de inconsistências.

## Configuração e Instalação

1. **Clone o Repositório**:
   ```bash
   git clone https://github.com/sergioperess/aplicativo_corrida.git
   ```

2. **Importe o Projeto**:
    - Abra o Android Studio e importe o repositório clonado.

3. **Execute o Projeto**:
    - Compile e execute o aplicativo em um emulador ou dispositivo físico Android.

## Como Usar
1. Inicie o aplicativo e pressione o botão **Start** para iniciar a corrida.
2. Acompanhe a reconciliação de dados através dos relatórios gerados no aplicativo.

## Contribuição
Contribuições são bem-vindas! Por favor, siga as etapas abaixo:
1. Fork o repositório.
2. Crie uma branch para sua funcionalidade:
   ```bash
   git checkout -b feature/nome-da-funcionalidade
   ```
3. Commit suas alterações:
   ```bash
   git commit -m "Adiciona nova funcionalidade"
   ```
4. Envie suas alterações:
   ```bash
   git push origin feature/nome-da-funcionalidade
   ```
5. Abra um Pull Request no repositório original.

## Licença
Este projeto está licenciado sob a Licença MIT. Consulte o arquivo LICENSE para mais informações.

## Contato
Para quaisquer dúvidas ou sugestões, entre em contato com o desenvolvedor:
- **Sérgio Peres**
- [GitHub Profile](https://github.com/sergioperess)

