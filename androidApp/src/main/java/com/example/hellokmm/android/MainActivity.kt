package com.example.hellokmm.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class Posts(
    val title: String,
    val body: String,
)

interface PostService {
    @GET("posts")
    fun getPosts(): Call<List<Posts>>
}

class MainActivity : ComponentActivity() {

    private val retrofit =
        Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://jsonplaceholder.typicode.com/").build()
            .create(PostService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val state = produceState<List<Posts>>(
                        initialValue = emptyList(),
                        producer = {
                            try {
                                withContext(Dispatchers.IO) {
                                    val response = retrofit.getPosts().execute()
                                    val posts = response.body()
                                    if (response.isSuccessful && posts != null) {
                                        value = posts
                                    } else {
                                        print(response.message())
                                    }
                                }
                            } catch (e: Exception) {
                                print(e.message)
                            }
                        },
                    )
                    LazyColumn {
                        item {
                            Text(
                                text = "Nice header",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                style = TextStyle(fontSize = 24.sp),
                            )
                        }
                        items(state.value) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = it.title,
                                    fontSize = 20.sp,
                                    fontStyle = FontStyle.Italic,
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(text = it.body)
                            }
                        }
                    }
                }
            }
        }
    }
}
