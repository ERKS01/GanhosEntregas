package com.ganhos.app.utils

/**
 * Utilitários para formatação e conversão de valores monetários.
 * Aceita pontos (600.50) e vírgulas (600,50) como separadores decimais.
 */

/**
 * Formata um valor Double para moeda brasileira (Real).
 * Exemplo: 600.5 → R$ 600,50
 */
fun Double.formatCurrency(): String {
    return String.format("R$ %.2f", this).replace(".", ",")
}

/**
 * Converte uma String para Double, aceitando ponto ou vírgula.
 * Exemplo: "600.50" → 600.5 | "600,50" → 600.5
 */
fun String.toDoubleOrZero(): Double {
    return try {
        val normalized = this.trim().replace(",", ".")
        normalized.toDoubleOrNull() ?: 0.0
    } catch (e: Exception) {
        0.0
    }
}

/**
 * Limita um valor Double a 2 casas decimais.
 * Exemplo: 600.5555 → 600.56 (com arredondamento)
 */
fun Double.limitDecimals(): Double {
    return kotlin.math.round(this * 100) / 100
}

/**
 * Formata um valor para exibição em EditText.
 * Remove zeros à direita desnecessários.
 * Exemplo: 600.50 → "600.5" | 600.00 → "600"
 */
fun Double.toInputFormat(): String {
    return if (this % 1.0 == 0.0) {
        this.toInt().toString()
    } else {
        String.format("%.2f", this)
    }
}