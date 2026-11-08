package com.curso_simulaciones.micuadragesimasegundaapp.actividades_secundarias;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.curso_simulaciones.micuadragesimasegundaapp.datos.AlmacenDatosRAM;

public class ActividadConfiguracion extends Activity {

    private EditText edit_text_broker, edit_text_usuario, edit_text_topico,
            edit_text_contrasena;
    private TextView text_broker, text_usuario, text_contrasena, text_topico;
    private int tamano_letra;
    private Button boton_grabar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gestionarResolucion();

        //para crear elementos de la GUI
        crearElementosGUI();

        ViewGroup.LayoutParams parametro_layout_principal = new
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        //pegar el contenedor con la GUI
        this.setContentView(crearGUI(), parametro_layout_principal);

        //Eventos GUI
        eventosGui();

    } //fin del método onCreate

    private void gestionarResolucion() {

        tamano_letra = (int) (0.5f * AlmacenDatosRAM.tamanoLetraResolucionIncluida);

    }

    private void eventosGui() {

        boton_grabar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                guardar();
                //regresar a la actividad anterior
                finish();

            }
        });
    }

    private TextView crearEtiqueta(String texto) {

        TextView etiqueta = new TextView(this);
        etiqueta.setGravity(Gravity.FILL_VERTICAL);
        etiqueta.setBackgroundColor(Color.YELLOW);
        etiqueta.setTextSize(tamano_letra);
        etiqueta.setText(texto);
        etiqueta.setTextColor(Color.BLACK);
        return etiqueta;

    }

    private EditText crearCampo(String valor) {

        EditText campo = new EditText(this);
        campo.setTextSize(tamano_letra);
        campo.setText("" + valor);
        return campo;

    }

    /*método responsable de la creación de los elementos de la GUI*/
    private void crearElementosGUI() {

        text_broker = crearEtiqueta("  URL DEL BROKER");
        edit_text_broker = crearCampo(AlmacenDatosRAM.MQTTHOST);

        text_usuario = crearEtiqueta("  USUARIO");
        edit_text_usuario = crearCampo(AlmacenDatosRAM.USERNAME);

        text_contrasena = crearEtiqueta("  CONTRASEÑA");
        edit_text_contrasena = crearCampo(AlmacenDatosRAM.PASSWORD);
        edit_text_contrasena.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edit_text_contrasena.setTransformationMethod(PasswordTransformationMethod.getInstance());

        text_topico = crearEtiqueta("  TOPICO");
        edit_text_topico = crearCampo(AlmacenDatosRAM.topicStr);

        boton_grabar = new Button(this);
        boton_grabar.setTextSize(TypedValue.COMPLEX_UNIT_SP, tamano_letra);
        boton_grabar.setText("GUARDAR");
        boton_grabar.getBackground().setColorFilter(Color.rgb(255, 255, 100),
                PorterDuff.Mode.MULTIPLY);

    }

    //fila horizontal: etiqueta a la izquierda y campo a la derecha
    private LinearLayout crearFila(TextView etiqueta, EditText campo) {

        LinearLayout fila = new LinearLayout(this);
        fila.setBackgroundColor(Color.YELLOW);
        fila.setWeightSum(2f);
        fila.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams parametrosIzquierda = new
                LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        parametrosIzquierda.weight = 1.0f;
        LinearLayout.LayoutParams parametrosDerecha = new
                LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        parametrosDerecha.weight = 1.0f;

        fila.addView(etiqueta, parametrosIzquierda);
        fila.addView(campo, parametrosDerecha);

        return fila;

    }

    private LinearLayout.LayoutParams parametrosFila(float peso) {

        LinearLayout.LayoutParams parametros = new
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        parametros.weight = peso;
        return parametros;

    }

    /*método responsable de administrar el diseño de la GUI*/
    private LinearLayout crearGUI() {

        //LinearLayoutPrincipal
        LinearLayout linear_principal = new LinearLayout(this);
        linear_principal.setOrientation(LinearLayout.VERTICAL);
        linear_principal.setBackgroundColor(Color.WHITE);

        linear_principal.addView(crearFila(text_broker, edit_text_broker), parametrosFila(0.5f));
        linear_principal.addView(crearFila(text_usuario, edit_text_usuario), parametrosFila(0.5f));
        linear_principal.addView(crearFila(text_contrasena, edit_text_contrasena), parametrosFila(0.5f));
        linear_principal.addView(crearFila(text_topico, edit_text_topico), parametrosFila(0.5f));

        //quinta fila: botón
        LinearLayout linearLayoutQuintaFila = new LinearLayout(this);
        linearLayoutQuintaFila.setBackgroundColor(Color.YELLOW);
        linearLayoutQuintaFila.setWeightSum(1f);
        linearLayoutQuintaFila.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams parametrosBoton = new
                LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        parametrosBoton.weight = 1.0f;
        linearLayoutQuintaFila.addView(boton_grabar, parametrosBoton);
        linear_principal.addView(linearLayoutQuintaFila, parametrosFila(0.5f));

        //sexta fila: espacio restante
        LinearLayout linearLayoutSextaFila = new LinearLayout(this);
        linearLayoutSextaFila.setBackgroundColor(Color.BLACK);
        linearLayoutSextaFila.setOrientation(LinearLayout.VERTICAL);
        linear_principal.addView(linearLayoutSextaFila, parametrosFila(7.5f));

        return linear_principal;
    }

    private void guardar() {

        AlmacenDatosRAM.MQTTHOST = edit_text_broker.getText().toString();
        AlmacenDatosRAM.USERNAME = edit_text_usuario.getText().toString();
        AlmacenDatosRAM.PASSWORD = edit_text_contrasena.getText().toString();
        AlmacenDatosRAM.topicStr = edit_text_topico.getText().toString();

        SharedPreferences prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("broker", AlmacenDatosRAM.MQTTHOST);
        editor.putString("usuario", AlmacenDatosRAM.USERNAME);
        editor.putString("pasword", AlmacenDatosRAM.PASSWORD);
        editor.putString("topico", AlmacenDatosRAM.topicStr);
        editor.commit();

    }

}