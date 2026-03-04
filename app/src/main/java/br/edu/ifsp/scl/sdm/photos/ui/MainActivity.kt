package br.edu.ifsp.scl.sdm.photos.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.edu.ifsp.scl.sdm.photos.R
import br.edu.ifsp.scl.sdm.photos.databinding.ActivityMainBinding
import br.edu.ifsp.scl.sdm.photos.model.Photo
import android.widget.ArrayAdapter
import android.widget.AdapterView
import com.bumptech.glide.Glide
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import android.widget.Toast
class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val photoList: MutableList<Photo> = mutableListOf()

    private fun retrievePhotos() {

        val url = "https://jsonplaceholder.typicode.com/photos"
        val queue = Volley.newRequestQueue(this)

        val request = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->

                photoList.clear()

                for (i in 0 until response.length()) {

                    val jsonObject = response.getJSONObject(i)

                    val photo = Photo(
                        albumId = jsonObject.getInt("albumId"),
                        id = jsonObject.getInt("id"),
                        title = jsonObject.getString("title"),
                        url = jsonObject.getString("url"),
                        thumbnailUrl = jsonObject.getString("thumbnailUrl")
                    )

                    photoList.add(photo)
                }

                val titles = photoList.map { it.title }

                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    titles
                )

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                amb.photosSp.adapter = adapter
            },
            { error ->
                Toast.makeText(
                    this,
                    "Erro ao carregar fotos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        queue.add(request)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        setSupportActionBar(amb.mainTb.apply {
            title = getString(R.string.app_name)
        })

        retrievePhotos()

        amb.photosSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {

                val selectedPhoto = photoList[position]

                val fixedImageUrl =
                    "${selectedPhoto.url.replace("via.placeholder.com", "placehold.co")}/FFF.png"

                val fixedThumbnailUrl =
                    "${selectedPhoto.thumbnailUrl.replace("via.placeholder.com","placehold.co")}/FFF.png"

                Glide.with(this@MainActivity)
                    .load(fixedImageUrl)
                    .into(amb.photoIv)

                Glide.with(this@MainActivity)
                    .load(fixedThumbnailUrl)
                    .into(amb.thumbnailIv)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}