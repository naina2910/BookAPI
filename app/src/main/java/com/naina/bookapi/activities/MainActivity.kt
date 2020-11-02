package com.naina.bookapi.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.naina.bookapi.R
import com.naina.bookapi.adapter.RecyclerViewAdapter
import com.naina.bookapi.model.Book
import com.naina.bookapi.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    lateinit var button: Button
    lateinit var editText: EditText
    lateinit var queue: RequestQueue
    lateinit var jsonObject: JSONObject
    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: RecyclerViewAdapter
    lateinit var loading_indicator: ProgressBar
    lateinit var layoutManager: RecyclerView.LayoutManager
    var mBooks= arrayListOf<Book>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.button)
        editText = findViewById(R.id.editText)
        loading_indicator = findViewById(R.id.loading_indicator)
        mRecyclerView = findViewById(R.id.recycler_view)
        layoutManager = LinearLayoutManager(this)
        button.setOnClickListener {
            queue = Volley.newRequestQueue(this)
            val BASE_URL = "https://www.googleapis.com/books/v1/volumes?q="
            jsonObject = JSONObject()
            val search_query = editText.text.toString()
            if (ConnectionManager().checkConnectivity(this)) {
                if (search_query.equals("")) {
                    Toast.makeText(this, "Please enter your query", Toast.LENGTH_SHORT).show();
                } else {
                    val final_query = search_query.replace(" ", "+")
                    val uri = Uri.parse(BASE_URL + final_query);
                    val buider = uri.buildUpon()
                    parseJson(buider.toString())
                }
            } else {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection Not Found")
                dialog.setPositiveButton("open setting")
                { text, listner ->
                    val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingIntent)
                    finish()
                }
                dialog.setNegativeButton("exit")
                { text, listner ->
                    ActivityCompat.finishAffinity(this)
                }

                dialog.create()
                dialog.show()
            }
        }
    }

    fun parseJson(key: String) {
        val request = object : JsonObjectRequest(Method.GET, key, null, Response.Listener {
            var title = ""
            var author = ""
            var publishedDate = "NoT Available"
            var description = "No Description"
            var pageCount = 1000
            var categories = "No categories Available "
            var buy = ""
            var price = "NOT_FOR_SALE"
            try {
                val items = it.getJSONArray("items")
                print("hiiii" + items)
                for (i in 0 until items.length()) {
                    print("data" + items)
                    val item = items.getJSONObject(i)
                    val volumeInfo = item.getJSONObject("volumeInfo")
                    title = volumeInfo.getString("title")
                    val authors = volumeInfo.getJSONArray("authors")

                    if (authors.length() == 1) {
                        author = authors.getString(0);
                    } else {
                        author = authors.getString(0) + "|" + authors.getString(1);
                    }
                    publishedDate = volumeInfo.getString("publishedDate");
                    pageCount = volumeInfo.getInt("pageCount");
                    val saleInfo = item.getJSONObject("saleInfo")
                    val ebook=saleInfo.getBoolean("isEbook")
                    if(ebook)
                    {
                        val listPrice = saleInfo.getJSONObject("listPrice")
                        price = listPrice.getString("amount") + " " + listPrice.getString("currencyCode")
                    }
                    /*val saleInfo = item.getJSONObject("saleInfo")
                    val listPrice = saleInfo.getJSONObject("listPrice")
                    price =
                        listPrice.getString("amount") + " " + listPrice.getString("currencyCode")
                    description = volumeInfo.getString("description")
                    buy = saleInfo.getString("buyLink")
                    categories = volumeInfo.getJSONArray("categories").getString(0)*/
                    val thumbnail =
                        volumeInfo.getJSONObject("imageLinks").getString("smallThumbnail")
                    val previewLink = volumeInfo.getString("previewLink")
                    val url = volumeInfo.getString("infoLink")
                    mBooks.add(
                        Book(
                            title, author, publishedDate, description, categories
                            , thumbnail, buy, previewLink, price, pageCount, url
                        )
                    )
                    mAdapter = RecyclerViewAdapter(this@MainActivity, mBooks)
                    mRecyclerView.adapter = mAdapter
                    mRecyclerView.layoutManager = layoutManager
                }


            } catch (e: JSONException) {
                /*Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()*/
            }
        }, Response.ErrorListener {
            Toast.makeText(
                this,
                "Volley error occurred!",
                Toast.LENGTH_SHORT
            ).show()

        }) {
            /*override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-type"] = "application/json"
                headers["key"] = "AIzaSyAfvP_Rt2_zdHngSuknQ4ro45rTR_UA9_Y"
                return headers
            }*/
        }
        queue.add(request)
    }
}