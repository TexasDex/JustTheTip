package com.texasdex.justthetip

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round

class CalculatorViewModel(private val preferencesManager: PreferencesManager) : ViewModel() {

    var billAmountInput by mutableStateOf("")
    var isManualBillMode by mutableStateOf(false)
    var tipPercentageInput by mutableStateOf("")
    
    var roundingOption by mutableStateOf(RoundingOption.NEAREST)
    var defaultPercentage by mutableStateOf(20f)
    var isAtmModeEnabled by mutableStateOf(true)

    init {
        viewModelScope.launch {
            defaultPercentage = preferencesManager.defaultPercentage.first()
            roundingOption = preferencesManager.roundingOption.first()
            isAtmModeEnabled = preferencesManager.atmMode.first()
            
            tipPercentageInput = if (defaultPercentage % 1 == 0f) {
                defaultPercentage.toInt().toString()
            } else {
                defaultPercentage.toString()
            }
        }
    }

    val billAmount: Double
        get() = billAmountInput.toDoubleOrNull() ?: 0.0

    val tipPercentage: Double
        get() = tipPercentageInput.toDoubleOrNull() ?: defaultPercentage.toDouble()

    val tipAmount: Double
        get() = round(billAmount * tipPercentage) / 100.0

    val totalAmount: Double
        get() = billAmount + tipAmount

    fun updateBillAmount(input: String) {
        if (!isAtmModeEnabled) {
            billAmountInput = input
            return
        }

        if (input.isEmpty()) {
            billAmountInput = ""
            isManualBillMode = false
            return
        }

        val oldPeriodCount = billAmountInput.count { it == '.' }
        val newPeriodCount = input.count { it == '.' }

        if (newPeriodCount > oldPeriodCount) {
            isManualBillMode = true
        }

        if (isManualBillMode) {
            billAmountInput = input
        } else {
            val digits = input.filter { it.isDigit() }
            if (digits.isEmpty()) {
                billAmountInput = ""
            } else {
                val value = digits.toDouble() / 100.0
                billAmountInput = String.format(Locale.US, "%.2f", value)
            }
        }
    }

    fun updateTipPercentage(input: String) {
        tipPercentageInput = input
    }

    fun setTipPercentage(percentage: Float) {
        val formatted = if (percentage % 1 == 0f) {
            percentage.toInt().toString()
        } else {
            "%.1f".format(percentage)
        }
        tipPercentageInput = formatted
    }

    fun reset() {
        billAmountInput = ""
        isManualBillMode = false
        tipPercentageInput = if (defaultPercentage % 1 == 0f) {
            defaultPercentage.toInt().toString()
        } else {
            defaultPercentage.toString()
        }
    }

    fun roundTip() {
        val currentTip = tipAmount
        val roundedTip = applyRounding(currentTip)
        if (billAmount > 0) {
            val newPercentage = (roundedTip / billAmount) * 100
            tipPercentageInput = "%.4f".format(newPercentage).trimEnd('0').trimEnd('.')
        }
    }

    fun roundTotal() {
        val currentTotal = totalAmount
        var roundedTotal = applyRounding(currentTotal)
        
        // Ensure tip doesn't become negative
        if (roundedTotal < billAmount) {
            roundedTotal = ceil(billAmount)
        }

        val newTip = roundedTotal - billAmount
        if (billAmount > 0) {
            val newPercentage = (newTip / billAmount) * 100
            tipPercentageInput = "%.4f".format(newPercentage).trimEnd('0').trimEnd('.')
        }
    }

    fun adjustTip(delta: Double) {
        if (billAmount > 0) {
            val newTip = (tipAmount + delta).coerceAtLeast(0.0)
            val newPercentage = (newTip / billAmount) * 100
            tipPercentageInput = "%.4f".format(newPercentage).trimEnd('0').trimEnd('.')
        }
    }

    fun updateDefaultPercentage(percentage: Float) {
        defaultPercentage = percentage
        viewModelScope.launch {
            preferencesManager.setDefaultPercentage(percentage)
        }
    }

    fun updateRoundingOption(option: RoundingOption) {
        roundingOption = option
        viewModelScope.launch {
            preferencesManager.setRoundingOption(option)
        }
    }

    fun updateAtmMode(enabled: Boolean) {
        isAtmModeEnabled = enabled
        viewModelScope.launch {
            preferencesManager.setAtmMode(enabled)
        }
        // When switching, reset manual mode for consistency
        isManualBillMode = false
    }

    private fun applyRounding(value: Double): Double {
        return when (roundingOption) {
            RoundingOption.UP -> ceil(value)
            RoundingOption.DOWN -> floor(value)
            RoundingOption.NEAREST -> round(value)
        }
    }
}
