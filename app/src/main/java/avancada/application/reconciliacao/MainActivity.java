package avancada.application.reconciliacao;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private Bitmap bitmap, mutableBitmap;
    private Canvas canvas;
    private List<Car> carList = new ArrayList<>();
    private Handler handler;
    private Runnable runnable;
    private static final int TRACK_COLOR = Color.WHITE; // Cor da pista

    private double[] tempos = new double[10];

    private List<Double> valores = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        imageView = findViewById(R.id.myImageView);
        Button botaoStart = findViewById(R.id.botaoStart);

        // Carregar a imagem da pasta drawable como Bitmap
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rota2);
        mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(mutableBitmap);

        // Configurar o comportamento do botão Start
        botaoStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createCars(); // Criar os carros

                // Iniciar a movimentação de cada carro em uma thread separada
                for (int i = 0; i < carList.size(); i++) {
                    Car car = carList.get(i);
                    Thread carThread = new Thread(car); // Cria uma thread para cada carro

                    carThread.start(); // Inicia a thread
                }

                startMovement(); // Iniciar a movimentação
            }
        });

    }

    private void startMovement() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                moveCars(); // Mover os carros
                handler.postDelayed(this, 20); // Executar a cada 20ms
            }
        };
        handler.post(runnable); // Inicia o loop
    }


    // Método para mover todos os carros
    @SuppressLint("DefaultLocale")
    private synchronized void moveCars() {
        // Limpa o Bitmap antes de desenhar os carros
        mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true); // Restaura a imagem da pista
        canvas = new Canvas(mutableBitmap); // Cria um novo canvas

        resetTempos();

        for (Car car : carList) {

            Paint paint = new Paint();
            paint.setColor(TRACK_COLOR);
            canvas.drawCircle(car.getX(), car.getY(), 10, paint); // Limpa a posição anterior

            car.updateSensors(mutableBitmap); // Atualiza os sensores

            // Verifica colisão
            if (car.getX() < 0 || car.getX() >= mutableBitmap.getWidth() || car.getY() < 0 || car.getY() >= mutableBitmap.getHeight() ||
                    mutableBitmap.getPixel(car.getX(), car.getY()) != TRACK_COLOR) {
                car.setPenalty(car.getPenalty() + 1);
            }

            if (car.getX() == 1117){
                valores = car.getValoresTempos();

                for (int i = 0; i < valores.size(); i++) {
                    tempos[i] = valores.get(i);
                }

                //System.out.println(Arrays.toString(tempos));

                TextView time1 = findViewById(R.id.time1);
                TextView time2 = findViewById(R.id.time2);
                TextView time3 = findViewById(R.id.time3);
                TextView time4 = findViewById(R.id.time4);
                TextView time5 = findViewById(R.id.time5);
                TextView time6 = findViewById(R.id.time6);
                TextView time7 = findViewById(R.id.time7);
                TextView time8 = findViewById(R.id.time8);
                TextView time9 = findViewById(R.id.time9);
                TextView time10 = findViewById(R.id.time10);

                time1.setText(String.format("Tempo1: %.3f s", tempos[0]));
                time2.setText(String.format("Tempo2: %.3f s", tempos[1]));
                time3.setText(String.format("Tempo3: %.3f s", tempos[2]));
                time4.setText(String.format("Tempo4: %.3f s", tempos[3]));
                time5.setText(String.format("Tempo5: %.3f s", tempos[4]));
                time6.setText(String.format("Tempo6: %.3f s", tempos[5]));
                time7.setText(String.format("Tempo7: %.3f s", tempos[6]));
                time8.setText(String.format("Tempo8: %.3f s", tempos[7]));
                time9.setText(String.format("Tempo9: %.3f s", tempos[8]));
                time10.setText(String.format("Tempo10: %.3f s", tempos[9]));

            }

            paint.setColor(car.getColor());
            canvas.drawCircle(car.getX(), car.getY(), 10, paint); // Desenha o carro na nova posição

        }

        // Redesenha a imagem na ImageView
        imageView.setImageBitmap(mutableBitmap);
    }

    // Reseta os TextViews para o formato inicial
    private void resetTempos() {
        TextView time1 = findViewById(R.id.time1);
        TextView time2 = findViewById(R.id.time2);
        TextView time3 = findViewById(R.id.time3);
        TextView time4 = findViewById(R.id.time4);
        TextView time5 = findViewById(R.id.time5);
        TextView time6 = findViewById(R.id.time6);
        TextView time7 = findViewById(R.id.time7);
        TextView time8 = findViewById(R.id.time8);
        TextView time9 = findViewById(R.id.time9);
        TextView time10 = findViewById(R.id.time10);

        // Define o texto inicial (você pode ajustar para o texto desejado)
        time1.setText("Tempo1: ---");
        time2.setText("Tempo2: ---");
        time3.setText("Tempo3: ---");
        time4.setText("Tempo4: ---");
        time5.setText("Tempo5: ---");
        time6.setText("Tempo6: ---");
        time7.setText("Tempo7: ---");
        time8.setText("Tempo8: ---");
        time9.setText("Tempo9: ---");
        time10.setText("Tempo10: ---");
    }

    private void createCars() {

        carList.clear(); // Limpa a lista de carros

        for (int i = 0; i < 1; i++) {
            // Criar um único carro na posição especificada
            int x = 121;
            int y = 100;

            int color = getRandomColor();

            // Criar o carro
            Car car = new Car("Carro 1", x, y, color, 21);

            // Adicionar o carro à lista
            carList.add(car);
        }

    }


    // Método para gerar uma cor aleatória que não seja branca
    private int getRandomColor() {
        Random random = new Random();
        int color;
        do {
            color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        } while (color == Color.WHITE); // Garantir que a cor não seja branca
        return color;
    }
}