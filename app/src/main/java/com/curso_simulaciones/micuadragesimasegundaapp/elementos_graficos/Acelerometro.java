package com.curso_simulaciones.micuadragesimasegundaapp.elementos_graficos;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Acelerometro extends GaugeSimple implements SensorEventListener {

    private SensorManager sensorManager;
    private int componenteAceleracion = 4;

    //últimas medidas (un decimal) de las tres componentes y del módulo
    private float ax = 0f, ay = 0f, az = 0f, aTotal = 0f;

    public Acelerometro(Context context) {
        super(context);
        //estado inicial
        setComponenteAcelerometro(componenteAceleracion);

    }

    /*
     1: ax, 2: ay, 3: az, 4: módulo de la aceleración
     */
    public void setComponenteAcelerometro(int componenteAceleracion) {

        this.componenteAceleracion = componenteAceleracion;

        if (componenteAceleracion == 1) {
            this.setUnidades("ax (m/s2)");
            this.setRango(-20, 20);
        }

        if (componenteAceleracion == 2) {
            this.setUnidades("ay (m/s2)");
            this.setRango(-20, 20);
        }

        if (componenteAceleracion == 3) {
            this.setUnidades("az (m/s2)");
            this.setRango(-20, 20);
        }

        if (componenteAceleracion == 4) {
            this.setUnidades("a (m/s2)");
            this.setRango(0, 20);
        }

    }

    public void captarSensor(Context context) {

        //captamos el servicio del sensor
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
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
        float a = (float) (Math.sqrt(resultado));

        ax = redondear(medida_x);
        ay = redondear(medida_y);
        az = redondear(medida_z);
        aTotal = redondear(a);

        float medida = aTotal;
        if (componenteAceleracion == 1) medida = ax;
        if (componenteAceleracion == 2) medida = ay;
        if (componenteAceleracion == 3) medida = az;

        this.setMedida(medida);

    }

    //un decimal
    private float redondear(float valor) {

        return Math.round(valor * 10) / 10.0f;

    }

    public float getAx() { return ax; }

    public float getAy() { return ay; }

    public float getAz() { return az; }

    public float getA() { return aTotal; }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}