package com.akhil.myexpensemanager.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akhil.myexpensemanager.AppConstants
import com.akhil.myexpensemanager.R
import com.akhil.myexpensemanager.models.ModelClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.content_expense.*

class EIActivity : AppCompatActivity() {

    private var expenseList:MutableList<ModelClass.EIModel> = mutableListOf()
    lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var madapter: ExpenseListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense)
        setSupportActionBar(findViewById(R.id.toolbar))

        val accountId = intent.extras?.get("account_id").toString()
        Log.e("akhillll",accountId)

        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            val intent = Intent(this, AddExpanseIncomeActivity::class.java)
            intent.putExtra("account_id",accountId)
            startActivity(intent)
        }

        madapter = ExpenseListAdapter(this, expenseList)

        rv_expenses.apply {
            layoutManager = LinearLayoutManager(this@EIActivity)
            adapter = madapter
        }

/*
        if(madapter.itemCount<=0){
            rv_expenses.visibility = View.GONE
            tv_no_expenses.visibility = View.VISIBLE
        }else{
            rv_expenses.visibility = View.VISIBLE
            tv_no_expenses.visibility = View.GONE
        }

*/

        db.collection("users")
            .document(auth.currentUser?.email.toString())
            .collection("accounts")
            .document(accountId)
            .collection("Expenses")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e(AppConstants.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                expenseList.clear()

                if (snapshot != null) {
                    Log.e(AppConstants.TAG, "snapshot is not null")

                    val expenses = snapshot.documents
                    Log.e(AppConstants.TAG, "docs size: "+expenses.size)
                    for(i in expenses){

                        if(i.data!=null){

                            val toObj = i.toObject(ModelClass.EIModel::class.java)

                            if (toObj != null) {
                                Log.e(AppConstants.TAG, "docs size: "+i.get("title").toString())
                                expenseList.add(toObj)
                            }

                            /*expenseList.add(ModelClass.EIModel(i.data?.get("id").toString(),i.data?.get("desc").toString(),i.data?.get("type").toString(),i.data?.get("amount"),i.data?.get("isRecurring"),i.data?.get("timestamp").toString()))*/

                        }
                    }
                    madapter.notifyDataSetChanged()
                    if(madapter.itemCount<=0){
                        rv_expenses.visibility = View.GONE
                        tv_no_expenses.visibility = View.VISIBLE
                    }else{
                        rv_expenses.visibility = View.VISIBLE
                        tv_no_expenses.visibility = View.GONE
                    }
                } else {
                    Log.d(AppConstants.TAG, "Current data: null")
                }
            }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1212) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this,"Account added successfully", Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(madapter.itemCount<=0){
            rv_expenses.visibility = View.GONE
            tv_no_expenses.visibility = View.VISIBLE
        }else{
            rv_expenses.visibility = View.VISIBLE
            tv_no_expenses.visibility = View.GONE
        }
    }

    class ExpenseListAdapter(private val context: Context?, private val list: List<ModelClass.EIModel>) : RecyclerView.Adapter<ExpenseListAdapter.ExpenseViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {

            val inflater = LayoutInflater.from(parent.context)

            return ExpenseViewHolder(
                inflater,
                parent
            )

        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {

            val expense: ModelClass.EIModel = list[position]
            holder.bind(expense)
            holder.itemView.setOnClickListener {
                val intent = Intent(context, EIActivity::class.java)
                //intent.putExtra("account_name",account.name)
                context?.startActivity(intent)
            }
        }

        class ExpenseViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
            RecyclerView.ViewHolder(
                inflater.inflate(
                    R.layout.accounts_list_item, parent, false
                )
            ) {
            private var mTitleView: TextView? = null

            init {
                mTitleView = itemView.findViewById(R.id.tv_account_name)
            }

            fun bind(ei: ModelClass.EIModel) {
                mTitleView?.text = ei.type+": "+ei.sub_type+" - "+ei.amount+"/-"
            }

        }
    }
}