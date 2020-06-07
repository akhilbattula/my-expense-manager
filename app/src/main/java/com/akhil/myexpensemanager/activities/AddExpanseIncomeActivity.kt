package com.akhil.myexpensemanager.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.akhil.myexpensemanager.AppConstants
import com.akhil.myexpensemanager.R
import com.akhil.myexpensemanager.models.ModelClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_expanse_income.*
import java.lang.Float.parseFloat
import java.lang.NumberFormatException
import java.text.SimpleDateFormat
import java.util.*


class AddExpanseIncomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expanse_income)

        val accountId = intent.extras?.get("account_id").toString()

        Log.e("akhillll",accountId)

        ArrayAdapter.createFromResource(this,R.array.expense_array,android.R.layout.simple_spinner_item)
            .also { adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sp_subtype.adapter = adapter
        }

        rg_type.setOnCheckedChangeListener { group, checkedId ->

            val radio: RadioButton = findViewById(checkedId)

            if(radio.text.toString() == "Expense"){
                ArrayAdapter.createFromResource(this,R.array.expense_array,android.R.layout.simple_spinner_item)
                    .also { adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        sp_subtype.adapter = adapter
                    }

            }else{
                ArrayAdapter.createFromResource(this,R.array.income_array,android.R.layout.simple_spinner_item)
                    .also { adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        sp_subtype.adapter = adapter
                    }

            }

        }

        val myCalendar = Calendar.getInstance();
        val date = OnDateSetListener { view, year, monthOfYear, dayOfMonth -> // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd-MM-yyyy"

            val sdf = SimpleDateFormat(myFormat, Locale.US)

            et_date.setText(sdf.format(myCalendar.time))

        }

        et_date.setOnClickListener {
            DatePickerDialog(this, date, myCalendar[Calendar.YEAR], myCalendar[Calendar.MONTH], myCalendar[Calendar.DAY_OF_MONTH]).show()
        }

        et_date.setOnFocusChangeListener { v, hasFocus ->

            if(hasFocus){
                DatePickerDialog(this, date, myCalendar[Calendar.YEAR], myCalendar[Calendar.MONTH], myCalendar[Calendar.DAY_OF_MONTH]).show()
            }

        }

        btn_add_ei.setOnClickListener {
            if(sp_subtype.selectedItemPosition==0){
                if(findViewById<RadioButton>(rg_type.checkedRadioButtonId).text == "Expense"){
                    Toast.makeText(this,"Please select an Expense type", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this,"Please select an Income type", Toast.LENGTH_SHORT).show()
                }
            }else if(et_description.text.isEmpty()){
                Toast.makeText(this,"Please enter description", Toast.LENGTH_SHORT).show()

            }else if(et_amount.text.isEmpty()){
                Toast.makeText(this,"Please enter Expense/Income amount", Toast.LENGTH_SHORT).show()
            }else{

                try{

                    val amount = parseFloat(et_amount.text.toString())

                    val date= Date()
                    Log.w("akhilll",""+date)
                    Log.w("akhilll","Email: "+FirebaseAuth.getInstance().currentUser?.email.toString())
                    val email: String? = getSharedPreferences(AppConstants.sharedPrefName, Context.MODE_PRIVATE).getString("email","abc@gmail.com")
                    val id = email?.substring(0,email.indexOf("@"))+"-expense-"+date.time
                    Log.w("akhilll","id: "+id)

                    val account = ModelClass.EIModel(id,et_description.text.toString(),findViewById<RadioButton>(rg_type.checkedRadioButtonId).text.toString(),sp_subtype.selectedItem.toString(),amount,false,date.toString())
                    FirebaseFirestore.getInstance().collection("users")
                        .document(FirebaseAuth.getInstance().currentUser?.email.toString())
                        .collection("accounts")
                        .document(accountId)
                        .collection("Expenses")
                        .document(id)
                        .set(account)
                        .addOnSuccessListener {
                            Log.d(AppConstants.TAG, "DocumentSnapshot added")
                            val intent = Intent()
                            setResult(Activity.RESULT_OK,intent)
                            finish()
                        }
                        .addOnFailureListener {
                            Log.e(AppConstants.TAG, "Error adding document")
                        }

                }catch (e: NumberFormatException){
                    Toast.makeText(this,"Please enter valid Expense/Income amount", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }
}