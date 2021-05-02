package ipca.example.coudelaria.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import ipca.example.coudelaria.R
import ipca.example.coudelaria.models.Cavalo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException


class CavalosFragment : Fragment() {

    var listView : ListView? = null
    lateinit var adapter : CavalosAdapter
    var cavalos : MutableList<Cavalo> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         val rootView = inflater.inflate(R.layout.fragment_cavalos, container, false)

        listView = rootView.findViewById<ListView>(R.id.listViewCavalos)
        adapter = CavalosAdapter()
        listView?.adapter = adapter

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        GlobalScope.launch(Dispatchers.IO) {

            val client = OkHttpClient()
            val request = Request.Builder().url("http://192.168.56.50:5000/api/cavalos").build()
            client.newCall(request).execute().use { response ->

                val jsStr : String = response.body!!.string()
                println(jsStr)

                val jsonArrayCavalos = JSONArray(jsStr)

                for ( index in  0 until jsonArrayCavalos.length()) {
                    val jsonArticle : JSONObject = jsonArrayCavalos.get(index) as JSONObject
                    val cavalo = Cavalo.fromJson(jsonArticle)
                    cavalos.add(cavalo)
                }

                GlobalScope.launch (Dispatchers.Main){
                    adapter.notifyDataSetChanged()
                }



            }
        }


    }

    inner class CavalosAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return cavalos.size
        }

        override fun getItem(position: Int): Any {
            return cavalos[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rowView = layoutInflater.inflate(R.layout.row_cavalo, parent, false)

            val textViewCod = rowView.findViewById<TextView>(R.id.textViewCod)
            val textViewCavalo = rowView.findViewById<TextView>(R.id.textViewCavalo)


            textViewCod.text = cavalos[position].codCavalo.toString()
            textViewCavalo.text = cavalos[position].nomeCavalo


            return rowView
        }
    }

}