package com.coolkids.todo.getTogether


import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.NoCache
import org.json.JSONObject
import java.util.*


/**
 * Handles the interactions with the server
 */

class ServerHandler private constructor() {
    var cache: Cache = NoCache()
    var network: Network = BasicNetwork(HurlStack())
    internal var queue: RequestQueue = RequestQueue(cache, network)
    internal var server: String = "https://hbptodoapp.herokuapp.com"
    internal var port: Int = 80
    internal var uname: String = ""
    internal var pass: String = ""

    init {
        queue.start()
    }

    fun saveCredentials(username: String, password: String){
        Log.d("Saved credentials ", username)
        this.uname = username
        this.pass= password
    }

    fun addCredentials(m: Map<String, String?>): Map<String, String?> {
        var n = HashMap(m)
        n["uname"] = uname
        n["password"] = pass
        return n
    }

    fun makeObjRequest(method:Int, url: String, params: Map<String, String?>,
                       callback: (JSONObject) -> Unit, errCallback: (VolleyError?)->Unit) {
        val request = JsonObjectRequest(
                Request.Method.POST, server + url, JSONObject(params),
                object: Response.Listener<JSONObject>{
                    override fun onResponse(response: JSONObject) {
                        callback(response)
                    }
                },
                object:Response.ErrorListener{
                    override fun onErrorResponse(error: VolleyError?) {
                        Log.e("Network Requests: ",error.toString())
                        errCallback(error)
                    }
                });

        queue.add(request)
    }


    fun validateCredentials(username: String, password: String,
                            callback: (AppUser) -> Unit, errCallback: (VolleyError?)->Unit = {_->}) {
        makeObjRequest(Request.Method.POST,"/users/current", mapOf("uname" to username, "password" to password),
                {resp->callback(AppUser(resp))}, errCallback);
    }

    fun newUser(firstName: String, lastName: String, username: String,
                email: String, password: String,
                callback: (AppUser) -> Unit, errCallback: (VolleyError?)->Unit = {_->}) {
        val req = HashMap<String, String>()
        req["firstName"] = firstName
        req["lastName"] = lastName
        req["uname"] = username
        req["email"] = email
        req["password"] = password

        val jsArrToEvents = {jsobj: JSONObject ->
            callback(AppUser(jsobj))
        }

        makeObjRequest(
                Request.Method.POST,"/users", req,
                jsArrToEvents, errCallback);
    }

    fun fetchEvents(callback:(ArrayList<PlannedEvent>) -> Unit,  errCallback: (VolleyError?)->Unit = {_->}) {
        val jsArrToEvents = { jsobj: JSONObject ->
            val jsarr = jsobj.getJSONArray("events");
            var ret: ArrayList<PlannedEvent> = ArrayList<PlannedEvent>()
            for (i in 0..jsarr.length() - 1) {
                ret.add(PlannedEvent(jsarr.getJSONObject(i)))
            }
            callback(ret)
        }
        makeObjRequest(Request.Method.POST,"/events/all", addCredentials(HashMap<String,String>()),jsArrToEvents,errCallback);
    }

    fun createNewEvent(title:String, date:String, description:String, location:String,
                       callBack:(PlannedEvent) -> Unit, errCallback: (VolleyError?)->Unit = {_->}){
        makeObjRequest(Request.Method.POST,"/events",
                addCredentials(mapOf("title" to title, "description" to description, "date" to date, "location" to location)),
                {jsobj->callBack(PlannedEvent(jsobj))},errCallback)
    }

    fun fetchOneEvent(eventId:String, callback: (PlannedEvent)->Unit, errCallback: (VolleyError?)->Unit = {_->}){
        makeObjRequest(Request.Method.POST, "/events/"+eventId, addCredentials(HashMap()),
                {jsobj->callback(PlannedEvent(jsobj))},errCallback)
    }

    fun joinEvent(eventId:String, callback: (PlannedEvent)->Unit, errCallback: (VolleyError?) -> Unit = { _->}){
        makeObjRequest(Request.Method.POST, "/events/"+eventId+"/join", addCredentials(HashMap()),
                {jsobj->callback(PlannedEvent(jsobj))},errCallback)
    }

    fun updateEvent (eventId:String, title:String, date:String, description:String, location:String,
                     callBack:(PlannedEvent) -> Unit, errCallback: (VolleyError?)->Unit = {_->}){
        makeObjRequest(Request.Method.PUT,"/events/"+eventId,
                addCredentials(mapOf("title" to title, "description" to description, "date" to date, "location" to location)),
                {jsobj->callBack(PlannedEvent(jsobj))},errCallback)
    }

    fun deleteEvent (eventId: String,  callBack:(JSONObject) -> Unit, errCallback: (VolleyError?)->Unit = {_->}){
        makeObjRequest(Request.Method.DELETE, "/events/"+eventId, addCredentials(HashMap()),callBack,errCallback)
    }

    fun createTask (eventId: String, name: String, description: String, callBack:(ToDoTask) -> Unit, errCallback: (VolleyError?)->Unit = {_->}){
        makeObjRequest(Request.Method.POST,"/tasks",addCredentials(mapOf("eventId" to eventId, "name" to name, "description" to description)),
                {jsobj->callBack(ToDoTask(jsobj))},errCallback);
    }

    fun editTask (taskId: Int, name: String, description: String, callback: (ToDoTask) -> Unit, errCallback: (VolleyError?)->Unit = {_->}){
        makeObjRequest(Request.Method.PUT,"/tasks/"+taskId,addCredentials(mapOf("name" to name, "description" to description)),
                {jsobj->callback(ToDoTask(jsobj))},errCallback);
    }

    fun assignTask (taskId: Int, assignTo: String?, callback: (ToDoTask) -> Unit, errCallback: (VolleyError?)->Unit = {_->}){
        makeObjRequest(Request.Method.PUT,"/tasks/"+taskId,addCredentials(mapOf("assigned" to assignTo)),
                {jsobj->callback(ToDoTask(jsobj))},errCallback)
    }

    fun deleteTask (taskId: Int, callBack:(JSONObject) -> Unit, errCallback: (VolleyError?)->Unit = {_->}){
        makeObjRequest(Request.Method.DELETE,"/tasks/"+taskId, addCredentials(mapOf()),
                callBack,errCallback)
    }

    companion object {

        private var singleton: ServerHandler = ServerHandler()

        val serverHandler: ServerHandler
            get() {
                return singleton
            }
    }
}


// httpserver thing .get(address, params)
// post requests
// put requests