package com.agenciacristal.calculadora

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    private var operacionActual = ""
    private var primerNumero: Double = Double.NaN
    private lateinit var operandoCalculadora: TextView // entrada que el usuario teclea
    private lateinit var resultadoCalculadora: TextView // resultado/acumulado
    private lateinit var formatoDecimal: DecimalFormat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vista_principal)

        formatoDecimal = DecimalFormat("#.##########")
        operandoCalculadora = findViewById(R.id.operandoCalculadora)
        resultadoCalculadora = findViewById(R.id.resultadoCalculadora)

        // Restaurar estado si rota la pantalla (opcional)
        savedInstanceState?.let {
            primerNumero = it.getDouble("primerNumero", Double.NaN)
            operacionActual = it.getString("operacionActual", "")
            operandoCalculadora.text = it.getString("entrada", "")
            resultadoCalculadora.text = it.getString("resultado", "")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putDouble("primerNumero", primerNumero)
        outState.putString("operacionActual", operacionActual)
        outState.putString("entrada", operandoCalculadora.text.toString())
        outState.putString("resultado", resultadoCalculadora.text.toString())
    }

    private fun tienePrimerNumero() = !primerNumero.isNaN()

    fun cambiarOperador(v: View) {
        val boton = v as Button
        // Si hay entrada o ya existe un acumulado, consolidar antes de cambiar operador
        if (operandoCalculadora.text.isNotEmpty() || tienePrimerNumero()) {
            calcular() // consolida la entrada actual en primerNumero
            operacionActual = when (boton.text.toString().trim()) {
                "X" -> "*"
                "÷" -> "/"
                else -> boton.text.toString().trim() // "+", "-", "/"
            }
            // Mostrar acumulado y limpiar la entrada para el siguiente número
            if (tienePrimerNumero()) {
                resultadoCalculadora.text = formatoDecimal.format(primerNumero)
            }
            operandoCalculadora.text = ""
        }
    }

    fun calcular() {
        // Si no hay entrada nueva, no hay nada que aplicar
        if (operandoCalculadora.text.isEmpty()) return
        val valor = operandoCalculadora.text.toString().toDoubleOrNull() ?: return
       if (!tienePrimerNumero()) {
            // Primer número de la operación
            primerNumero = valor
        } else {
            // División por cero
            if (operacionActual == "/" && valor == 0.0) {
                resultadoCalculadora.text = "Error"
                primerNumero = Double.NaN
                operacionActual = ""
                operandoCalculadora.text = ""
                return
            }
            primerNumero = when (operacionActual) {
                "+" -> primerNumero + valor
                "-" -> primerNumero - valor
                "*" -> primerNumero * valor
                "/" -> primerNumero / valor
                else -> valor // por si no había operador aún
            }
        }
        // Limpia la entrada para continuar operando
        operandoCalculadora.text = ""
    }

    fun seleccionarNumero(v: View) {
        // Solo concatena el dígito (sin punto decimal)
        val texto = (v as Button).text.toString()
        operandoCalculadora.text = operandoCalculadora.text.toString() + texto
    }

    fun igual(@Suppress("UNUSED_PARAMETER") v: View) {
        calcular()
        if (tienePrimerNumero()) {
            resultadoCalculadora.text = formatoDecimal.format(primerNumero)
        }
        operacionActual = ""
    }

    fun borrar(v: View) {
        val etiqueta = (v as Button).text.toString().trim()
        if (etiqueta == "C") {
            val s = operandoCalculadora.text.toString()
            if (s.isNotEmpty()) {
                operandoCalculadora.text = s.substring(0, s.length - 1)
            } else {
                // reset suave
                primerNumero = Double.NaN
                operacionActual = ""
                resultadoCalculadora.text = ""
            }
        } else if (etiqueta == "CA") {
            // reset total
            primerNumero = Double.NaN
            operacionActual = ""
            operandoCalculadora.text = ""
            resultadoCalculadora.text = ""
        }
    }
}
