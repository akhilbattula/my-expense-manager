package com.akhil.myexpensemanager.fragments

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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akhil.myexpensemanager.AppConstants.Companion.TAG
import com.akhil.myexpensemanager.R
import com.akhil.myexpensemanager.activities.AddAccountActivity
import com.akhil.myexpensemanager.activities.EIActivity
import com.akhil.myexpensemanager.models.ModelClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_accounts.*
import kotlinx.android.synthetic.main.fragment_accounts.rv_accounts

class AccountsFragment : Fragment() {

    private var accountsList:MutableList<ModelClass.Account> = mutableListOf()
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var madapter: AccountsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_accounts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        madapter = AccountsListAdapter(context,accountsList)
        rv_accounts.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = madapter
        }

        btn_add_new_account.setOnClickListener {
            val intent = Intent(activity, AddAccountActivity::class.java)
            startActivityForResult(intent,1212)
        }

        rv_accounts.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = madapter
        }
        /*if(madapter.itemCount<=0){
            rv_accounts.visibility = View.GONE
            tv_no_accounts.visibility = View.VISIBLE
        }else{
            rv_accounts.visibility = View.VISIBLE
            tv_no_accounts.visibility = View.GONE

        }*/

        db.collection("users")
            .document(auth.currentUser?.email.toString())
            .collection("accounts")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    Log.e(TAG, "snapshot is not null")
                    accountsList.clear()

                    val accounts = snapshot.documents
                    Log.e(TAG, "docs size: "+accounts.size)
                    for(i in accounts){

                        if(i.data!=null){

                            val toObj = i.toObject(ModelClass.Account::class.java)

                            if (toObj != null) {

                                Log.e(TAG, "docs size: "+i.get("title").toString())
                                accountsList.add(toObj)

                            }

                        }
                    }
                    madapter.notifyDataSetChanged()
                    if(accountsList.size<=0){
                        rv_accounts.visibility = View.GONE
                        tv_no_accounts.visibility = View.VISIBLE
                    }else{
                        rv_accounts.visibility = View.VISIBLE
                        tv_no_accounts.visibility = View.GONE
                    }
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1212) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(activity,"Account added successfully", Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(madapter.itemCount<=0){
            rv_accounts.visibility = View.GONE
            tv_no_accounts.visibility = View.VISIBLE
        }else{
            rv_accounts.visibility = View.VISIBLE
            tv_no_accounts.visibility = View.GONE
        }

    }
    
    class AccountsListAdapter(private val context: Context?, private val list: List<ModelClass.Account>) : RecyclerView.Adapter<AccountsListAdapter.AccountsViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountsViewHolder {

            val inflater = LayoutInflater.from(parent.context)

            return AccountsViewHolder(
                inflater,
                parent
            )

        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: AccountsViewHolder, position: Int) {

            val account: ModelClass.Account = list[position]
            holder.bind(account)
            holder.itemView.setOnClickListener {
                Log.e("akhilll",account?.id)
                val intent = Intent(context, EIActivity::class.java)
                intent.putExtra("account_id",account.id)
                context?.startActivity(intent)
            }
        }

        class AccountsViewHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(
            R.layout.accounts_list_item, parent, false)){
            private var mTitleView: TextView? = null

            init {
                mTitleView = itemView.findViewById(R.id.tv_account_name)
            }

            fun bind(account: ModelClass.Account) {
                mTitleView?.text = account.name
            }

        }

    }


}
