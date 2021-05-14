package ipca.example.coudelaria.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.navigation.findNavController
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

var cavalos : MutableList<Cavalo> = arrayListOf()

class CavalosFragment : Fragment() {

    var listView : ListView? = null
    lateinit var adapter : CavalosAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         val rootView = inflater.inflate(R.layout.fragment_cavalos, container, false)

        listView = rootView.findViewById<ListView>(R.id.listViewCavalos)
        adapter = CavalosAdapter()
        listView?.adapter = adapter

        setHasOptionsMenu(true)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        GlobalScope.launch(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder().url("http://192.168.1.82:5000/api/cavalos").build()
            client.newCall(request).execute().use { response ->
                cavalos.clear()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_cavalos,menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        when (item.itemId){
            R.id.itemAdd -> {
                val action = CavalosFragmentDirections.actionNavigationCavalosToAddCavaloFragment2()
                this.view?.findNavController()?.navigate(action)
                return true
            }
        }

        return false
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

            rowView.setOnClickListener {
                val action = CavalosFragmentDirections
                    .actionNavigationCavalosToEditCavaloFragment(cavalos[position].toJson().toString())
                rowView.findNavController().navigate(action)
            }


            return rowView
        }
    }

}