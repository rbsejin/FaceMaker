package com.example.facemaker.game

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.facemaker.R
import timber.log.Timber
import java.util.*

const val LEFT = 1
const val RIGHT = 2

class Game(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    var screenWidth = 200
    var screenHeight = screenWidth * 2

    private val paint = Paint()
    private var game: GameThread? = null

    var playerX: Float = screenWidth / 2.0f
    var playerY: Float = screenHeight / 2.0f
    var isMoveButtonClicked: Boolean = false
    var stopDirection: Int = 0
    var moveDirection: Int = 0
    var moveCount = 0

    var playerWidth = 100
    var playerHeight = 100

    var shitX = 100
    var shitY = 100

    val shitList = mutableListOf<Shit>()

    val random = Random()
    var cnt = 0
    var score = 0

    var run = false
    var isOver = false

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        screenWidth = w
        screenHeight = h

        playerX = screenWidth / 2.0f

        if (game == null) {
            game = GameThread().also {
                it.start()
            }

            handler.sendEmptyMessage(0)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        paint.color = Color.WHITE
        paint.textSize = screenHeight / 16.0f

        canvas?.apply {
            val player = BitmapFactory.decodeResource(resources, R.drawable.player)
            playerY = screenHeight * 3 / 4 - player.height.toFloat()
            drawBitmap(player, playerX, playerY.toFloat(), null)
            Timber.i(playerX.toString())

            val shitBitmap = BitmapFactory.decodeResource(resources, R.drawable.shit)
            playerWidth = player.width
            playerHeight = player.height
            for (shit in shitList) {
                drawBitmap(shitBitmap, shit.x.toFloat(), shit.y.toFloat(), null)
            }

            val buttonLeft = BitmapFactory.decodeResource(
                resources,
                R.drawable.baseline_keyboard_arrow_left_black_48dp
            )
            Bitmap.createScaledBitmap(buttonLeft, screenWidth / 8, screenHeight / 8, true)
            drawBitmap(buttonLeft, screenWidth / 4.0f - 100, screenHeight * 3 / 4.0f + 150, null)


            val buttonRight = BitmapFactory.decodeResource(
                resources,
                R.drawable.baseline_keyboard_arrow_right_black_48dp
            )
            Bitmap.createScaledBitmap(buttonRight, screenWidth / 8, screenHeight / 8, true)
            drawBitmap(buttonRight, screenWidth * 3 / 4.0f, screenHeight * 3 / 4.0f + 150, null)

            paint.strokeWidth = 5.0f
            drawLine(
                0.0f,
                screenHeight * 3 / 4.0f,
                screenWidth.toFloat(),
                screenHeight * 3 / 4.0f,
                paint
            )

            drawText("SCORE: $score", 100.0f, 200.0f, paint)

            if (isOver) {
                drawText("GAME OVER!", 150.0f, screenHeight / 3.0f, paint)
            }

            if (!run) {
                drawText("시작하려면", 150.0f, screenHeight / 3.0f + 200, paint)
                drawText("화면 터치", 150.0f, screenHeight / 3.0f + 400, paint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let { event ->
            //만약 회면을 터치했다면
            if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN
                || event.action == MotionEvent.ACTION_POINTER_DOWN
            ) {
                if (event.x > screenWidth / 2.0f && event.y > screenHeight * 3 / 4) {
                    if (!isMoveButtonClicked && moveCount == 0) {
                        isMoveButtonClicked = true
                        stopDirection = LEFT
                    }

                    moveDirection = LEFT

                } else if (event.x < screenWidth / 2.0f && event.y > screenHeight * 3 / 4) {
                    if (!isMoveButtonClicked && moveCount == 0) {
                        isMoveButtonClicked = true
                        stopDirection = RIGHT
                    }

                    moveDirection = RIGHT
                } else {
                    if (!run) {
                        if (isOver) {
                            reset()
                        }
                        run = true
                        game = GameThread().also {
                            it.start()
                        }

                        handler.sendEmptyMessage(0)
                    }
                }
            }

            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_POINTER_UP) {
                if (event.x > screenWidth / 2.0f) {
                    isMoveButtonClicked = false
                } else {
                    isMoveButtonClicked = false
                }
            }

        }
        return true
    }

    override fun onDetachedFromWindow() {
        run = false

        super.onDetachedFromWindow()
    }

    fun reset() {
        run = true
        isOver = false
        shitList.clear()
        score = 0

        isMoveButtonClicked = false
        stopDirection = 0
        moveDirection = 0
        moveCount = 0
        cnt = 0
    }

    private var handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if (isMoveButtonClicked) {
                if (moveDirection == LEFT) {
                    playerX += 40
                    if (playerX > screenWidth - playerWidth) {
                        playerX = screenWidth - playerWidth.toFloat()
                    }

                } else if (moveDirection == RIGHT) {
                    playerX -= 40
                    if (playerX < 0) {
                        playerX = 0.0f
                    }
                }
            }

            if (cnt % 6 == 0) {
                val shit =
                    Shit(
                        random.nextInt(screenWidth - 80),
                        random.nextInt(20).toInt() + 30
                    )
                shitList.add(shit)

                val shit2 =
                    Shit(
                        random.nextInt(screenWidth - 80),
                        random.nextInt(20).toInt() + 30
                    )
                shitList.add(shit2)
            }

            ++cnt

            // 똥 이동
            val removeShitList = mutableListOf<Shit>()

            for (shit in shitList) {
                shit.y += shit.speed

                val shitLeft = shit.x
                val shitRight = shit.x + 60.0f
                val shitTop = shit.y
                val shitBottom = shit.y + 60.0f

                if (shitRight > playerX + 20 && shitLeft < playerX + playerWidth - 20
                    && shitBottom > playerY + 20 && shitTop < playerY + playerHeight - 40
                ) {
                    isOver = true
                    run = false
                }

                if (shit.y > screenHeight * 3 / 4) {
                    removeShitList.add(shit)
                }
            }

            score += removeShitList.size
            shitList.removeAll(removeShitList)
        }
    }

    companion object {
        const val ROW_COUNT = 20
        const val COL_COUNT = 10
    }

    inner class GameThread() : Thread() {
        override fun run() {

            while (run) {
                try {
                    // 뷰에서 이미지를 분리
                    postInvalidate()

                    sleep(100)
                    handler.sendEmptyMessage(0)
                } catch (e: Exception) {
                    Timber.e("GameThread 예외 발생")
                }
            }
        }
    }
}

class Shit(var x: Int, val speed: Int) {
    var y: Int = 100
}
