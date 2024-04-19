//package com.example.comp4521_group22
//
//import android.app.DatePickerDialog
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.text.method.ScrollingMovementMethod
//import android.util.Log
//import android.widget.Button
//import android.widget.CheckBox
//import android.widget.DatePicker
//import android.widget.EditText
//import android.widget.RadioButton
//import android.widget.RadioGroup
//import android.widget.TextView
//import java.io.IOException
//import com.google.gson.Gson
//import okhttp3.*
//import java.text.SimpleDateFormat
//import java.util.Calendar
//import java.util.Locale
//import java.util.concurrent.TimeUnit
//import kotlin.concurrent.thread
//
//
//class MainActivity : AppCompatActivity() {
//    val credentials = Credentials.basic("leohong", "92816881")
//    val todoItemList = TodoItemList()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
//        val group1 = findViewById<RadioButton>(R.id.group1)
//        val group2 = findViewById<RadioButton>(R.id.group2)
//        var groupdata: String = ""
//
//        val summaryData = findViewById<EditText>(R.id.summary)
//        val descriptionData = findViewById<EditText>(R.id.description)
//        val deadlinebutton = findViewById<Button>(R.id.deadlinedatepickerbutton)
//        val completedData = findViewById<CheckBox>(R.id.completed)
//        val importantData = findViewById<CheckBox>(R.id.important)
//        val postbutton = findViewById<Button>(R.id.postButton)
//        val getbutton = findViewById<Button>(R.id.getbutton)
//        var deadlinedata: String = ""
//
//        val todolist_textarea = findViewById<TextView>(R.id.TodoList)
//
//
//        radioGroup.setOnCheckedChangeListener(){group, checkedID->
//            if(checkedID == group1.id){
//                groupdata = "1"
//            }
//            else if (checkedID == group2.id){
//                groupdata = "2"
//            }
//        }
//
//        val calendar = Calendar.getInstance()
//        val listener = object : DatePickerDialog.OnDateSetListener{
//            override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
//                calendar.set(year, month, day)
//                val myformat = "yyyy-MM-dd"
//                val sdf = SimpleDateFormat(myformat, Locale.CHINA)
//                deadlinedata  = sdf.format(calendar.time)
//            }
//        }
//
//        deadlinebutton.setOnClickListener {  //日曆按鈕的點擊事件
//            DatePickerDialog(this,
//                listener,
//                calendar.get(Calendar.YEAR),
//                calendar.get(Calendar.MONTH),
//                calendar.get(Calendar.DAY_OF_MONTH)).show()
//        }
//
//        postbutton.setOnClickListener(){
//            val newTodoItem = TodoItem(
//                group = groupdata,
//                summary = summaryData.text.toString(),
//                description = descriptionData.text.toString(),
//                deadline = deadlinedata,
//                important = importantData.isChecked,
//                completed = completedData.isChecked
//            )
//
//            var jsonData: String = ""
//            val t1 = thread {
//                jsonData = postJsonData("http://yesducky.com/api/todo/", credentials, newTodoItem)
//            }
//            t1.join()
//        }
//
//        getbutton.setOnClickListener(){
//            val t1 = thread {
//                todoItemList.update()
//            }
//            t1.join()
//            var displayedText: String = ""
//            for(i in todoItemList.items){
//                displayedText += "id: ${i.id},\n group: ${i.group},\n summary: ${i.summary},\n description: ${i.description},\n created: ${i.created},\n deadline: ${i.deadline},\n important: ${i.important},\n completed: ${i.completed}\n"
//                displayedText += "__________________\n"
//            }
//            todolist_textarea.setMovementMethod(ScrollingMovementMethod())
//            todolist_textarea.text = displayedText
//
//        }
//
//    }
//
//
//}
//fun postJsonData(url: String, credentials: String, item: TodoItem): String {
//    val client = OkHttpClient.Builder()
//        .connectTimeout(10, TimeUnit.SECONDS)
//        .writeTimeout(10, TimeUnit.SECONDS)
//        .readTimeout(30, TimeUnit.SECONDS)
//        .build()
//
//    val postbody: RequestBody = FormBody.Builder()
//        .add("group", item.group)
//        .add("summary", item.summary)
//        .add("description", item.description)
//        .add("deadline", item.deadline)
//        .add("important", item.important.toString())
//        .add("completed", item.completed.toString())
//        .build()
//
//    val request: Request = Request.Builder()
//        .url(url)
//        .header("Authorization", credentials)
//        .post(postbody)
//        .build()
//
//    client.newCall(request).execute().use { response ->
//        if (!response.isSuccessful) throw IOException("Unexpected code $response")
//        return response.toString()
//    }
//}
//
//class TodoItem(
//    var id: Int = 0,
//    var group: String = "",
//    var summary: String = "",
//    var description: String = "",
//    var created: String = "",
//    var deadline: String = "",
//    var important: Boolean = false,
//    var completed: Boolean = false
//) {
//    fun printItem() {
//        println("id: $id,\n group: $group,\n summary: $summary,\n description: $description,\n created: $created,\n deadline: $deadline,\n important: $important,\n completed: $completed\n")
//    }
//}
//
///**
// * Get the json data from the server
// * Example:
// * val TodoItemList = TodoItemList()
// * TodoItemList.update()
// * TodoItemList.printList()
// */
//class TodoItemList(
//    var items: List<TodoItem> = listOf(TodoItem())
//){
////    init {
////        update()
////    }
//
//    /**
//     * Update the todo items
//     */
//    fun update(){
//        val JsonString = getJsonData("http://yesducky.com/api/todo/", credentials)
//        this.items = Gson().fromJson(JsonString, Array<TodoItem>::class.java).toList()
//    }
//
//    /**
//     * Print all the todo items
//     */
//    fun printList(){
//        for (i in items){
//            i.printItem()
//        }
//    }
//
//    /**
//     * Print the todo item by id
//     * @param id the id of the item
//     */
//    fun printItem(id: Int){
//        findItemById(id)?.printItem()
//    }
//
//    /**
//     * Find the item by id
//     * @param id the id of the item
//     * @return TodoItem
//     */
//    fun findItemById(id: Int): TodoItem? {
//        return items.find { item -> item.id == id }
//    }
//
//    /**
//     * Get the json data from the server
//     * @param url the url of the server
//     * @param credentials the credentials of the server
//     * @return stringified json
//     */
//    fun getJsonData(url: String, credentials: String): String {
//        val client = OkHttpClient.Builder()
//            .connectTimeout(10, TimeUnit.SECONDS)
//            .writeTimeout(10, TimeUnit.SECONDS)
//            .readTimeout(30, TimeUnit.SECONDS)
//            .build();
//
//        val request: Request = Request.Builder()
//            .url(url)
//            .header("Authorization", credentials)
//            .build()
//
//        client.newCall(request).execute().use { response ->
//            if (!response.isSuccessful) throw IOException("Unexpected code $response")
//            return response.body!!.string()
//        }
//    }
//}
