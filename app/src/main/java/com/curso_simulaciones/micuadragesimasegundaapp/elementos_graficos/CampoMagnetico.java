package com.curso_simulaciones.micuadragesimasegundaapp.elementos_graficos;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class CampoMagnetico extends GaugeSimple implements SensorEventListener {

    private SensorManager sensorManager;

    //1: Bx, 2: By, 3: Bz, 4: módulo B
    private int componenteCampo = 4;

    //últimas medidas (un decimal) en microteslas
    private float bx = 0f, by = 0f, bz = 0f, bTotal = 0f;

    //límites posibles de la escala (en µT)
    private static final float[] LIMITES = {100f, 200f, 500f, 1000f, 5000f};
    private float limite = LIMITES[0];

    public CampoMagnetico(Context context) {
        super(context);
        //estado inicial
        setComponenteCampo(componenteCampo);

    }

    public void setComponenteCampo(int componenteCampo) {

        this.componenteCampo = componenteCampo;

        if (componenteCampo == 1) this.setUnidades("Bx (µT)");
        if (componenteCampo == 2) this.setUnidades("By (µT)");
        if (componenteCampo == 3) this.setUnidades("Bz (µT)");
        if (componenteCampo == 4) this.setUnidades("B (µT)");

        aplicarRango();

    }

    private void aplicarRango() {

        if (componenteCampo == 4) {
            this.setRango(0, limite);
        } else {
            this.setRango(-limite, limite);
        }

    }

    /*
     Reescala el tacómetro de acuerdo con la medida.
     Sube de escala cuando la medida supera el límite actual y
     baja cuando cabe holgadamente (90 %) en un límite menor,
     para evitar que la escala parpadee cerca de un límite.
     */
    public void cambiarEscala(float medida) {

        float m = Math.abs(medida);

        if (m > limite) {
            limite = menorLimite(m, 1.0f);
        } else {
            float ajustado = menorLimite(m, 0.9f);
            if (ajustado < limite) {
                limite = ajustado;
            }
        }

        aplicarRango();

    }

    private float menorLimite(float valor, float factor) {

        for (float l : LIMITES) {
            if (valor <= factor * l) {
                return l;
            }
        }
        return LIMITES[LIMITES.length - 1];

    }

    public void captarSensor(Context context) {

        //captamos el servicio del sensor
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);

    }

    public void soltarSensor() {

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

    }

    //se activa sólo cuando hay cambios
    public void onSensorChanged(SensorEvent event) {

        float medida_x = event.values[SensorManager.DATA_X];
        float medida_y = event.values[SensorManager.DATA_Y];
        float medida_z = event.values[SensorManager.DATA_Z];
        float resultado = medida_x * medida_x + medida_y * medida_y + medida_z * medida_z;
        float b = (float) (Math.sqrt(resultado));

        bx = redondear(medida_x);
        by = redondear(medida_y);
        bz = redondear(medida_z);
        bTotal = redondear(b);

        float medida = bTotal;
        if (componenteCampo == 1) medida = bx;
        if (componenteCampo == 2) medida = by;
        if (componenteCampo == 3) medida = bz;

        cambiarEscala(medida);
        this.setMedida(medida);

    }

    //un decimal
    private float redondear(float valor) {

        return Math.round(valor * 10) / 10.0f;

    }

    public float getBx() { return bx; }

    public float getBy() { return by; }

    public float getBz() { return bz; }

    public float getB() { return bTotal; }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}