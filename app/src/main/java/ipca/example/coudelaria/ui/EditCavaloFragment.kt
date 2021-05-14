package ipca.example.coudelaria.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import ipca.example.coudelaria.R
import ipca.example.coudelaria.models.Cavalo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import kotlin.toString as toString


class EditCavaloFragment : Fragment() {

    private var cavaloJsonStr: String? = null
    lateinit var editTextName: EditText
    lateinit var buttonEdit : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            cavaloJsonStr = it.getString("cavalo")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_edit_cavalo, container, false)

        editTextName = rootView.findViewById(R.id.editTextName)
        buttonEdit = rootView.findViewById(R.id.buttonEdit)

        // Inflate the layout for this fragment
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cavaloTemp = Cavalo.fromJson(JSONObject(cavaloJsonStr))

        editTextName.hint = cavaloTemp.nomeCavalo

        buttonEdit.setOnClickListener {

            GlobalScope.launch(Dispatchers.IO) {
                val client = OkHttpClient()

                val cavalo = Cavalo(
                        cavaloTemp.codCavalo,
                        editTextName.text.toString(),
                        cavaloTemp.dataNascimento,
                        cavaloTemp.genero,
                        cavaloTemp.mae,
                        cavaloTemp.pai,
                        cavaloTemp.codCoudelariaNasc,
                        cavaloTemp.codCoudelariaResid)

                val requestBody = RequestBody.create(
                        "application/json".toMediaTypeOrNull(),
                        cavalo.toJson().toString()
                )
                Log.d("coudelaria", cavalo.toJson().toString())
                val request = Request.Builder()
                        .url("http://192.168.1.82:5000/api/cavalos/${cavaloTemp.codCavalo}")
                        .put(requestBody)
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

            println(editTextName.text.toString())

        }

    }

}