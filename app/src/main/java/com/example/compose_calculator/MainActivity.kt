package com.example.compose_calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose_calculator.ui.theme.ComposeCalculatorTheme
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeCalculatorTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF1E1E1E)) {
                    CalculatorApp()
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorApp() {
    var expression by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(text = expression, fontSize = 32.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = result, fontSize = 24.sp, color = Color.Gray)
        }

        val buttons = listOf(
            listOf("7", "8", "9", "/"),
            listOf("4", "5", "6", "*"),
            listOf("1", "2", "3", "-"),
            listOf("0", "C", "=", "+"),
            listOf("⌫")
        )

        buttons.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                row.forEach { button ->
                    CalculatorButton(button) {
                        when (button) {
                            "C" -> {
                                expression = ""
                                result = ""
                            }
                            "=" -> {
                                val evalResult = evaluateExpression(expression)
                                if (evalResult != "Error") {
                                    expression = evalResult
                                }
                                result = evalResult
                            }
                            "⌫" -> {
                                if (expression.isNotEmpty()) {
                                    expression = expression.dropLast(1)
                                }
                            }
                            else -> {
                                expression += button
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(text: String, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(80.dp)
            .background(Color.DarkGray, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Text(text = text, fontSize = 28.sp, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

fun evaluateExpression(expression: String): String {
    return try {
        val tokens = expression.replace("×", "*").replace("÷", "/")
        val result = eval(tokens)
        result.toString()
    } catch (e: Exception) {
        "Error"
    }
}

fun eval(expression: String): Double {
    val numbers = mutableListOf<Double>()
    val operators = mutableListOf<Char>()
    var currentNumber = ""

    for (char in expression) {
        if (char.isDigit() || char == '.') {
            currentNumber += char
        } else {
            if (currentNumber.isNotEmpty()) {
                numbers.add(currentNumber.toDouble())
                currentNumber = ""
            }
            operators.add(char)
        }
    }
    if (currentNumber.isNotEmpty()) numbers.add(currentNumber.toDouble())

    while (operators.contains('*') || operators.contains('/')) {
        for (i in operators.indices) {
            if (operators[i] == '*' || operators[i] == '/') {
                val result = if (operators[i] == '*') numbers[i] * numbers[i + 1] else numbers[i] / numbers[i + 1]
                numbers[i] = result
                numbers.removeAt(i + 1)
                operators.removeAt(i)
                break
            }
        }
    }

    while (operators.isNotEmpty()) {
        val result = if (operators[0] == '+') numbers[0] + numbers[1] else numbers[0] - numbers[1]
        numbers[0] = result
        numbers.removeAt(1)
        operators.removeAt(0)
    }
    return numbers[0]
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeCalculatorTheme {
        CalculatorApp()
    }
}



