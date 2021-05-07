package ipca.example.coudelaria.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import ipca.example.coudelaria.R
import ipca.example.coudelaria.models.Cavalo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.text.SimpleDateFormat
import java.util.*


fun Date.toServerString() : String  {
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    format.timeZone = TimeZone.getTimeZone("GMT")
    return format.format(this)
}

class AddCavaloFragment : Fragment(){

    lateinit var  editText   : EditText
    lateinit var  buttonSave : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_add_cavalo, container, false)
        editText = rootView.findViewById<EditText>(R.id.editTextName)
        buttonSave = rootView.findViewById<Button>(R.id.buttonSave)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonSave.setOnClickListener {



            GlobalScope.launch(Dispatchers.IO) {
                val client = OkHttpClient()
                val cavalo = Cavalo(
                        null,
                        editText.text.toString(),
                        Date().toServerString(),
                        "M",
                        null,
                        null,
                    10,
                        10  )

                val requestBody = RequestBody.create(
                    "application/json".toMediaTypeOrNull(),
                    cavalo.toJson().toString()
                    )
                Log.d("coudelaria", cavalo.toJson().toString())
                val request = Request.Builder()
                    .url("http://192.168.56.50:5000/api/cavalos")
                    .post(requestBody)
                    .build()
                client.newCall(request).execute().use { response ->
                    Log.d("coudelaria", response.message)
                    GlobalScope.launch (Dispatchers.Main){
                        if (response.message == "OK"){
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

}