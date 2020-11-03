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
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.naina.bookapi.R
import com.naina.bookapi.adapter.RecyclerViewAdapter
import com.naina.bookapi.model.Book
import com.naina.bookapi.util.ConnectionManager
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
    var mBooks = arrayListOf<Book>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.button)
        editText = findViewById(R.id.editText)
        loading_indicator = findViewById(R.id.loading_indicator)
        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        button.setOnClickListener {
            mBooks.clear()
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
            var subtitle=""
            var author = "NOT AVAILABLE"
            var publisher="NOT AVAILABLE"
            var publishedDate = "NOT AVAILABLE"
            var description = "NO DESCRIPTION"
            var pageCount = 1000
            var rating=3.0
            var price = "NOT FOR SALE"
            var buyLink="NOT FOR SALE"
            try {
                val items = it.getJSONArray("items")
                for (i in 0 until items.length()) {
                    val item = items.getJSONObject(i)
                    val volumeInfo = item.getJSONObject("volumeInfo")
                    title = volumeInfo.getString("title")
                    if(volumeInfo.has("subtitle")){
                        subtitle=volumeInfo.getString("subtitle")
                    }
                    if(volumeInfo.has("authors"))
                    {
                        val authors = volumeInfo.getJSONArray("authors")
                        if (authors.length() == 1) {
                            author = authors.getString(0)
                        } else {
                            author = authors.getString(0) + "\n" + authors.getString(1);
                        }
                    }
                    if(volumeInfo.has("publisher"))
                    {
                        publisher = volumeInfo.getString("publisher")
                    }
                    if(volumeInfo.has("publishedDate"))
                    {
                        publishedDate = volumeInfo.getString("publishedDate")
                    }
                    if(volumeInfo.has("desciption"))
                    {
                        description = volumeInfo.getString("description")
                    }
                    if(volumeInfo.has("pageCount"))
                    {
                        pageCount = volumeInfo.getInt("pageCount")
                    }
                    if(volumeInfo.has("averageRating"))
                    {
                        rating = volumeInfo.getDouble("averageRating")
                    }
                    val thumbnail = volumeInfo.getJSONObject("imageLinks").getString("thumbnail")
                    val language=volumeInfo.getString("language")
                    val previewLink = volumeInfo.getString("previewLink")
                    val saleInfo = item.getJSONObject("saleInfo")
                    if (saleInfo.has("listPrice")) {
                        val listPrice = saleInfo.getJSONObject("listPrice")
                        price = listPrice.getString("amount") + " " + listPrice.getString("currencyCode")
                        buyLink = saleInfo.getString("buyLink")
                    }
                    val infoLink = volumeInfo.getString("infoLink")
                    mBooks.add(
                        Book(
                            title,
                            subtitle,
                            author,
                            publisher,
                            publishedDate,
                            description,
                            pageCount,
                            rating,
                            thumbnail,
                            language,
                            previewLink,
                            price,
                            buyLink,
                            infoLink
                        )
                    )
                    mAdapter = RecyclerViewAdapter(this@MainActivity, mBooks)
                    mRecyclerView.adapter = mAdapter
                    mRecyclerView.layoutManager = layoutManager
                }
            } catch (e: JSONException) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }, Response.ErrorListener {
            Toast.makeText(
                this,
                "Volley error occurred!",
                Toast.LENGTH_SHORT
            ).show()

        }) {
        }
        queue.add(request)
    }
}