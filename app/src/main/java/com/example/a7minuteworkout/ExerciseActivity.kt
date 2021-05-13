package com.example.a7minuteworkout

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.sevenminuteworkout.Constants
import com.sevenminuteworkout.ExerciseModel
import kotlinx.android.synthetic.main.activity_exercise.*
import kotlinx.android.synthetic.main.dialog_custom_back_confirmation.*
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var restTimer: CountDownTimer? = null
    private var restProgress = 0
    private var restTimerDuration: Long = 10 //10

    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress = 0
    private var exerciseDuration: Long = 30 //30

    private var exerciselList: ArrayList<ExerciseModel>? = null
    private var exerciseCurrentPosition = -1

    private var tts: TextToSpeech? = null
    private var player: MediaPlayer? = null

    private var exerciseAdapter: ExerciseStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)
        setSupportActionBar(toolbar_exercise_activity)
        val actionBar = supportActionBar
        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        toolbar_exercise_activity.setNavigationOnClickListener{
            customDialogForBackButton()
        }
        tts = TextToSpeech(this,this)
        exerciselList = Constants.defaultExerciseList()
        setupRestView()
        setupExerciseStatusRecyclerView()
    }

    override fun onDestroy() {
        if(restTimer!=null) {
            restTimer!!.cancel()
            restProgress=0
        }
        if(exerciseTimer!=null) {
            exerciseTimer!!.cancel()
            exerciseProgress=0
        }
        if(tts!=null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        if(player!=null) {
            player!!.stop()
        }
        super.onDestroy()
    }

    private fun setRestProgressBar() {
        progressBar.progress = restProgress
        restTimer = object:CountDownTimer(restTimerDuration*1000,1000) {
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                progressBar.progress = 10-restProgress
                tvTimer.text = (10-restProgress).toString()
            }

            override fun onFinish() {
                exerciseCurrentPosition++
                exerciselList!![exerciseCurrentPosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()
                setupExerciseView()
            }
        }.start()

    }

    private fun setupRestView() {
        llRestView.visibility = View.VISIBLE
        llExerciseView.visibility = View.GONE
        try {
            player = MediaPlayer.create(this,R.raw.press_start)
            player!!.isLooping = false
            player!!.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
                    exerciselList!![exerciseCurrentPosition].setIsSelected(false)
                    exerciselList!![exerciseCurrentPosition].setIsCompleted(true)
                    exerciseAdapter!!.notifyDataSetChanged()
                    setupRestView()
                } else {
                    finish()
                    val intent = Intent(this@ExerciseActivity,FinishActivity::class.java)
                    startActivity(intent)
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
        speak(exerciselList!![exerciseCurrentPosition].getName())
        setExerciseProgressBar()
        ivImage.setImageResource(exerciselList!![exerciseCurrentPosition].getImage())
        tvExerciseName.text = exerciselList!![exerciseCurrentPosition].getName()
    }

    private fun speak(text:String) {
        tts!!.speak(text,TextToSpeech.QUEUE_FLUSH,null,"")
    }

    override fun onInit(status: Int) {
        if(status==TextToSpeech.SUCCESS) {
            val res = tts!!.setLanguage(Locale.US)
            if(res==TextToSpeech.LANG_MISSING_DATA || res==TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("tts","The specified language is not supported")
            }
        } else {
            Log.e("tts","Text to Speech not initialization failed")
        }
    }

    private fun setupExerciseStatusRecyclerView() {
        rvExerciseStatus.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        exerciseAdapter = ExerciseStatusAdapter(exerciselList!!,this)
        rvExerciseStatus.adapter = exerciseAdapter
    }

    private fun customDialogForBackButton() {
        val customDialog = Dialog(this)
        customDialog.setContentView(R.layout.dialog_custom_back_confirmation)
        customDialog.tvYes.setOnClickListener{
           finish()
           customDialog.dismiss()
        }
        customDialog.tvNo.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }

}