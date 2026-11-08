package com.curso_simulaciones.micuadragesimasegundaapp.actividades_secundarias;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.curso_simulaciones.micuadragesimasegundaapp.R;
import com.curso_simulaciones.micuadragesimasegundaapp.comunicaciones.ClientePubSubMQTT;
import com.curso_simulaciones.micuadragesimasegundaapp.datos.AlmacenDatosRAM;
import com.curso_simulaciones.micuadragesimasegundaapp.elementos_graficos.Acelerometro;
import com.curso_simulaciones.micuadragesimasegundaapp.elementos_graficos.Boton;
import com.curso_simulaciones.micuadragesimasegundaapp.elementos_graficos.CampoMagnetico;

import org.json.JSONException;
import org.json.JSONObject;

public class ActividadComoClientePubMQTT extends Activity implements Runnable {

    private int tamanoLetraResolucionIncluida;
    private int COLOR_1 = Color.rgb(220, 156, 80);

    //botones de selección de componente: aceleración y campo magnético
    private Boton ax, ay, az, a;
    private Boton bx, by, bz, b;

    private Acelerometro acelerometro;
    private CampoMagnetico campoMagnetico;

    protected TextView textviewRol;
    private TextView textviewAviso;

    protected Button botonConectar;

    //componente mostrada: 1: x, 2: y, 3: z, 4: módulo
    private int componente_aceleracion = 4;
    private int componente_campo = 4;

    private ClientePubSubMQTT cliente;

    private Thread hilo;
    private volatile boolean ejecutando = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gestionarResolucion();

        //para crear elementos de la GUI
        crearElementosGUI();

        /*
        Para informar cómo se debe pegar el administrador de
        diseño LinearLayout obtenido con el método crearGui()
        */
        ViewGroup.LayoutParams parametro_layout_principal = new
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        //pegar el contenedor con la GUI
        this.setContentView(crearGUI(), parametro_layout_principal);

        eventos();

        crearCliente();

