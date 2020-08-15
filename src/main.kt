import kotlin.concurrent.fixedRateTimer
import kotlin.math.pow

fun basic(rightNum:String?, leftNum:String?, op:String?):Double? {
    return when (op) {
        "+" -> {
            (rightNum?.toDouble()!! + leftNum?.toDouble()!!)
        }
        "-" -> {
            (rightNum?.toDouble()!! - leftNum?.toDouble()!!)
        }
        "*" -> {
            (rightNum?.toDouble()!! * leftNum?.toDouble()!!)
        }
        "^" -> {
            ((rightNum?.toDouble()!!).pow(leftNum?.toDouble()!!))
        }
        else -> {
            (rightNum?.toDouble()!! / leftNum?.toDouble()!!)
        }
    }
}


fun elemInside(mainString:String?, listCheck:List<String>):Boolean {
    for (ops in listCheck) {
        if (mainString?.contains(ops)!!){
            return true
        }
    }
    return false
}

fun getOpIndex(query: String?, operations:List<String>):Array<Int> {
    var allIndex:Array<Int> = arrayOf()
    var dupQuery = query
    while (elemInside(dupQuery, operations)) {
        for (op in operations) {
            if (dupQuery?.contains(op)!!) {
                allIndex = allIndex.plusElement(dupQuery.indexOf(op))
                dupQuery = dupQuery.substring(0, dupQuery.indexOf(op)) + '2' + dupQuery.substring(dupQuery.indexOf(op) + 1)
            }
        }
    }
    allIndex.sort()

    var afterRemoveIndex:Array<Int> = arrayOf()
    for (indexes in allIndex) {
        val beforeVal = indexes - 1
        if (!allIndex.contains(beforeVal) && indexes!=0) {
            afterRemoveIndex = afterRemoveIndex.plus(indexes)
        }
    }

    return afterRemoveIndex
}


fun isInside(query: String?, op: String?, allIndex:Array<Int>):Boolean {
    for (indexes in allIndex) {
        if (query?.get(indexes).toString() == op) {
            return true
        }
    }
    return false
}


fun parseSimple(query:String?):Double? {
    val operations = listOf("^", "/", "*", "-", "+")
    var allIndex: Array<Int> = arrayOf()

    var calcQuery = query
    for (op in operations) {
        var index = 0
        while (calcQuery?.contains(op)!! && (isInside(calcQuery, op, allIndex) || index == 0)) {
            calcQuery = calcQuery.replace("-+", "-")
            calcQuery = calcQuery.replace("--", "+")
            calcQuery = calcQuery.replace("+-", "-")
            allIndex = getOpIndex(calcQuery, operations)
            if (calcQuery.contains(op)) {
                val indexOp = calcQuery.indexOf(op)
                val indexIndexOp = allIndex.indexOf(indexOp)
                if (indexIndexOp != -1) {
                    val rightIndex =
                        if (indexIndexOp == allIndex.lastIndex) calcQuery.lastIndex else allIndex[indexIndexOp + 1]
                    val leftIndex = if (indexIndexOp == 0) 0 else allIndex[indexIndexOp - 1]
                    val rightNum =
                        calcQuery.slice(if (rightIndex == calcQuery.lastIndex) indexOp + 1..rightIndex else indexOp + 1 until rightIndex)
                    val leftNum =
                        calcQuery.slice(if (leftIndex == 0) leftIndex until indexOp else leftIndex + 1 until indexOp)
                    val result = basic(leftNum, rightNum, op)
                    calcQuery = (if (leftIndex != 0) calcQuery.substring(
                        0,
                        leftIndex + 1
                    ) else "") + result.toString() + (if (rightIndex != calcQuery.lastIndex) calcQuery.substring(
                        rightIndex..calcQuery.lastIndex
                    ) else "")
                }
            }
            index++
            allIndex = getOpIndex(calcQuery, operations)
        }
    }
    return calcQuery?.toDouble()
}

fun getAllIndex(query: String?, char: Char, replacement:String="%"):List<Int> {
    var myQuery = query
    var indexes:List<Int> = listOf()
    while (char in myQuery!!) {
        val indexFinded = myQuery.indexOf(char)
        indexes = indexes.plus(indexFinded)
        myQuery = myQuery.substring(0 until indexFinded) + replacement + myQuery.substring(indexFinded+1..myQuery.lastIndex)
    }
    return indexes
}

fun getBrackets(query: String?): List<Int> {
    val allEndIndex = getAllIndex(query, ')')
    val allStartIndex = getAllIndex(query, '(')
    val firstIndex = allStartIndex[0]
    for (endIndex in allEndIndex) {
        val inBrac = query?.substring(firstIndex+1 until endIndex)
        val inBracStart = getAllIndex(inBrac, '(')
        val inBracEnd = getAllIndex(inBrac, ')')
        if (inBracStart.size == inBracEnd.size){
            return listOf(firstIndex, endIndex)
        }
    }
    return listOf(-1, -1)
}

fun evaluate(query:String?):Double? {
    var calcQuery = query
    var index = 0;
    // Check if brackets are present
    while (calcQuery?.contains('(')!! && index < 200){
        val startBrackets = getBrackets(calcQuery)[0]
        val endBrackets = getBrackets(calcQuery)[1]
        val inBrackets = calcQuery.slice(startBrackets+1 until endBrackets)
        if ('(' in inBrackets && ')' in inBrackets){
            val inBracValue = evaluate(inBrackets)
            calcQuery = calcQuery.substring(0, startBrackets) + inBracValue.toString() + (if(endBrackets == calcQuery.lastIndex) "" else calcQuery.substring(endBrackets+1..calcQuery.lastIndex))
        }
        else {
            val inBracValue = parseSimple(inBrackets)
            calcQuery = calcQuery.substring(0, startBrackets) + inBracValue.toString() + (if(endBrackets == calcQuery.lastIndex) "" else calcQuery.substring(endBrackets+1..calcQuery.lastIndex))
        }
        index++
    }

    return parseSimple(calcQuery)
}


fun main() {
    print("Enter your expression: ")
    val query = readLine()
    println(evaluate(query))
}
