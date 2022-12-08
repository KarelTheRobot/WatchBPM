package com.example.metronome2

import android.app.Activity
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import com.example.metronome2.databinding.ActivityMainBinding
import java.time.Instant
import kotlin.math.roundToInt

class MainActivity : Activity(), SensorEventListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager
    private lateinit var bpmlabel: TextView
    private lateinit var accLists: MutableList<MutableList<Float>>
    private var sensorType = Sensor.TYPE_LINEAR_ACCELERATION
    private lateinit var bpmCounter : BPMCounter;
    private var countEvery = 3;
    private var c = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bpmlabel = findViewById(R.id.bpmlabel)
        bpmlabel.setText("Loading...")


        setupSensorStuff()
        bpmCounter = BPMCounter(1024, 32);
    }

    private fun setupSensorStuff() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(sensorType)?.also {
            sensorManager.registerListener(
                this, it,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST

            )
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == sensorType) {
            c += 1
            if (c % countEvery == 0) {
                val result = bpmCounter.record(event.values[0], event.values[1], event.values[2])
                if (result != -1.0f) {
                    // Round to the nearest 5
                    var result_int = ((result * 12).roundToInt() * 5)
                    bpmlabel.setText("" + result_int)
                }
            }
        }
    }

    private fun get_color(bpm: Int) {

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }
}