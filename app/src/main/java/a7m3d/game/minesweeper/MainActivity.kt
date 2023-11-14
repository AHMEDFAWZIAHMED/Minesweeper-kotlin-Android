package a7m3d.game.minesweeper

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import kotlin.random.Random
import kotlin.random.nextInt

class MainActivity : AppCompatActivity() {

    private val silver = Color.parseColor("silver")
    private val squares = arrayListOf<AppCompatImageView>()
    private val squaresID = listOf(
        R.id.square11, R.id.square12, R.id.square13, R.id.square14, R.id.square15,
        R.id.square21, R.id.square22, R.id.square23, R.id.square24, R.id.square25,
        R.id.square31, R.id.square32, R.id.square33, R.id.square34, R.id.square35,
        R.id.square41, R.id.square42, R.id.square43, R.id.square44, R.id.square45,
        R.id.square51, R.id.square52, R.id.square53, R.id.square54, R.id.square55
    )
    private val numbersID = listOf(
        R.drawable.zero_bomb, R.drawable.number_1, R.drawable.number_2, R.drawable.number_3,
        R.drawable.number_4, R.drawable.number_5
        )
    private lateinit var hiddenBomb: List<Int>
    private val allIndex = arrayListOf(*Array(25) {it*1})
    private val colIndex = (0..4).map {col -> (0..4).map {it*5+col}}
    private val revealedSpot = arrayListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        for (i in squaresID.indices) {
            val square = findViewById<AppCompatImageView>(squaresID[i])
            square.setBackgroundColor(silver)
            square.setOnClickListener { checkSpot(i) }
            squares.add(square)
        }
    }

    private fun checkSpot(indx: Int) {
        if (revealedSpot.isEmpty()) plantBombs(indx)
        revealedSpot += indx
        if (indx in hiddenBomb) {
            gameOver(indx, "Good luck next time")
            return
        }
        var numberOfBomb = 0
        for (n in findNeighbors(indx)) {
            if (n in hiddenBomb) numberOfBomb++
        }

        squares[indx].setImageResource(numbersID[numberOfBomb])
        squares[indx].setBackgroundColor(silver)
        squares[indx].isClickable = false
        if (revealedSpot.size == 20) {
            gameOver( result = "You got it!")
            return
        }
        if (numberOfBomb == 0) {
            for (n in findNeighbors(indx)) {
                if (n !in revealedSpot) checkSpot(n)
            }
        }
    }

    private fun findNeighbors(number: Int): ArrayList<Int> {
        val allInd = listOf(-1, 1, -4, 4, -5, 5, -6, 6).map {it+number}
        val firstCo = listOf(1, -4, -5, 5, 6).map {it+number}
        val lastCo = listOf(-1, 4, -5, 5, -6).map {it+number}
        val nearNeighbors = arrayListOf<Int>()

        if (number in colIndex[0]) {
            for (f in firstCo) if (f in allIndex) nearNeighbors += f
        }
        else if (number in colIndex[4]) {
            for (l in lastCo) if (l in allIndex) nearNeighbors += l
        }
        else {
            for (a in allInd) if (a in allIndex) nearNeighbors += a
        }
        return nearNeighbors
    }

    private fun plantBombs(number: Int) {
        hiddenBomb = generateSequence {
            Random.nextInt(0..24)
        }.distinct().filterNot { it == number }.take(5).toList()
    }

    private fun gameOver(indx: Int = -1, result: String) {
        for (square in squares) square.isClickable = false
        for (h in hiddenBomb) {
            squares[h].setImageResource(R.drawable.bomb)
            squares[h].setBackgroundColor(silver)
        }
        var delay = (1000).toLong()
        if (indx > -1) {
            squares[indx].setImageResource(R.drawable.bomb)
            squares[indx].setBackgroundColor(silver)
            for (i in hiddenBomb.indices) {
                Handler(Looper.getMainLooper()).postDelayed({
                    squares[hiddenBomb[i]].setImageResource(R.drawable.explosion)
                    squares[hiddenBomb[i]].setBackgroundColor(silver)
                }, (500*(i+1)).toLong())
            }
            delay = (3000).toLong()
        }
        Handler(Looper.getMainLooper()).postDelayed({
            val dialog = AlertDialog.Builder(this)
            dialog.setCancelable(false)
            dialog.setTitle(result)
            dialog.setNeutralButton("Restart") { _, _ -> recreate() }
            dialog.setPositiveButton("Exit") { _, _ -> finish() }
            val alertDialog = dialog.create()
            val dWindow = alertDialog.window
            val wlp = dWindow?.attributes
            wlp?.gravity = Gravity.BOTTOM
            alertDialog.show()
        }, delay)
    }
}