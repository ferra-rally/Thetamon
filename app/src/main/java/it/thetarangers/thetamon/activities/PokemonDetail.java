package it.thetarangers.thetamon.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.utilities.ImageManager;

public class PokemonDetail extends AppCompatActivity {
    Pokemon pokemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_detail);

        pokemon = getIntent().getParcelableExtra("pokemon");

        new Holder(PokemonDetail.this);
    }

    class Holder {
        final ConstraintLayout clBackground;
        final ImageView ivSprite;
        ImageManager imageManager = new ImageManager();

        Holder(Context context){
            clBackground = findViewById(R.id.clBackground);
            clBackground.setBackgroundColor(Color.parseColor(pokemon.getAverageColor()));

            ivSprite = findViewById(R.id.ivSprite);
            ivSprite.setImageBitmap(imageManager.loadFromDisk(
                    context.getFilesDir() + context.getString(R.string.sprites_front),
                    pokemon.getId() + context.getString(R.string.extension)));


        }
    }
}