package com.example.a7minuteworkout

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.sevenminuteworkout.Constants
import com.sevenminuteworkout.ExerciseModel
import kotlinx.android.synthetic.main.activity_exercise.*

class ExerciseActivity : AppCompatActivity() {

    private var restTimer: CountDownTimer? = null
    private var restProgress = 0

    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress = 0
    private var exerciseDuration: Long = 30

    private var exerciselList: ArrayList<ExerciseModel>? = null
    private var exerciseCurrentPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)
        setSupportActionBar(toolbar_exercise_activity)
        val actionBar = supportActionBar
        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        toolbar_exercise_activity.setNavigationOnClickListener{
            onBackPressed()
        }
        exerciselList = Constants.defaultExerciseList()
        setupRestView()
    }

    override fun onDestroy() {
        if(restTimer!=null) {
            restTimer!!.cancel()
            restProgress=0
        }
        super.onDestroy()
    }

    private fun setRestProgressBar() {
        progressBar.progress = restProgress
        restTimer = object:CountDownTimer(10000,1000) {
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                progressBar.progress = 10-restProgress
                tvTimer.text = (10-restProgress).toString()
            }

            override fun onFinish() {
                exerciseCurrentPosition++
                setupExerciseView()
            }
        }.start()

    }

    private fun setupRestView() {
        llRestView.visibility = View.VISIBLE
        llExerciseView.visibility = View.GONE
        if(restTimer!=null) {
            restTimer!!.cancel()
            restProgress=0
        }
        setRestProgressBar()
        tvUpcomingExerciseName.text = exerciselList!![exerciseCurrentPosition+1].getName()
    }

    private fun setExerciseProgressBar() {
        progressBarExercise.progress = exerciseProgress
        exerciseTimer = object:CountDownTimer(exerciseDuration*1000,1000) {
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                progressBarExercise.progress = exerciseDuration.toInt()-exerciseProgress
                tvExerciseTimer.text = (exerciseDuration-exerciseProgress).toString()
            }

            override fun onFinish() {
                if(exerciseCurrentPosition<(exerciselList?.size!! - 1)) {
                    setupRestView()
                } else {
                    Toast.makeText(this@ExerciseActivity,"Workout finished",Toast.LENGTH_SHORT).show()
                }
            }
        }.start()

    }

    private fun setupExerciseView() {
        llRestView.visibility = View.GONE
        llExerciseView.visibility = View.VISIBLE
        if(exerciseTimer!=null) {
            exerciseTimer!!.cancel()
            exerciseProgress=0
        }
        setExerciseProgressBar()
        ivImage.setImageResource(exerciselList!![exerciseCurrentPosition].getImage())
        tvExerciseName.text = exerciselList!![exerciseCurrentPosition].getName()
    }

}