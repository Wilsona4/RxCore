package org.example

import java.util.*
import kotlin.math.abs

fun main() {
//    println(countingSort(arrayOf(0, 0, 1, 2, 1)))
    println(pangrams("We promptly judged antique ivory buckles for the next prize"))
}

fun matchingStrings(strings: Array<String>, queries: Array<String>): Array<Int> {
    // Write your code here
    return queries.map { query -> strings.count { query == it } }.toTypedArray()
}

fun lonelyInteger(a: Array<Int>): Int =
    a.groupingBy { it }.eachCount().filterValues { value -> value == 1 }.keys.first()

fun diagonalDifference(arr: Array<Array<Int>>): Int {
    // Write your code here
    var right = 0
    var left = 0

    for (i in arr.indices) {
        right += arr[i][i]
        left += arr[i][(arr.size - 1) - i]
    }

    return abs(right - left)
}

fun flippingBits(n: Long): Long {
    return n.toUInt().inv().toLong()
}

fun countingSort(arr: Array<Int>): Array<Int> {
    // Write your code here
    val list = Array(100) { 0 }

    arr.groupingBy { it }.eachCount().forEach { (t, u) ->
        list[t] = u
    }

//    for (i in arr.indices){
//        list[arr[i]]++
//    }

    return list
}

fun pangrams(s: String): String {
    // Write your code here
    val set = s.lowercase().filter {
        it.isLetter()
    }.groupBy { it }

    return if (set.count() == 26) "pangram" else "not pangram"
}

fun printFunction(name: String, function: () -> Unit) {
    println("---$name---")
    function()
}