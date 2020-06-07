package com.akhil.myexpensemanager.models

import com.google.firebase.Timestamp
import java.util.*

class ModelClass {

    data class User(var fname:String, var lname:String, var phoneNumber:String, var email:String)

    data class Account(var id: String? = null,var name: String? = null,var desc: String? = null,var status: String? = null,var addedTimestamp: String? = null,var modifiedTimestamp: String? = null)

    data class EIModel(var id: String? = null,/*var title: String? = null,*/var desc: String? = null,var type: String? = null,var sub_type: String? = null,var amount: Float? = null,val isRecurring: Boolean? = null, var timestamp: String? = null)

    data class Income(var id: String? = null,var title: String? = null,var desc: String? = null,var type: String? = null,var amount: Float? = null,val isRecurring: Boolean? = null, var timestamp: String? = null)

}