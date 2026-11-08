package com.curso_simulaciones.micuadragesimasegundaapp.actividades_secundarias;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.curso_simulaciones.micuadragesimasegundaapp.R;
import com.curso_simulaciones.micuadragesimasegundaapp.elementos_graficos.Boton;

public class ActividadComunicacion extends Activity {

    private Boton pub, sub;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        creacionElementosGUI();

        setContentView(crearGUI());

        eventos();

    }

    private void creacionElementosGUI() {

        pub = new Boton(this);
        pub.setImagen(R.drawable.wifi_subida);

        sub = new Boton(this);
        sub.setImagen(R.drawable.wifi_bajada);

    }

    private LinearLayout crearGUI() {

        //LinearLayoutPrincipal
        LinearLayout linearLayoutPrincipal = new LinearLayout(this);
        linearLayoutPrincipal.setOrientation(LinearLayout.VERTICAL);
        linearLayoutPrincipal.setBackgroundColor(Color.WHITE);
        linearLayoutPrincipal.setWeightSum(10f);

        //LinearLayout primera fila
        LinearLayout linearLayoutPrimeraFila = new LinearLayout(this);
        linearLayoutPrimeraFila.setOrientation(LinearLayout.VERTICAL);

        //fondo primera fila
        Drawable fondo = getResources().getDrawable(R.drawable.modelo);
        linearLayoutPrimeraFila.setBackgroundDrawable(fondo);

        //LinearLayout segunda fila
        LinearLayout linearLayoutSegundaFila = new LinearLayout(this);
        linearLayoutSegundaFila.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutSegundaFila.setBackgroundColor(Color.WHITE);
        linearLayoutSegundaFila.setWeightSum(2f);

        //pegar las filas en el princpal
        LinearLayout.LayoutParams parametrosPrimeraFila = new
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        parametrosPrimeraFila.weight = 9.0f;
        linearLayoutPrimeraFila.setLayoutParams(parametrosPrimeraFila);

        LinearLayout.LayoutParams parametrosSegundaFila = new
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        parametrosSegundaFila.weight = 1.0f;
        linearLayoutSegundaFila.setLayoutParams(parametrosSegundaFila);

        linearLayoutPrincipal.addView(linearLayoutPrimeraFila);
        linearLayoutPrincipal.addView(linearLayoutSegundaFila);

        //pegado botones segunda fila
        LinearLayout.LayoutParams parametrosPegadoBotones = new
                LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        parametrosPegadoBotones.weight = 1.0f;

        linearLayoutSegundaFila.addView(pub, parametrosPegadoBotones);
        linearLayoutSegundaFila.addView(sub, parametrosPegadoBotones);

        return linearLayoutPrincipal;
    }

    private void eventos() {

        pub.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                lanzarActividadComoClientePubMQTT();

            }
        });

        sub.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                lanzarActividadComoClienteSubMQTT();

            }
        });

    }//fin eventos()

    private void lanzarActividadComoClientePubMQTT() {

        Intent intent = new Intent(this, ActividadComoClientePubMQTT.class);
        startActivity(intent);
    }

    private void lanzarActividadComoClienteSubMQTT() {

        Intent intent = new Intent(this, ActividadComoClienteSubMQTT.class);
        startActivity(intent);
    }

}