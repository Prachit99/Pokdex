import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pokedex.Pokemon
import com.example.pokedex.R

class PokemonAdapter(private val pokemonList: List<Pokemon>) : RecyclerView.Adapter<PokemonAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pokemonNameTextView: TextView = itemView.findViewById(R.id.pokemonName)
        private val pokemonTypeTextView: TextView = itemView.findViewById(R.id.pokemonType)
        private val pokemonImageView: ImageView = itemView.findViewById(R.id.pokemonImage)

        fun bind(pokemon: Pokemon) {
            pokemonNameTextView.text = "Name: ${pokemon.name}"
            pokemonTypeTextView.text = "Type: ${pokemon.types.joinToString(", ")}"
            Glide.with(itemView.context)
                .load(pokemon.imageUrl) // Use the image URL provided by Pokemon
                .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
                .error(R.drawable.error_image) // Image to show in case of error
                .into(pokemonImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pokemon_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pokemon = pokemonList[position]
        holder.bind(pokemon)
    }

    override fun getItemCount(): Int = pokemonList.size
}