        hilo = new Thread(this);

    }//fin del método onCreate

    private void gestionarResolucion() {

        //tamano de letra para usar acomodado a la resolución de pantalla
        tamanoLetraResolucionIncluida = (int) (0.8 *
                AlmacenDatosRAM.tamanoLetraResolucionIncluida);

    }

    private void crearElementosGUI() {

        //botones de la aceleración
        ax = new Boton(this);
        ax.setImagen(R.drawable.ax);
        ay = new Boton(this);
        ay.setImagen(R.drawable.ay);
        az = new Boton(this);
        az.setImagen(R.drawable.az);
        a = new Boton(this);
        a.setImagen(R.drawable.a);

        //botones del campo magnético
        bx = new Boton(this);
        bx.setImagen(R.drawable.bx);
        by = new Boton(this);
        by.setImagen(R.drawable.by);
        bz = new Boton(this);
        bz.setImagen(R.drawable.bz);
        b = new Boton(this);
        b.setImagen(R.drawable.b);

        acelerometro = new Acelerometro(this);
        acelerometro.setAngulosSectores(50, 100, 100);
        acelerometro.setColorFranjaDinámica(Color.rgb(0, 255, 0));
        acelerometro.captarSensor(this);

        campoMagnetico = new CampoMagnetico(this);
        campoMagnetico.setAngulosSectores(100, 100, 50);
        campoMagnetico.setColorFranjaDinámica(Color.rgb(255, 140, 0));
        campoMagnetico.captarSensor(this);

        textviewRol = new TextView(this);
        textviewRol.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                (int) (2f * tamanoLetraResolucionIncluida));
        textviewRol.setBackgroundColor(Color.YELLOW);
        textviewRol.setText("PUB");
        textviewRol.setTextColor(Color.RED);
        textviewRol.setGravity(Gravity.CENTER);
        textviewRol.setEnabled(false);

        textviewAviso = new TextView(this);
        textviewAviso.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                (int) (0.8 * tamanoLetraResolucionIncluida));
        textviewAviso.setBackgroundColor(Color.YELLOW);
        textviewAviso.setTextColor(Color.RED);
        textviewAviso.setGravity(Gravity.CENTER);

        botonConectar = new Button(this);
        botonConectar.setTextSize(TypedValue.COMPLEX_UNIT_SP, tamanoLetraResolucionIncluida);
        botonConectar.setText("CONECTAR");
        botonConectar.getBackground().setColorFilter(COLOR_1, PorterDuff.Mode.MULTIPLY);

    }

    /*
     Fila con un tacómetro a la izquierda (80 %) y cuatro botones
     de selección de componente en columna a la derecha (20 %)
     */
    private LinearLayout crearFilaInstrumento(View instrumento, Boton b1, Boton b2, Boton b3,
                                              Boton b4) {

        LinearLayout fila = new LinearLayout(this);
        fila.setBackgroundColor(Color.WHITE);
        fila.setWeightSum(10f);

        LinearLayout columnaIzquierda = new LinearLayout(this);
        LinearLayout columnaDerecha = new LinearLayout(this);
        columnaDerecha.setOrientation(LinearLayout.VERTICAL);
        columnaDerecha.setWeightSum(4f);

        LinearLayout.LayoutParams parametrosIzquierda = new
                LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        parametrosIzquierda.weight = 8.0f;
        fila.addView(columnaIzquierda, parametrosIzquierda);

        LinearLayout.LayoutParams parametrosDerecha = new
                LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        parametrosDerecha.weight = 2.0f;
        fila.addView(columnaDerecha, parametrosDerecha);

        columnaIzquierda.addView(instrumento, new
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout.LayoutParams parametrosBotones = new
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        parametrosBotones.weight = 1.0f;
        columnaDerecha.addView(b1, parametrosBotones);
        columnaDerecha.addView(b2, parametrosBotones);
        columnaDerecha.addView(b3, parametrosBotones);
        columnaDerecha.addView(b4, parametrosBotones);

        return fila;

    }

    private LinearLayout crearGUI() {

        //LinearLayoutPrincipal
        LinearLayout linearLayoutPrincipal = new LinearLayout(this);
        linearLayoutPrincipal.setOrientation(LinearLayout.VERTICAL);
        linearLayoutPrincipal.setBackgroundColor(Color.BLACK);
        linearLayoutPrincipal.setWeightSum(10f);

        //fila 1: rol
        LinearLayout linearLayoutFilaUno = new LinearLayout(this);
        linearLayoutFilaUno.setBackgroundColor(Color.WHITE);
        linearLayoutFilaUno.setGravity(Gravity.CENTER);
        textviewRol.setLayoutParams(new
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayoutFilaUno.addView(textviewRol);

        //fila 2: acelerómetro y botones ax, ay, az, a
        LinearLayout linearLayoutFilaDos = crearFilaInstrumento(acelerometro, ax, ay, az, a);

        //fila 3: campo magnético y botones bx, by, bz, b
        LinearLayout linearLayoutFilaTres = crearFilaInstrumento(campoMagnetico, bx, by, bz, b);

        //fila 4: aviso
        LinearLayout linearLayoutFilaCuatro = new LinearLayout(this);
        linearLayoutFilaCuatro.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutFilaCuatro.setBackgroundColor(Color.WHITE);
        linearLayoutFilaCuatro.addView(textviewAviso, new
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        //fila 5: botón conectar
        LinearLayout linearLayoutFilaCinco = new LinearLayout(this);
        linearLayoutFilaCinco.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutFilaCinco.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams parametrosPegadoBotones = new
                LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        parametrosPegadoBotones.weight = 1.0f;
        linearLayoutFilaCinco.addView(botonConectar, parametrosPegadoBotones);

        //pegar las filas en el principal
        linearLayoutPrincipal.addView(linearLayoutFilaUno, parametrosFila(1.0f));
        linearLayoutPrincipal.addView(linearLayoutFilaDos, parametrosFila(3.75f));
        linearLayoutPrincipal.addView(linearLayoutFilaTres, parametrosFila(3.75f));
        linearLayoutPrincipal.addView(linearLayoutFilaCuatro, parametrosFila(0.5f));
        linearLayoutPrincipal.addView(linearLayoutFilaCinco, parametrosFila(1.0f));

        return linearLayoutPrincipal;
    }

    private LinearLayout.LayoutParams parametrosFila(float peso) {

        LinearLayout.LayoutParams parametros = new
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        parametros.weight = peso;
        return parametros;

    }

    private void seleccionarAceleracion(int componente) {

        acelerometro.setComponenteAcelerometro(componente);
        componente_aceleracion = componente;

    }

    private void seleccionarCampo(int componente) {

        campoMagnetico.setComponenteCampo(componente);
        componente_campo = componente;

    }

    protected void eventos() {

        ax.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { seleccionarAceleracion(1); }
        });

        ay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { seleccionarAceleracion(2); }
        });

        az.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { seleccionarAceleracion(3); }
        });

        a.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { seleccionarAceleracion(4); }
        });

        bx.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { seleccionarCampo(1); }
        });

        by.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { seleccionarCampo(2); }
        });

        bz.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { seleccionarCampo(3); }
        });

        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { seleccionarCampo(4); }
        });

        //evento cliente
        botonConectar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (botonConectar.getText().toString().equals("CONECTAR")) {
                    botonConectar.setText("EMPEZAR");
                    //conectar cliente
                    cliente.conectar();

                } else {

                    ejecutando = true;
                    hilo.start();
                    botonConectar.setEnabled(false);

                }

            }
        });

    }

    public void crearCliente() {

        cliente = new ClientePubSubMQTT(this);

    }

    protected void onResume() {
        super.onResume();

    }

    protected void onPause() {
        super.onPause();
        ejecutando = false;
        if (cliente != null)
            cliente.desconectar();

    }

    protected void onDestroy() {
        super.onDestroy();
        acelerometro.soltarSensor();
        campoMagnetico.soltarSensor();

    }

    public void run() {

        while (ejecutando) {

            try {
                Thread.sleep(200);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //escribir IoT
            escribir();
            //actualizar aqui estado de conexión
            runOnUiThread(new Runnable() {
                public void run() {
                    actualizarAviso();
                }
            });

        }

    }

    /*
    comunicaciones IoT PUB
     */

    private void escribir() {

        //escribir (enviar) datos al cliente
        String dato = getStringJSON();
        byte[] dato_en_byte = dato.getBytes();
        cliente.setEnviarMensajes(dato_en_byte);

    }

    /*
     JSON enviado:
     componente_aceleracion, ax, ay, az, a  (m/s2)
     componente_campo, bx, by, bz, b        (microteslas)
     */
    private String getStringJSON() {

        JSONObject obj = new JSONObject();

        try {

            obj.put("componente_aceleracion", componente_aceleracion);
            obj.put("ax", acelerometro.getAx());
            obj.put("ay", acelerometro.getAy());
            obj.put("az", acelerometro.getAz());
            obj.put("a", acelerometro.getA());

            obj.put("componente_campo", componente_campo);
            obj.put("bx", campoMagnetico.getBx());
            obj.put("by", campoMagnetico.getBy());
            obj.put("bz", campoMagnetico.getBz());
            obj.put("b", campoMagnetico.getB());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //convertir a String
        return obj.toString();

    }

    private void actualizarAviso() {

        textviewAviso.setText("Estado conexión IoT:" + AlmacenDatosRAM.conectado_PubSub);

    }

}