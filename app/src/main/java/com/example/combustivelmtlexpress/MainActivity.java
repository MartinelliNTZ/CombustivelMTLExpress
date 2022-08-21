package com.example.combustivelmtlexpress;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity  {
    /*Declaracão de linkagem dos itens da interface*/
    private Switch swtKmTotal, swtKmFinal;
    private EditText kmFinal, kmInicial, kmTotal, combustivel;
    private Button btCalcular, btLimpar;
    private TextView txtResultado;
    private ImageView planoDeFundo, imgConsumo;

    /*Declaração de variaveis simples*/
    private boolean peloTotal, peloFinal;/*Total é true porque o switch total inicia checado e Final e false usado no metodo selecionarEdits*/
    private Double totalKM, totalLitros, mediaComsumo;
    private String msgAlerta;/*Mensagem que sera exibbida pelo metodo testeDeintegridade*/
    private int x;/*variavel que define qual imagem sera selecionada de plano de fundo*/
    /*Declaração do loop*/
    private Handler handler = new Handler();
    private Runnable runnable;
    /*variavel para formata a exibição de um Double*/
    private DecimalFormat fmt =new DecimalFormat("0.00");

    /*Variaveis que comtem os ids dos planos de fundo e das msg de erros.*/
    int[] imagensIds = {
            R.drawable.ckamarelo,
            R.drawable.ckverde,
            R.drawable.ckvermelho
    };
    int[] fundoIds = {
            R.drawable.foto1,
            R.drawable.foto2,
            R.drawable.foto3
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*linkagem das variaveis locais com a interface*/
        swtKmTotal = (Switch) findViewById(R.id.swtKmTotal);
        swtKmFinal=(Switch)findViewById(R.id.swtKmFinal);
        kmFinal = findViewById(R.id.edtKmFinal);
        kmInicial = findViewById(R.id.edtKmInicial);
        kmTotal = findViewById(R.id.edtKmTotal);
        combustivel = findViewById(R.id.edtCombustivel);
        txtResultado= findViewById(R.id.txtResultado);
        btLimpar = findViewById(R.id.btLimpar);
        btCalcular = findViewById(R.id.btCalcular);
        imgConsumo =findViewById(R.id.imgComsumo);
        planoDeFundo = findViewById(R.id.imageView2);

        /*Atribuiçao de variaveis*/
        peloFinal=true;
        peloTotal =false;
        msgAlerta="";
        x=0;

        /*Mudar checados e editaveis*/
        swtKmTotal.setChecked(true);
        selecionarEdits(peloTotal);

        /*Colocar switchs para quando um estiver ativo o outro desligar.*/
       swtKmFinal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(swtKmFinal.isChecked()){
                    swtKmTotal.setChecked(false);
                    selecionarEdits(peloFinal);
                }else {
                    swtKmTotal.setChecked(true);
                    selecionarEdits(peloTotal);
                }

            }
        });
       swtKmTotal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (swtKmTotal.isChecked()) {
                    swtKmFinal.setChecked(false);
                    selecionarEdits(peloTotal);
                } else {
                    swtKmFinal.setChecked(true);
                    selecionarEdits(peloFinal );
                }
            }
        });

        /*métodos dos botoes*/
       btLimpar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
            limpar();
           }
       });
       btCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isTudoOk()) {
                    calcular();
                    testeDeIntegridade();
                    txtResultado.setText("Seu veiculo percorreu " + totalKM + "KM, e gastou " + totalLitros + " litros de combustivel.\nSua média de consumo é:\n" + fmt.format(mediaComsumo) + "KM / Litro.\n\n"+msgAlerta);
                    txtResultado.setVisibility(View.VISIBLE);
                    imgConsumo.setVisibility(View.VISIBLE);
                }
            }
        });
        atualizarPlanoFundo();/*chama o método com loop infinito que vai trocar a imagem a cada 10 segundos*/
    }

    public void selecionarEdits(boolean estado){

        kmInicial.setEnabled(estado);
        kmFinal.setEnabled(estado);
        kmTotal.setEnabled(!estado);
    }/*Método que bloqueia e desbloqueia os edits conforme o switch que for selecionado*/
    public void limpar(){
        kmInicial.setText("");
        kmTotal.setText("");
        kmFinal.setText("");
        msgAlerta="";
        combustivel.setText("");
        txtResultado.setVisibility(View.INVISIBLE);
        imgConsumo.setVisibility(View.INVISIBLE);
    }/*Método que zera dos os campos e oculta imagens.*/
    public void calcular(){
        if(isTudoOk()& swtKmTotal.isChecked()){
            totalKM= Double.parseDouble(kmTotal.getText().toString());
            totalLitros = Double.parseDouble(combustivel.getText().toString());
            mediaComsumo = totalKM/totalLitros;
        }else if(isTudoOk() & swtKmFinal.isChecked()){
            totalKM= Double.parseDouble(kmFinal.getText().toString())-Double.parseDouble(kmInicial.getText().toString());
            totalLitros = Double.parseDouble(combustivel.getText().toString());
            mediaComsumo = totalKM/totalLitros;
        }
    }
    public boolean isTudoOk(){
        if(kmFinal.getText().length()!=0 & kmFinal.getText().length()!=0 & combustivel.getText().length()!=0 & swtKmFinal.isChecked()){
            if(Double.parseDouble(kmInicial.getText().toString())<Double.parseDouble(kmFinal.getText().toString())){
                kmInicial.setError(null);
                return true;
            }  else {
                kmInicial.requestFocus();
                kmInicial.setError("Km final deve ser maior que km inicial");
                return false;
            }
        }else if(kmTotal.getText().length()!=0 & combustivel.getText().length()!=0 & swtKmTotal.isChecked()){
            return true;
        }else{
            return false;
        }
    }/*método que verifica se todos os campos estao prenchidos*/
    public void testeDeIntegridade(){
        if(mediaComsumo<=1){
            imgConsumo.setImageResource(imagensIds[2]);
            msgAlerta="Sua média esta muito baixa, os valores estão corretos?";

        }else if(mediaComsumo>1 & mediaComsumo<=7){
            imgConsumo.setImageResource(imagensIds[0]);
            msgAlerta="Sua média esta um pouco baixa.";
        }else if(mediaComsumo>7 & mediaComsumo<=19){
            imgConsumo.setImageResource(imagensIds[1]);
            msgAlerta="Sua média está muito boa.";
        }else if(mediaComsumo>19 & mediaComsumo<=80) {
            imgConsumo.setImageResource(imagensIds[1]);
            msgAlerta = "Sua média está EXCELENTE.";
        }else if(mediaComsumo>80){
        imgConsumo.setImageResource(imagensIds[2]);
        msgAlerta="Sua média esta muito alta, os valores estão corretos?";
        }
    }/*Testa se a média de consumo esta boa e exibe mesnsagems.*/
    public void atualizarPlanoFundo() {
        runnable = new Runnable() {
            @Override
            public void run() {
                /*Aqui se insere o codigo que vai ficar em loop infinito*/
                if (x == 2) {
                    planoDeFundo.setImageResource(fundoIds[x]);
                    x = 0;
                } else {
                    planoDeFundo.setImageResource(fundoIds[x]);
                    x++;
                }

                long agora = SystemClock.uptimeMillis();/*Varialvel que recebe a hora atual em milisegundos*/
                long proximo = agora+(10000-(agora%1000));/*Varialvel que determina o valor da proxima atualizacao do loop 10.000=10segundos*/
                handler.postAtTime(runnable,proximo);/*hadler que executa o loop*/
            }
        };
        runnable.run();



    }/*Cria um loop infino bque troca o plano de fundo acada 10 segundos*/
}