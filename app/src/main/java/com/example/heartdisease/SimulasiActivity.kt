package com.example.heartdisease

import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class SimulasiActivity : AppCompatActivity() {

    private lateinit var interpreter: Interpreter
    private val mModelPath = "heartdisease.tflite"

    private lateinit var resultText: TextView
    private lateinit var Age: EditText
    private lateinit var Sex: EditText
    private lateinit var ChestPainType: EditText
    private lateinit var Restingbp: EditText
    private lateinit var Cholestoral: EditText
    private lateinit var Restecg: EditText
    private lateinit var Maxhr: EditText
    private lateinit var Exang: EditText
    private lateinit var Slope: EditText
    private lateinit var Thal: EditText
    private lateinit var checkButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulasi)

        resultText = findViewById(R.id.txtResult)
        Age = findViewById(R.id.Age)
        Sex = findViewById(R.id.Sex)
        ChestPainType = findViewById(R.id.ChestPainType)
        Restingbp = findViewById(R.id.Restingbp)
        Cholestoral = findViewById(R.id.Cholestoral)
        Restecg = findViewById(R.id.Restecg)
        Maxhr = findViewById(R.id.Maxhr)
        Exang = findViewById(R.id.Exang)
        Slope = findViewById(R.id.Slope)
        Thal = findViewById(R.id.Thal)
        checkButton = findViewById(R.id.btnCheck)

        checkButton.setOnClickListener {
            var result = doInference(
                Age.text.toString(),
                Sex.text.toString(),
                ChestPainType.text.toString(),
                Restingbp.text.toString(),
                Cholestoral.text.toString(),
                Restecg.text.toString(),
                Maxhr.text.toString(),
                Exang.text.toString(),
                Slope.text.toString(),
                Thal.text.toString())
            runOnUiThread {
                if (result == 0) {
                    resultText.text = "Terkena Penyakit Jantung"
                }else if (result == 1){
                    resultText.text = "Tidak Terkena Penyakit Jantung"
                }
            }
        }
        initInterpreter()
    }

    private fun initInterpreter() {
        val options = org.tensorflow.lite.Interpreter.Options()
        options.setNumThreads(8)
        options.setUseNNAPI(true)
        interpreter = org.tensorflow.lite.Interpreter(loadModelFile(assets, mModelPath), options)
    }

    private fun doInference(input1: String, input2: String, input3: String, input4: String, input5: String, input6: String, input7: String, input8: String, input9: String, input10: String): Int{
        val inputVal = FloatArray(10)
        inputVal[0] = input1.toFloat()
        inputVal[1] = input2.toFloat()
        inputVal[2] = input3.toFloat()
        inputVal[3] = input4.toFloat()
        inputVal[4] = input5.toFloat()
        inputVal[5] = input6.toFloat()
        inputVal[6] = input7.toFloat()
        inputVal[7] = input8.toFloat()
        inputVal[8] = input9.toFloat()
        inputVal[9] = input10.toFloat()
        val output = Array(1) { FloatArray(2) }
        interpreter.run(inputVal, output)

        Log.e("result", (output[0].toList()+" ").toString())

        return output[0].indexOfFirst { it == output[0].maxOrNull() }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer{
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}