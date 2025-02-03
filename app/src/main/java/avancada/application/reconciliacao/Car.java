package avancada.application.reconciliacao;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Car implements Runnable {
    private String nome;
    private int x;
    private int y;
    private int color; // Cor do carro
    private int d;
    private int penalty; // Penalidade por colisões
    private int direction; // Frente do carro
    private boolean firstMove; // Para garantir o primeiro movimento para a direita
    private Map<Integer, Integer> sensor; // Mapa de sensores
    private int distance;
    private int laps; // Número de voltas
    private static final int TRACK_COLOR = Color.WHITE; // Cor da pista
    // Semáforo para controlar o acesso à região crítica
    private static final Semaphore semaphore = new Semaphore(1);
    private volatile boolean isRunning = true; // Variável para controlar a execução

    // Controle de tempo e atraso
    private long startTime; // Tempo inicial do movimento

    private int dist_porcent;

    Rec rec = new Rec();

    Random random = new Random();

    // Coordenadas dos sensores
    private final int[] sensorX = {182, 421, 642, 820, 974, 1117};
    private final int[] sensorY = {221, 348, 482, 587};

    private final int[] sensorXr2 = {182, 305, 455, 513, 660, 892, 1117};
    private final int[] sensorYr2 = {211, 353, 483};

    private List<Double> tempos = new ArrayList<>();

    // Lista para armazenar os resultados
    private List<Double> resultados = new ArrayList<>();
    private List<Double> valoresTempos = new ArrayList<>();

    private double[] reconciliacao = new double[11];

    DecimalFormat df = new DecimalFormat("#.####");


    public Car(String nome, int x, int y, int color, int d) {
        this.nome = nome;
        this.x = x;
        this.y = y;
        this.color = color;
        this.d = d; // Inicializa o raio do sensor
        this.distance = 0; // Inicializa a distância com 0
        this.direction = 3; // Iniciar apontando para a direita
        this.penalty = 0; // Inicializa o contador de penalidade
        this.laps = -1; // Inicializa o número de voltas
        this.sensor = new HashMap<>(); // Inicializa o mapa de sensores
        // Preenche o mapa de sensores com valores iniciais (distância máxima d)
        for (int i = 0; i < 8; i++) {
            sensor.put(i, d);
        }

        this.startTime = System.currentTimeMillis(); // Marca o tempo inicial
    }

    public int getDist_porcent() {
        return dist_porcent;
    }

    public void setDist_porcent(int dist_porcent) {
        this.dist_porcent = dist_porcent;
    }

    public String getNome() {
        return nome;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getColor() {
        return color;
    }

    public int getDistance() {
        return distance;
    }

    public int getPenalty() {
        return penalty;
    }

    public int getLaps() {
        return laps;
    }

    public void setLaps(int laps) {
        this.laps = laps;
    }

    public Map<Integer, Integer> getSensor() {
        return sensor;
    }

    public int getD() {
        return d;
    }

    public int getDirection() {
        return direction;
    }

    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void setSensor(Map<Integer, Integer> sensor) {
        this.sensor = sensor;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public Map<Integer, Integer> getSensorData() {
        return sensor;
    }

    public boolean isFirstMove() {
        return firstMove;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void updateDistance() {
        this.dist_porcent = (distance / 2885) * 100; // Progresso em %
    }

    public List<Double> getTempos() {
        return tempos;
    }

    public List<Double> getValoresTempos() {
        return valoresTempos;
    }

    public void setValoresTempos(List<Double> valoresTempos) {
        this.valoresTempos = valoresTempos;
    }

    // Método para atualizar os sensores a cada movimento
    public synchronized void updateSensors(Bitmap mutableBitmap) {
        // Atualiza os sensores para cada uma das 8 direções
        for (int dir = 0; dir < 8; dir++) {
            int xTemp = x;
            int yTemp = y;
            double euclideanDistance = 0;

            // Calcula a distância até o próximo obstáculo
            while (euclideanDistance < d) {
                switch (dir) {
                    case 0:
                        yTemp--;
                        break; // Cima
                    case 1:
                        yTemp++;
                        break; // Baixo
                    case 2:
                        xTemp--;
                        break; // Esquerda
                    case 3:
                        xTemp++;
                        break; // Direita
                    case 4:
                        xTemp--;
                        yTemp--;
                        break; // Diagonal superior esquerda
                    case 5:
                        xTemp++;
                        yTemp--;
                        break; // Diagonal superior direita
                    case 6:
                        xTemp--;
                        yTemp++;
                        break; // Diagonal inferior esquerda
                    case 7:
                        xTemp++;
                        yTemp++;
                        break; // Diagonal inferior direita
                }

                // Verifica limites da pista
                if (xTemp < 0 || xTemp >= mutableBitmap.getWidth() || yTemp < 0 || yTemp >= mutableBitmap.getHeight()) {
                    euclideanDistance = d;  // Caso ultrapasse os limites, marca como distância máxima
                    break;
                }
                // Detecta obstáculos (cor da pista diferente de TRACK_COLOR)
                else if (mutableBitmap.getPixel(xTemp, yTemp) != TRACK_COLOR) {
                    euclideanDistance = Math.sqrt(Math.pow(xTemp - x, 2) + Math.pow(yTemp - y, 2));  // Calcula a distância até o obstáculo
                    break;
                }

                euclideanDistance = Math.sqrt(Math.pow(xTemp - x, 2) + Math.pow(yTemp - y, 2));  // Distância até o ponto
            }

            // Atualiza a distância do sensor para a direção atual
            sensor.put(dir, (int) Math.min(d, euclideanDistance));
        }
    }

    public synchronized void move() {

        long currentTime = System.currentTimeMillis(); // Tempo atual em milissegundos

        long time = currentTime - startTime;
        // Converte o tempo para segundos
        Double elapsedTimeSeconds = time / 1000.0;

        /*if(x == sensorX[0]){
            tempos.add(elapsedTimeSeconds);
        } else if (y == sensorY[0]) {
            tempos.add(elapsedTimeSeconds);
        } else if (y == sensorY[1]) {
            tempos.add(elapsedTimeSeconds);
        } else if (y == sensorY[2]) {
            tempos.add(elapsedTimeSeconds);
        } else if (y == sensorY[3]) {
            tempos.add(elapsedTimeSeconds);
        } else if (x == sensorX[1]) {
            tempos.add(elapsedTimeSeconds);
        } else if (x == sensorX[2]) {
            tempos.add(elapsedTimeSeconds);
        } else if (x == sensorX[3]) {
            tempos.add(elapsedTimeSeconds);
        } else if (x == sensorX[4]) {
            tempos.add(elapsedTimeSeconds);
        } else if (x == sensorX[5]) {
            tempos.add(elapsedTimeSeconds);
        }*/

        //Sensores para a rota 2
        if(x == sensorXr2[0]){
            tempos.add(elapsedTimeSeconds);
        } else if (x == sensorXr2[1]) {
            tempos.add(elapsedTimeSeconds);
        } else if (x == sensorXr2[2]) {
            tempos.add(elapsedTimeSeconds);
        } else if (y == sensorYr2[0]) {
            tempos.add(elapsedTimeSeconds);
        } else if (x == sensorXr2[3]) {
            tempos.add(elapsedTimeSeconds);
        } else if (y == sensorYr2[1]) {
            tempos.add(elapsedTimeSeconds);
        } else if (y == sensorYr2[2]) {
            tempos.add(elapsedTimeSeconds);
        } else if (x == sensorXr2[4]) {
            tempos.add(elapsedTimeSeconds);
        } else if (x == sensorXr2[5]) {
            tempos.add(elapsedTimeSeconds);
        } else if (x == sensorXr2[6]) {
            tempos.add(elapsedTimeSeconds);
        }

        // Verifica se o carro já atingiu a posição para parar
        if (x == 1117) {
            for (int i = 0; i < tempos.size(); i++) {
                System.out.println("tempo " + i +":"  + tempos.get(i));
                valoresTempos.add(tempos.get(i));
            }

            // Adiciona o primeiro elemento da lista original como está
            resultados.add(tempos.get(0));

            // Calcula as diferenças para as demais posições
            for (int i = 1; i < tempos.size(); i++) {
                double diferenca = tempos.get(i) - tempos.get(i - 1);
                resultados.add(diferenca);
            }

            // Exibe os resultados
            for (int i = 0; i < resultados.size(); i++) {
                System.out.printf("Resultado posição %d: %.3f\n", i, resultados.get(i));
                reconciliacao[i] = resultados.get(i);
            }

            reconciliacao[10] = 25.0;

            rec.recDados(reconciliacao);

            isRunning = false; // Atualiza o indicador para parar o loop
            // O carro chega à posição final e não se move mais
            System.out.println("O carro parou em x = " + x);
            tempos.clear();
            resultados.clear();
            return; // Interrompe o movimento
        }

        // Verifica se é o primeiro movimento
        if (firstMove) {
            x++;  // Movimenta para a direita inicialmente
            firstMove = false;
        } else {
            // Detecta se a próxima direção precisa ser uma curva
            int selectedDirection = direction;

            // Verifica se há um bloqueio à frente
            if (sensor.get(direction) < d) {
                // Se o carro está indo para a direita (direção 3), e há um bloqueio à frente, a curva será para baixo (direção 1)
                if (direction == 3) {
                    if (sensor.get(1) > 0) {  // Se há espaço abaixo, vira para baixo
                        selectedDirection = 1;  // Curva para baixo
                    }
                }
                // Se o carro está indo para baixo (direção 1), e há um bloqueio à frente, a curva será para a direita (direção 3)
                else if (direction == 1) {
                    if (sensor.get(3) > 0) {  // Se há espaço à direita, vira para a direita
                        selectedDirection = 3;  // Curva para a direita
                    }
                }
                // Outras curvas podem ser adicionadas conforme a pista
            } else {
                // Caso não haja bloqueio, o carro segue em frente ou escolhe uma direção livre
                int[] forwardDirections = getForwardDirections(direction);
                selectedDirection = selectDirectionWithMostSpace(forwardDirections);
            }

            // Move o carro na direção selecionada
            moveCarInDirection(selectedDirection);

            // Atualiza a direção do carro
            direction = selectedDirection;
        }

        // Incrementa a distância e redesenha o carro
        distance++;
        updateDistance();
    }

    // Método para obter as direções possíveis com base na direção do carro
    private int[] getForwardDirections(int currentDirection) {
        switch (currentDirection) {
            case 0:
                return new int[]{0, 4, 5}; // Cima
            case 1:
                return new int[]{1, 6, 7}; // Baixo
            case 2:
                return new int[]{2, 4, 6}; // Esquerda
            case 3:
                return new int[]{3, 5, 7}; // Direita
            case 4:
                return new int[]{4, 0, 2}; // Diagonal superior esquerda
            case 5:
                return new int[]{5, 0, 3}; // Diagonal superior direita
            case 6:
                return new int[]{6, 1, 2}; // Diagonal inferior esquerda
            case 7:
                return new int[]{7, 1, 3}; // Diagonal inferior direita
            default:
                return new int[]{3}; // Direita padrão
        }
    }

    // Método para selecionar a direção com mais espaço livre baseado nos sensores
    private int selectDirectionWithMostSpace(int[] forwardDirections) {
        int selectedDirection = forwardDirections[0];
        double maxDistance = -1;
        for (int dir : forwardDirections) {
            double sensorValue = sensor.get(dir);
            if (sensorValue > maxDistance) {
                maxDistance = sensorValue;
                selectedDirection = dir;
            }
        }
        return selectedDirection;
    }

    // Método para mover o carro na direção selecionada
    private void moveCarInDirection(int selectedDirection) {
        switch (selectedDirection) {
            case 0:
                y--;
                break;  // Cima
            case 1:
                y++;
                break;  // Baixo
            case 2:
                x--;
                break;  // Esquerda
            case 3:
                x++;
                break;  // Direita
            case 4:
                x--;
                y--;
                break;  // Diagonal superior esquerda
            case 5:
                x++;
                y--;
                break;  // Diagonal superior direita
            case 6:
                x--;
                y++;
                break;  // Diagonal inferior esquerda
            case 7:
                x++;
                y++;
                break;  // Diagonal inferior direita
        }
    }


    // Método para verificar se o carro está na região do semáforo
    private boolean isInTrafficLightRegion() {
        // Verifica se o valor de y é igual a 303 ou 437
        return (y == 303 || y == 437);
    }

    @Override
    public void run() {
        // Verifica se o carro está se movendo
        while (isRunning) {

            try {

                // Verifica se o carro está entrando na região crítica (semáforo)
                if (isInTrafficLightRegion()) {
                    // Adquire o semáforo para garantir que apenas um carro entre na região
                    semaphore.acquire();

                    // Atraso aleatório entre 100 e 1000 milissegundos enquanto o carro espera na região crítica
                    int waitTime = 100 + random.nextInt(901);
                    System.out.println(nome + " está esperando por " + waitTime + "ms.");
                    Thread.sleep(waitTime);  // Espera o tempo aleatório

                    // Libera o semáforo ao sair da região crítica
                    semaphore.release();
                    System.out.println(nome + " saiu da região crítica.");
                }

                // Caso não esteja na região crítica, move normalmente
                move();

                // Atraso padrão entre os movimentos para o carro não se mover muito rápido
                Thread.sleep(10);

            } catch (InterruptedException e) {
                e.printStackTrace();
                isRunning = false;
            }
        }
    }


    // Método para parar a execução do carro
    public void stopRunning() {
        isRunning = false;
    }
}
