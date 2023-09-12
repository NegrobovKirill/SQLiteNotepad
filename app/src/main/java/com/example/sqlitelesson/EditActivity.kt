package com.example.sqlitelesson

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.sqlitelesson.DB.MyDbManager
import com.example.sqlitelesson.DB.MyIntentConstants
import com.example.sqlitelesson.databinding.ActivityEditBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditActivity : AppCompatActivity() {

    var id = 0
    var isEditState = false
    lateinit var binding: ActivityEditBinding
    val myDbManager = MyDbManager(this )

    val editLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()){


        Picasso.get().load(it).into(binding.imageView)
        //Glide.with(this).load(it).into(binding.imageView)
        tempImageUri = it.toString()
        this.contentResolver.takePersistableUriPermission(it!!,Intent.FLAG_GRANT_READ_URI_PERMISSION )


    }

    var tempImageUri = "empty"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getMyIntents()

    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()

    }

    fun onClickAddImage(view: View) {
        binding.mainImageLayout.visibility = View.VISIBLE
        binding.bAddImage.visibility = View.GONE
    }
    fun onClickDeleteImage(view: View) {
        binding.mainImageLayout.visibility = View.GONE
        binding.bAddImage.visibility = View.VISIBLE
        tempImageUri = "empty"
    }

    fun omClickChooseImage(view: View){

        editLauncher.launch(arrayOf("image/*"))

    }

    fun onClickSave(view: View){

        val myTitle = binding.edTitle.text.toString()
        val myContent = binding.edContent.text.toString()
        if(myTitle != "" && myContent != ""){
            CoroutineScope(Dispatchers.Main).launch {
                if (isEditState){
                    myDbManager.updateItem(myTitle,myContent,tempImageUri,id,getCurrentTime())
                }else {
                    myDbManager.insertToDb(myTitle, myContent, tempImageUri,getCurrentTime())
                }
                finish()
            }

        }

    }
    fun onEditEnable(view: View){
        binding.edTitle.isEnabled = true
        binding.edContent.isEnabled = true
        binding.editAll.visibility = View.GONE
        binding.bAddImage.visibility = View.VISIBLE
        if (tempImageUri == "empty")return
        binding.bEditImage.visibility = View.VISIBLE
        binding.bDeleteImage.visibility = View.VISIBLE
    }

    fun getMyIntents(){
        binding.editAll.visibility = View.GONE
        val i = intent

        if(i != null){
            if(i.getStringExtra(MyIntentConstants.I_TITLE_KEY) != null){
                binding.bAddImage.visibility = View.GONE
                binding.mainImageLayout.visibility = View.GONE
                isEditState = true
                binding.edTitle.isEnabled = false
                binding.edContent.isEnabled = false
                binding.editAll.visibility = View.VISIBLE
                binding.edTitle.setText(i.getStringExtra(MyIntentConstants.I_TITLE_KEY))
                binding.edContent.setText(i.getStringExtra(MyIntentConstants.I_CONTENT_KEY))
                id = i.getIntExtra(MyIntentConstants.I_ID_KEY,0)
                tempImageUri = i.getStringExtra(MyIntentConstants.I_URI_KEY)!!

                if(tempImageUri != "empty"){
                    binding.mainImageLayout.visibility = View.VISIBLE
                    binding.bDeleteImage.visibility = View.GONE
                    binding.bEditImage.visibility = View.GONE

                    Picasso.get().load(tempImageUri).into(binding.imageView)
                    //Glide.with(this).load(tempImageUri).into(binding.imageView)




                }
            }
        }
    }
    private fun getCurrentTime():String{
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yy hh:mm", Locale.getDefault())
        return formatter.format(time)
    }
}