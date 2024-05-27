    package com.example.lolapi// com.example.android.MainActivity.kt

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.android.Summoner
import com.example.lolapi.R
import com.google.firebase.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings

    interface RiotGamesAPI {
    @GET("/lol/summoner/v4/summoners/by-name/{summonerName}")
    fun getSummonerByName(
        @Path("summonerName") summonerName: String,
        @Query("api_key") apiKey: String // Parámetro para la clave de API
    ): Call<Summoner>
    // Define más llamadas a la API según tus necesidades
}


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 30
        }
        val fireBaseConfig = Firebase.remoteConfig
        fireBaseConfig.setConfigSettingsAsync(configSettings)
        fireBaseConfig.setDefaultsAsync(mapOf("show_error_button" to false, "error_button_text" to ""))

        setContentView(R.layout.activity_login)

        setContentView(R.layout.start_screen)


        Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener{
            task ->
            if (task.isSuccessful){
                val showErrorButton = Firebase.remoteConfig.getBoolean("show_error_button")
                val errorButtonText = Firebase.remoteConfig.getString(("error_button_text"))
            }
        }

        Toast.makeText(this, "Toca la lupa para buscar perfiles", Toast.LENGTH_LONG).show()


        /*
                val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.start_toolbar)
                setSupportActionBar(toolbar)

                supportActionBar?.apply {
                    title = null // Hace que no aparezca el título del proyecto en la toolbar
                }

                val menu_button: ImageView = findViewById(R.id.btn_menu)
                menu_button.setOnClickListener { view ->
                    showPopupMenu(view)
                }
a
         */



        RestoreMenuButtons(this)
    }

    private fun SetProfile(nombre: String, ma: MainActivity){
        setContentView(R.layout.profile_layout)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://euw1.api.riotgames.com/lol/summoner/v4/summoners/by-name/") // URL base de la API de Riot Games
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val riotGamesAPI = retrofit.create(RiotGamesAPI::class.java)

        val text: TextView = findViewById(R.id.user_name)
        val level: TextView = findViewById(R.id.user_level)
        val icon: ImageView = findViewById(R.id.user_icon)

        val call = riotGamesAPI.getSummonerByName(nombre, "RGAPI-7b1e100a-cd68-4bda-bb3b-247c110a4986")

        var image_url = "https://ddragon.leagueoflegends.com/cdn/11.14.1/img/profileicon/"

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.start_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title = null // Hace que no aparezca el título del proyecto en la toolbar
        }

        val menu_button: ImageView = findViewById(R.id.btn_menu)
        menu_button.setOnClickListener { view ->
            showPopupMenu(view)
        }

        val menu_search: SearchView = findViewById(R.id.search_view)
        menu_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Realizar la búsqueda cuando se presiona Enter o se confirma la búsqueda
                SetProfile(query, ma)
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })

        val requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)

        call.enqueue(object : Callback<Summoner> {
            override fun onResponse(call: Call<Summoner>, response: Response<Summoner>) {
                if (response.isSuccessful) {
                    val summoner = response.body()
                    if (summoner != null) {
                        text.text = summoner.name
                        level.text = summoner.summonerLevel
                        image_url += summoner.profileIconId + ".png"



                        Glide.with(ma)
                            .load(image_url)
                            .apply(requestOptions)
                            .into(icon)
                    }
                } else {
                    text.text= "Usuario no encontrado"
                    level.text= "Verifica que el nombre sea correcto"
                    image_url="data:image/png;base64,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"

                    Glide.with(ma)
                        .load(image_url)
                        .apply(requestOptions)
                        .into(icon)
                }
            }

            override fun onFailure(call: Call<Summoner>, t: Throwable) {
                // Manejar errores de conexión u otros errores aquí
                text.text= "No se pudo encontrar al usuario"
                level.text= "Verifica que el nombre sea correcto"
                image_url="hdata:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAOEAAADhCAMAAAAJbSJIAAAAdVBMVEX///8AAACzs7Pm5uZTU1PQ0NCDg4MYGBhzc3O4uLhiYmIbGxsxMTH8/Pzh4eHq6uq9vb0gICA8PDyOjo4QEBCLi4uUlJSAgIA3Nzf29vYNDQ1OTk7X19d7e3tdXV3ExMSioqKgoKCsrKxFRUVpaWkqKipPT09VXIiEAAAIEUlEQVR4nOWde3+iPBCFxXoBlVWrqFC0Qt39/h/x1ba2Xk4ggcxMyHv+XfktTyeZkwy59HpX5ZPRvOez8kOw3vqMeAYMgvHeW8T4EzDwN4rx8BR8aZz4iTg8Zd+EwdJLxPwX8Iy48g/xuw9etfbONB4A/UN8AvQuo4ZPgGdEr3yxnAHEsU8NNT0iRK9MIz3+9R8RNlSvEMsdQMxwX0xfnNM8jZshYl/MRxPXFG4W+bwGMoUZFZpGfFyCn0prt1/UdKq0fAXPwSimxZQdQEOz0bA6jGn0AR5bjl7ATzcuRjEIBlFaiRgvICLKqK4i7somiNNOIUaVhL3e+xt4KktQQ4WjBHkNhnWI/8BTaxhFRxHrxtMxjCLOqG4izhY1vogR4egmPY7Z319DtfM+jDhFphE7mW52eQ3hGbHjvljXTC+mgUY33fHFTbUnfkrRF1EUC/fSTagz6cMZFfpisRtLK7t/zwl4TW1E1FD721BYH1kDQgPTiP8s+rIKxw0Iz4hodDNGDbUnjdiMsJfCKELTkI5iQ0KTyZQwYlNC5RjVuSg2Juz1+rB24xxiC8J8AAixaUgitiAcQsLzlNgtRPuErkWRgPCcUV3qixSEZ190KIokhOcpsTuINITBeutMQyUixJMpEUQqQlyBk0AkIwwytLQo/hP5Q4gL/vxRJCR0ZKZBSeiGaZASBuu9fEa1R5ihKr4Dkyl7hNOD9odwVkSLhMkGLkqRTjcWCUfRCkVxLOyLVgn7EFFhGlyIdgldRLRM2N/AdAOXFjH1RduEGHEs6IvWCRWIaOE0D6J9wgiahlx5yj5hP1ohRDHTICB0LKOSEDqFSEOoMA2RdENE6JAvUhGqfJEfkYywv4Ef3/gbKh0hNg1+X6QjPGdUhMhenqIkNEIkMw1SQkW64Y0iLSFGzFjTDTGhA6ZBTdiHMw1ORHJCnG4YTYOeECPyfbZhIBT2RQ5Coyha90UWQgPTsL9Yk4dQcr7IRCjoi1yE2Bc5vkyxEarSDXmpmI9QyjQYCfsreDoDrqPaQ+QkjLBpwD2B9hA5CVWmQZtueAklfJGZUGEalIjchDijwvNuLCGyE7KbBj8hN6IAoaIvUiFKECp8kQhRgpDXF2UIOX1RiBB/JSZZyShFqPgQToAoRshmGnKEitGNdURBQpVpWG6ogoSqdGMZUZSQxRdlCRWlYqt9UZgQN1R43k3TwoY0ocoX7UVRnJDcF+UJVRU4WxlVnrCPNzFYMw0HCBVRtDUldoHQzDRM/xc3CPsrdFIKPsjXNIqOEBJOplwhVJiGBV90hlBV2GgdRXcIFabRuqG6Q6j0xZYN1SFCxW6btjMNlwiVU+JWvugWIfbFdsNwxwgJJlOuEdrf3OccoXVfdI/Qti+6R6jaotm0LwoRlvtjBSKMYtPzboQIt9NDWfHPuALXzBeFvpDOgmlY9QOLWzRFvnIXF1+fJZV/A1xHbdAXJQiPh8+jfT82xohNzrsRICz3X8d8Z4eKbKNoqGNzXxQgTK5mMA3Lqt9ZMg1+wuJ3cF3dFaM9urDGuALHTlhObs7Xfq1sp4eHw8a//x9DRHbC7fr2oUHFL8O7X/7K0DS4CR+uSlqHqoeiRHkmv9meKWbC4jFB/lV1RegW17+LyT4NVsLoOHl6248C/ykqD4Mz2YrCSnh1wlthVzxOFJ3wiqg/JWYlTGBdG7hiGdbd/aHvi5yExRtM/7vnrjhCOzPupb20iJGwnChuD3rsilGBKm6P0q3AMRJulXfqPboiOrFfG/HxLfgIK67VyU53jz7fQIyltx6VjfCIssxVy+Tm2VDRmAGijmlwEZbVgXn97YpJfZb5kY4vMhGW2+rAZD9lm41OlvmRRkNlIoRfeG91dcUjnlAoVW8aPIRFfXb8csXyVD2WeVatabAQKp3wVm+XrggnvTWINVs0OQjRcBToUFbMmCpUM4DjINTMjuvwaW6liViZbhgItcZgF83q0pFKlaZBT1gaZkfbiPSEe+0hSgtV9EX6M4Z4rkBUmwY14dFoiNIGUWUaxIR31VFaqUY3tISaTmhHiqMLtqSEsDBDJpxu+vfvYJdQYzhqVXC++HDdq1XCsqYkaF/4Au3itqtYJawtCdpX/dXLVs8vlbgMuPbqZYuElYUZOk2RL6abn4RqkXC/m5ELdQN8ae/PZfZ2c2lELIP9i+kV0YVVXybS39d/RewaoVEUl50kNNii+ZVRu0eo2r+oQuwgocEWzXQz7SShanMfimKx7CKhydEF6RFuonad0Ghff552ktAA0UQuERocQNFVQoMtml0lNDCNxoTJgnqkXS2826ZNFB8I15NkJKrkgKrr0DSaEQbrqbRgbbZFusl1V4UIC9dRdTQPpd9dU3DhtJZW0q+uq8amUfJXCxsKbtHUUFc6YqDYbaOhQvrF9QX3L9brpTtBbGoakcEaNGnBTe+1Sgvuby8t1Mw05kmHEOE+jVq9FB1qqM1MI406lG4a+uJLMeiM9cMtmvWK83IVHgbOCV4x2XQAF8/zoWvK4Zr/1uUpp/SOlis1n2m4qAWKIj4kraOK3/8BxHblKceUvqMoNp1MOakYIsL1qF0VRoTn3XRWGNGnhopNwy/EBURES4u6KtwXvTKN/y0i3BHeVeHRDVxy21WpouiVL8KzikfSr2VT0DQm0m9lUzFAHOTSb2VV8VNDPfgF+JxuvAO8IN58ms9O/gHe+WJ2Gkq/DYl+++JpGEu/DJG+EQ+5r4DfpuGZTdzrgug14KUvJr+A/wHxCWqVDME8gQAAAABJRU5ErkJggg=="
            }
        })
    }
    // Menu ------------------------------------------------------------------------------------------------------------------------
    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.menu_settings)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_settings -> {
                    // Lógica cuando se selecciona la opción del menú
                    Toast.makeText(this, "Configuración seleccionada", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.action_home -> {
                    onBackPressed()
                    true
                }
                // Otras opciones
                else -> false
            }
        }

        popupMenu.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setContentView(R.layout.start_screen)
        RestoreMenuButtons(this)
    }

    private fun RestoreMenuButtons(ma: MainActivity){
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.start_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title = null // Hace que no aparezca el título del proyecto en la toolbar
        }

        val menu_button: ImageView = findViewById(R.id.btn_menu)
        menu_button.setOnClickListener { view ->
            showPopupMenu(view)
        }

        val menu_search: SearchView = findViewById(R.id.search_view)
        menu_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Realizar la búsqueda cuando se presiona Enter o se confirma la búsqueda
                SetProfile(query, ma)
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })
    }
}