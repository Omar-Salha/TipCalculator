package com.example.tippy

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.tippy.databinding.ActivityMainBinding

private const val INITIAL_TIP_PERCENT = 15
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tvTipPercentLabel: TextView
    private lateinit var etBaseAmount: EditText
    private lateinit var sbTip: SeekBar
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipDescription: TextView
    private lateinit var spinnerPeople: Spinner
    private var numOfPeople: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tvTipPercentLabel = binding.tvTipPercentLabel
        etBaseAmount = binding.etBaseAmount
        sbTip = binding.sbTip
        tvTipAmount = binding.tvTipAmount
        tvTotalAmount = binding.tvTotalAmount
        tvTipDescription = binding.tvTipDescription

        spinnerPeople = binding.spinnerPeople
        ArrayAdapter.createFromResource(
            this,
            R.array.people_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            spinnerPeople.adapter = adapter
        }


        sbTip.progress = INITIAL_TIP_PERCENT
        tvTipPercentLabel.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescrition(INITIAL_TIP_PERCENT)

        sbTip.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                tvTipPercentLabel.text = "$p1%"
                computeTipAndTotal(numOfPeople)
                updateTipDescrition(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        etBaseAmount.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                computeTipAndTotal(numOfPeople)
            }

        })

        spinnerPeople.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                numOfPeople = p0?.getItemAtPosition(p2).toString().toInt()
                computeTipAndTotal(numOfPeople)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        })
    }

    private fun updateTipDescrition(tipPercent: Int) {
        val tipDescription = when (tipPercent) {
            in 0..9 -> "Poor"
            in 10..14 -> "Acceptable"
            in 15..19 -> "Good"
            in 20..24 -> "Great"
            else -> "Amazing"
        }

        tvTipDescription.text = tipDescription

        val color = ArgbEvaluator().evaluate(
            tipPercent.toFloat() / sbTip.max,
            ContextCompat.getColor(this, R.color.poor_tip),
            ContextCompat.getColor(this, R.color.amazing_tip)
        ) as Int
        tvTipDescription.setTextColor(color)
    }

    private fun computeTipAndTotal(numOfPeople: Int) {
        if (etBaseAmount.text.isEmpty()) {
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            return
        }

        val baseAmount = etBaseAmount.text.toString().toDouble()
        val tipPercent = sbTip.progress

        val tipAmount = baseAmount * tipPercent / 100
        val totalAmount = (baseAmount + tipAmount) / numOfPeople

        tvTipAmount.text = "%.2f".format(tipAmount)
        tvTotalAmount.text = "%.2f".format(totalAmount)+ " per Person"

    }
}