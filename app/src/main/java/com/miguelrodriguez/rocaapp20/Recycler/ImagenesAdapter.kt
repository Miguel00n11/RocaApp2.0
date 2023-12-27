import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.miguelrodriguez.rocaapp20.R
import com.miguelrodriguez.rocaapp20.Recycler.ClaseImagen

class AdaptadorImagenes(private val listaImagenes: List<ClaseImagen>) : RecyclerView.Adapter<AdaptadorImagenes.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_imagen, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imagen = listaImagenes[position]
        // Cargar la imagen en el ImageView del ViewHolder utilizando una biblioteca como Picasso o Glide
        // (Asegúrate de agregar las dependencias correspondientes a tu archivo build.gradle)
        // Picasso.get().load(imagen.ruta).into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return listaImagenes.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Puedes personalizar los elementos de tu ViewHolder según tus necesidades,
        // por ejemplo, un ImageView para mostrar la imagen y un botón para eliminar.
        // val imageView: ImageView = itemView.findViewById(R.id.imageView)
        // val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminar)
    }
}
