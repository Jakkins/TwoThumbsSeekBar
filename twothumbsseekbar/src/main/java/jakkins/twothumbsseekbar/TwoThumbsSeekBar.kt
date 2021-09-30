package jakkins.twothumbsseekbar

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

@SuppressLint("ClickableViewAccessibility")
class TwoThumbsSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes) {

    // private var seekMinll: LinearLayout
    // private var seekMaxll: LinearLayout
    private var seekMinImg: ImageView
    private var seekMaxImg: ImageView
    private var rangebarContainer: LinearLayout

    private var xDown = 0f
    private var yDown = 0f

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.two_thumbs_seekbar, this, true)

        orientation = VERTICAL

        seekMinImg = findViewById(R.id.left_thumb)
        seekMaxImg = findViewById(R.id.right_thumb)

        rangebarContainer = findViewById(R.id.rangebar_container)
        rangebarContainer.post {
            Runnable {

                val leftImageWidth = seekMinImg.drawable.intrinsicWidth
                val rightImageWidth = seekMaxImg.drawable.intrinsicWidth

                Log.i("logPos", "leftImageWidth: $leftImageWidth")
                Log.i("logPos", "rightImageWidth: $rightImageWidth")

                val MIN_X: Float = 0.0F
                val MAX_X: Float =
                    (rangebarContainer.width - rightImageWidth).toFloat()

                var seekMINCollisionPosX: Float? = MIN_X + leftImageWidth
                var seekMAXCollisionPosX: Float? = MAX_X

                /**
                 * MIN
                 *
                 * ruddy top-left corner
                 */
                seekMinImg.setOnTouchListener(object : OnTouchListener {
                    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                        logPos(view!!, event!!)
                        findViewById<TextView>(R.id.tv_range_min).text = view.x.toString()
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                xDown = event.x
                                seekMinImg.bringToFront()
                                return true
                            }
                            /**
                             * ACTION_MOVE
                             *
                             * event.rawX != event.x
                             * seekMin.x === view.x
                             */
                            MotionEvent.ACTION_MOVE -> {
                                val moveX = event.x
                                val distanceX = moveX - xDown
                                var tempPos = seekMinImg.x + distanceX
                                when {
                                    (tempPos + leftImageWidth > seekMAXCollisionPosX!!) -> {
                                        seekMinImg.x = seekMAXCollisionPosX!! - leftImageWidth
                                    }
                                    (tempPos < MIN_X) -> seekMinImg.x = MIN_X
                                    else -> seekMinImg.x = tempPos
                                }
                                seekMINCollisionPosX = seekMinImg.x + leftImageWidth
                                return true
                            }
                            else -> return false
                        }
                    }
                })

                /**
                 * MAX
                 */
                seekMaxImg.setOnTouchListener(object : OnTouchListener {
                    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                        logPos(view!!, event!!)
                        findViewById<TextView>(R.id.tv_range_max).text = view!!.x.toString()
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                xDown = event.x
                                yDown = event.y
                                seekMaxImg.bringToFront()
                                return true
                            }
                            /**
                             * ACTION_MOVE
                             */
                            MotionEvent.ACTION_MOVE -> {
                                val moveX = event.x
                                val distanceX = moveX - xDown
                                var tempPos = seekMaxImg.x + distanceX
                                when {
                                    tempPos < seekMINCollisionPosX!! -> {
                                        seekMaxImg.x = seekMINCollisionPosX!!
                                    }
                                    tempPos > MAX_X -> seekMaxImg.x = MAX_X
                                    else -> seekMaxImg.x = tempPos
                                }
                                seekMAXCollisionPosX = seekMaxImg.x
                                return true
                            }
                            else -> return false
                        }
                    }
                })
            }.run()
        }
    }

    private fun logPos(view: View, event: MotionEvent) {
        Log.i("logPos", "\n------------------------")
        Log.i("logPos", "view x: " + view.x)
        Log.i("logPos", "event rawX: " + event.rawX)

        val offset = IntArray(2)
        view.getLocationOnScreen(offset)
        val Xoffset = offset[0]
        val Yoffset = offset[1]
        Log.i("logPos", "Xoffset: $Xoffset")
    }
}

