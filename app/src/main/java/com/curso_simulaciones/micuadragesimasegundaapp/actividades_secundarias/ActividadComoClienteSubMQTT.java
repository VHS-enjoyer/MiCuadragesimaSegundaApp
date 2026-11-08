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

import com.curso_simulaciones.micuadragesimasegundaapp.comunicaciones.ClientePubSubMQTT;
import com.curso_simulaciones.micuadragesimasegundaapp.datos.AlmacenDatosRAM;
import com.curso_simulaciones.micuadragesimasegundaapp.elementos_graficos.Acelerometro;
import com.curso_simulaciones.micuadragesimasegundaapp.elementos_graficos.CampoMagnetico;

import org.json.JSONException;
import org.json.JSONObject;

public class ActividadComoClienteSubMQTT extends Activity implements Runnable {

    private int tamanoLetraResolucionIncluida;
    private int COLOR_1 = Color.rgb(220, 156, 80);

    //en el SUB los tacómetros sólo despliegan: no captan sensores
    private Acelerometro acelerometro;
    private CampoMagnetico campoMagnetico;

    private Button botonConectar;
    private TextView textviewRol;
    private TextView textviewAviso;

    private ClientePubSubMQTT cliente;

    private JSONObject obj;

    private Thread hilo;
    private volatile boolean ejecutando = false;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        gestionarResolucion();

        creacionElementosGUI();

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

    }

    private void gestionarResolucion() {

        //tamano de letra para usar acomodado a la resolución de pantalla
        tamanoLetraResolucionIncluida = (int) (0.8 *
                AlmacenDatosRAM.tamanoLetraResolucionIncluida);

    }

    private void creacionElementosGUI() {

        acelerometro = new Acelerometro(this);
        acelerometro.setAngulosSectores(50, 100, 100);
        acelerometro.setColorFranjaDinámica(Color.rgb(0, 255, 0));

        campoMagnetico = new CampoMagnetico(this);
        campoMagnetico.setAngulosSectores(100, 100, 50);
        campoMagnetico.setColorFranjaDinámica(Color.rgb(255, 140, 0));

        textviewRol = new TextView(this);
        textviewRol.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                (int) (2 * tamanoLetraResolucionIncluida));
        textviewRol.setBackgroundColor(Color.YELLOW);
        textviewRol.setText("SUB");
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

    private LinearLayout.LayoutParams parametrosFila(float peso) {

        LinearLayout.LayoutParams parametros = new
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        parametros.weight = peso;
        return parametros;

    }

    private LinearLayout crearGUI() {

        //LinearLayoutPrincipal
        LinearLayout linearLayoutPrincipal = new LinearLayout(this);
        linearLayoutPrincipal.setOrientation(LinearLayout.VERTICAL);
        linearLayoutPrincipal.setBackgroundColor(Color.WHITE);
        linearLayoutPrincipal.setWeightSum(10f);

        LinearLayout.LayoutParams parametrosMatch = new
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        //fila 1: rol
        LinearLayout linearLayoutFilaUno = new LinearLayout(this);
        linearLayoutFilaUno.setBackgroundColor(Color.WHITE);
        linearLayoutFilaUno.setGravity(Gravity.CENTER);
        linearLayoutFilaUno.addView(textviewRol, parametrosMatch);

        //fila 2: acelerómetro
        LinearLayout linearLayoutFilaDos = new LinearLayout(this);
        linearLayoutFilaDos.setBackgroundColor(Color.WHITE);
        linearLayoutFilaDos.setGravity(Gravity.CENTER);
        linearLayoutFilaDos.addView(acelerometro, parametrosMatch);

        //fila 3: campo magnético
        LinearLayout linearLayoutFilaTres = new LinearLayout(this);
        linearLayoutFilaTres.setBackgroundColor(Color.WHITE);
        linearLayoutFilaTres.setGravity(Gravity.CENTER);
        linearLayoutFilaTres.addView(campoMagnetico, parametrosMatch);

        //fila 4: aviso
        LinearLayout linearLayoutFilaCuatro = new LinearLayout(this);
        linearLayoutFilaCuatro.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutFilaCuatro.setBackgroundColor(Color.WHITE);
        linearLayoutFilaCuatro.addView(textviewAviso, parametrosMatch);

        //fila 5: botón
        LinearLayout linearLayoutFilaCinco = new LinearLayout(this);
        linearLayoutFilaCinco.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutFilaCinco.setBackgroundColor(Color.WHITE);
        linearLayoutFilaCinco.setWeightSum(1f);
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

    protected void eventos() {

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

    protected void onPause() {
        super.onPause();
        ejecutando = false;
        if (cliente != null)
            cliente.desconectar();

    }

    public void run() {

        while (ejecutando) {

            try {
                Thread.sleep(100);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //leer IoT
            leer();
            //actualizar aqui estado de conexión
            runOnUiThread(new Runnable() {
                public void run() {
                    actualizarAviso();
                }
            });

        }

    }

    /*
    comunicaciones IoT SUB
     */

    private void leer() {

        //String JSON
        String nuevo_dato_string = cliente.leerString();

        if (nuevo_dato_string != null) {
            convertirStrigJson(nuevo_dato_string);

        }

    }

    //obtener la información del JSON
    public void convertirStrigJson(String datoString) {

        try {

            //convertir String a JSON
            obj = new JSONObject(datoString);

            //aceleración: componente seleccionada en el PUB y su valor
            int componente_aceleracion = obj.getInt("componente_aceleracion");
            String claveA = "a";
            if (componente_aceleracion == 1) claveA = "ax";
            if (componente_aceleracion == 2) claveA = "ay";
            if (componente_aceleracion == 3) claveA = "az";

            acelerometro.setComponenteAcelerometro(componente_aceleracion);
            acelerometro.setMedida((float) obj.getDouble(claveA));

            //campo magnético: componente seleccionada en el PUB y su valor
            int componente_campo = obj.getInt("componente_campo");
            String claveB = "b";
            if (componente_campo == 1) claveB = "bx";
            if (componente_campo == 2) claveB = "by";
            if (componente_campo == 3) claveB = "bz";

            float medida_campo = (float) obj.getDouble(claveB);
            campoMagnetico.setComponenteCampo(componente_campo);
            campoMagnetico.cambiarEscala(medida_campo);
            campoMagnetico.setMedida(medida_campo);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void actualizarAviso() {

        textviewAviso.setText("Estado conexión IoT:" + AlmacenDatosRAM.conectado_PubSub);

    }

}