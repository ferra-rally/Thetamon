package it.thetarangers.thetamon.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.database.DaoThread;
import it.thetarangers.thetamon.database.PokemonDao;
import it.thetarangers.thetamon.database.PokemonDb;
import it.thetarangers.thetamon.favorites.FavoritesManager;
import it.thetarangers.thetamon.fragments.FragmentAbility;
import it.thetarangers.thetamon.fragments.FragmentMoves;
import it.thetarangers.thetamon.model.Ability;
import it.thetarangers.thetamon.model.EvolutionDetail;
import it.thetarangers.thetamon.model.Move;
import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.utilities.ImageManager;
import it.thetarangers.thetamon.utilities.StringManager;
import it.thetarangers.thetamon.utilities.TypeTextViewManager;
import it.thetarangers.thetamon.utilities.VolleyEvolutionChain;
import it.thetarangers.thetamon.utilities.VolleyPokemonDetail;

public class PokemonDetailActivity extends AppCompatActivity {

    public static final String POKEMONS = "pokemons";
    public static int RESULT_OK = 42;
    public static int REQUEST_CODE = 42;

    Pokemon pokemon;
    Handler handler;
    List<Move> moves;
    FavoritesManager favoritesManager = new FavoritesManager(this);
    Intent result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_detail);

        // built from parcelable
        pokemon = getIntent().getParcelableExtra("pokemon");

        result = new Intent();

        handler = new Handler();
        new Holder();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data != null) {
            @SuppressWarnings("unchecked")
            HashMap<Integer, Boolean> tmp = (HashMap<Integer, Boolean>) data.getSerializableExtra(POKEMONS);
            if (tmp != null) {
                @SuppressWarnings("unchecked")
                HashMap<Integer, Boolean> current = (HashMap<Integer, Boolean>) result.getSerializableExtra(POKEMONS);
                if (current == null) {
                    current = new HashMap<>();
                }
                // Merge the two HashMaps
                current.putAll(tmp);
                result.putExtra(POKEMONS, current);
                setResult(RESULT_OK, result);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateIntent() {
        @SuppressWarnings("unchecked")
        HashMap<Integer, Boolean> toSend = (HashMap<Integer, Boolean>) result.getSerializableExtra(POKEMONS);
        if (toSend == null) {
            toSend = new HashMap<>();
        }
        toSend.put(pokemon.getId(), pokemon.getFavorite());
        result.putExtra(POKEMONS, toSend);
        setResult(RESULT_OK, result);
    }

    class Holder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
        final ImageView ivSprite;
        final ShapeableImageView ivOverlay;
        final ShapeableImageView ivBackground;
        final TextView tvName;
        final TextView tvID;
        final TextView tvHabitat;
        final TextView tvHp;
        final TextView tvAttack;
        final TextView tvDefense;
        final TextView tvSpecialAttack;
        final TextView tvSpecialDefense;
        final TextView tvSpeed;
        final TextView tvFlavorText;
        final TextView tvGender;
        final LinearLayout llAbilities;
        final ProgressBar pbHp;
        final ProgressBar pbAttack;
        final ProgressBar pbDefense;
        final ProgressBar pbSpecialAttack;
        final ProgressBar pbSpecialDefense;
        final ProgressBar pbSpeed;
        final ProgressBar pbGender;


        final TextView tvLoading, tvType1, tvType2;

        final LinearLayout llEvolution1;
        final LinearLayout llEvolution2;
        final LinearLayout llEvolution3;

        final Button btnMoves;

        final ConstraintLayout clBody, clLoading;

        final ToggleButton tbFavorite;

        ImageManager imageManager;

        Holder() {
            tvID = findViewById(R.id.tvId);
            tvHabitat = findViewById(R.id.tvHabitat);
            tvName = findViewById(R.id.tvName);
            ivBackground = findViewById(R.id.ivBackground);
            ivSprite = findViewById(R.id.ivSprite);
            ivOverlay = findViewById(R.id.ivOverlay);
            tvHp = findViewById(R.id.tvHp);
            tvAttack = findViewById(R.id.tvAttack);
            tvDefense = findViewById(R.id.tvDefense);
            tvSpecialAttack = findViewById(R.id.tvSpecialAttack);
            tvSpecialDefense = findViewById(R.id.tvSpecialDefense);
            tvSpeed = findViewById(R.id.tvSpeed);
            tvGender = findViewById(R.id.tvGenderRate);

            pbHp = findViewById(R.id.pbHp);
            pbAttack = findViewById(R.id.pbAttack);
            pbDefense = findViewById(R.id.pbDefense);
            pbSpecialAttack = findViewById(R.id.pbSpecialAttack);
            pbSpecialDefense = findViewById(R.id.pbSpecialDefense);
            pbSpeed = findViewById(R.id.pbSpeed);
            pbGender = findViewById(R.id.pbGender);

            tvFlavorText = findViewById(R.id.tvFlavorText);

            tvLoading = findViewById(R.id.tvLoading);
            tvType1 = findViewById(R.id.tvType1);
            tvType2 = findViewById(R.id.tvType2);

            btnMoves = findViewById(R.id.btnMoves);
            btnMoves.setOnClickListener(this);
            btnMoves.setEnabled(false);

            findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PokemonDetailActivity.this, PokedexActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });

            llAbilities = findViewById(R.id.llAbilities);

            llEvolution1 = findViewById(R.id.llEvolution1);
            llEvolution2 = findViewById(R.id.llEvolution2);
            llEvolution3 = findViewById(R.id.llEvolution3);

            clBody = findViewById(R.id.clBody);
            clLoading = findViewById(R.id.clLoading);

            tbFavorite = findViewById(R.id.tbFavorite);

            imageManager = new ImageManager();

            // set everything before having the detail of the pokemon
            this.beforeDetails();

            VolleyEvolutionChain volleyEvolutionChain = new VolleyEvolutionChain(PokemonDetailActivity.this) {
                @Override
                public void fill(EvolutionDetail evolutionDetail) {
                    pokemon.setEvolutionChain(evolutionDetail.toJSON());
                    DaoThread thread = new DaoThread();

                    // Save pokemon when the API is called
                    thread.savePokemon(PokemonDetailActivity.this, pokemon);

                    fillEvolution(evolutionDetail);
                    // Fill the view with details when all information are available
                    Holder.this.afterDetails();
                }
            };

            VolleyPokemonDetail volley = new VolleyPokemonDetail(PokemonDetailActivity.this, pokemon) {
                @Override
                public void fill(Pokemon pokemon) {
                    // Set the reference to pokemon with details and call holder method
                    PokemonDetailActivity.this.pokemon = pokemon;

                    pokemon.encode();

                    volleyEvolutionChain.getEvolutionChain(pokemon.getUrlEvolutionChain());
                }
            };

            Thread thread = new Thread(() -> {
                PokemonDao dao = PokemonDb.getInstance(PokemonDetailActivity.this).pokemonDao();
                Pokemon tmp = dao.getPokemonFromId(pokemon.getId()).get(0);

                Log.d("Tokyo", "Sono il pokemon del DB e sono un favorito: ");

                //Call the API only if the pokemon is not in the DB
                if (tmp.getMovesList() == null) {
                    // Get the detail of the pokemon
                    Log.d("POKE", "volley");
                    volley.getPokemonDetail();
                } else {
                    Log.d("POKE", "db");
                    pokemon = tmp;
                    EvolutionDetail ev = tmp.getEvolutionDetail();

                    handler.post(() -> {
                        fillEvolution(ev);
                        Holder.this.afterDetails();
                    });
                }
            });

            thread.start();
        }

        private void beforeDetails() {

            //set textViews
            tvID.setText(String.format(Locale.getDefault(), "#%d", pokemon.getId()));
            tvName.setText(StringManager.capitalize(pokemon.getName()));

            //set the type textViews
            TypeTextViewManager typeTextViewManager = new TypeTextViewManager(pokemon, tvType1, tvType2);
            typeTextViewManager.setup();

            //set the sprite part
            ivBackground.setBackgroundColor(Color.parseColor(pokemon.getAverageColor()));

            ivSprite.setImageBitmap(imageManager.loadFromDisk(
                    PokemonDetailActivity.this.getFilesDir() + PokemonDetailActivity.this.getString(R.string.sprites_front),
                    pokemon.getId() + PokemonDetailActivity.this.getString(R.string.extension)));

            // programmatically resize the height of ivSprite based on screen height
            double resize = 0.25;
            int screenHeight = PokemonDetailActivity.this.getResources().getDisplayMetrics().heightPixels;
            ivSprite.getLayoutParams().height = (int) (screenHeight * resize);

            float radius = PokemonDetailActivity.this.getResources().getDimension(R.dimen.pokemon_detail_image_radius);

            ivOverlay.setShapeAppearanceModel(ivOverlay.getShapeAppearanceModel()
                    .toBuilder()
                    .setBottomLeftCorner(CornerFamily.ROUNDED, radius)
                    .setBottomRightCorner(CornerFamily.ROUNDED, radius)
                    .build());

            ivBackground.setShapeAppearanceModel(ivOverlay.getShapeAppearanceModel()
                    .toBuilder()
                    .setBottomLeftCorner(CornerFamily.ROUNDED, radius)
                    .setBottomRightCorner(CornerFamily.ROUNDED, radius)
                    .build());

            //set the loading views to visible
            clLoading.setVisibility(View.VISIBLE);
            clBody.setVisibility(View.INVISIBLE);

        }

        void enableButton() {
            btnMoves.setEnabled(true);
        }

        void afterDetails() {

            // set the loading textView to invisible
            runOnUiThread(() -> {
                clBody.setVisibility(View.VISIBLE);
                clLoading.setVisibility(View.INVISIBLE);
            });

            Resources res = getResources();

            //Fill text views with pokemon's details
            tvHabitat.setText(String.format(Locale.getDefault(), "%s: %s", res.getString(R.string.label_habitat), StringManager.capitalize(pokemon.getHabitat())));
            tvHp.setText(String.format(Locale.getDefault(), "%s: %d", res.getString(R.string.label_hp), pokemon.getHp()));
            pbHp.setProgress(pokemon.getHp());
            tvAttack.setText(String.format(Locale.getDefault(), "%s: %d", res.getString(R.string.label_attack), pokemon.getAttack()));
            pbAttack.setProgress(pokemon.getAttack());
            tvDefense.setText(String.format(Locale.getDefault(), "%s: %d", res.getString(R.string.label_defense), pokemon.getDefense()));
            pbDefense.setProgress(pokemon.getDefense());
            tvSpecialAttack.setText(String.format(Locale.getDefault(), "%s: %d", res.getString(R.string.label_special_attack), pokemon.getSpecialAttack()));
            pbSpecialAttack.setProgress(pokemon.getSpecialAttack());
            tvSpecialDefense.setText(String.format(Locale.getDefault(), "%s: %d", res.getString(R.string.label_special_defense), pokemon.getSpecialDefense()));
            pbSpecialDefense.setProgress(pokemon.getSpecialDefense());
            tvSpeed.setText(String.format(Locale.getDefault(), "%s: %d", res.getString(R.string.label_speed), pokemon.getSpeed()));
            pbSpeed.setProgress(pokemon.getSpeed());

            int genderRate = pokemon.getGenderRate();
            double genderPerc = (genderRate / 8.0) * 100;
            if(genderRate == -1) {
                tvGender.setText(PokemonDetailActivity.this.getString(R.string.label_genderless));
                pbGender.setProgress(8);
                pbGender.getProgressDrawable().setTint(PokemonDetailActivity.this.getColor(R.color.colorGenderLess));
            } else {
                tvGender.setText(String.format(Locale.getDefault(), "%3.1f%%♀ %3.1f%%♂",  genderPerc, (100 - genderPerc)));
                pbGender.setProgress(genderRate);
            }

            tvFlavorText.setText(StringManager.format(pokemon.getFlavorText()));

            fillAbilities(pokemon);

            // Start parsing moves from DB
            moves = pokemon.getMovesList();
            DaoThread daoThread = new DaoThread();

            daoThread.getMoveDetails(PokemonDetailActivity.this, moves, handler, this::enableButton);

            //set the favorite star
            if (pokemon.getFavorite()) {
                tbFavorite.setChecked(true);
            } else {
                tbFavorite.setChecked(false);
            }
            tbFavorite.setOnCheckedChangeListener(this);

        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btnMoves) {
                FragmentMoves fragmentMoves = new FragmentMoves();
                Bundle args = new Bundle();
                args.putParcelableArrayList(FragmentMoves.MOVES, (ArrayList<? extends Parcelable>) moves);
                fragmentMoves.setArguments(args);
                fragmentMoves.show(getSupportFragmentManager(), FragmentMoves.TAG);
            }
            if (v.getId() == R.id.cvAbility) {
                FragmentAbility fragmentAbility = new FragmentAbility();

                TextView tv = v.findViewById(R.id.tvAbility);

                Bundle arg = new Bundle();
                arg.putString("name", StringManager.decapitalize(tv.getText().toString()));
                fragmentAbility.setArguments(arg);
                fragmentAbility.show(getSupportFragmentManager(), FragmentAbility.TAG);
            }
        }

        private void fillAbilities(Pokemon pokemon) {
            List<Ability> abilityList = pokemon.getAbilityList();

            for (int i = 0; i < abilityList.size(); i++) {

                MaterialCardView card = (MaterialCardView) View.inflate(PokemonDetailActivity.this, R.layout.item_ability, null);
                TextView tvAbility = card.findViewById(R.id.tvAbility);

                card.setOnClickListener(this);

                tvAbility.setText(StringManager.capitalize(abilityList.get(i).getName()));

                llAbilities.addView(card);
            }

        }

        private void fillEvolution(EvolutionDetail firstEvolution) {
            // Add card corresponding to first pokemon in EvolutionDetail
            MaterialCardView card = (MaterialCardView) View.inflate(PokemonDetailActivity.this, R.layout.item_evolution, null);
            fillCard(card, firstEvolution);

            llEvolution1.addView(card);
            List<EvolutionDetail> secondEvolutions = firstEvolution.getNextPokemon();

            if (secondEvolutions != null) {
                llEvolution2.setVisibility(View.VISIBLE);
                int secondEvoNumber = 0;

                for (EvolutionDetail secondEvolution : secondEvolutions) {
                    secondEvoNumber++; // Increase the counter of second evolutions
                    // Add card corresponding to second evolution
                    card = (MaterialCardView) View.inflate(PokemonDetailActivity.this,
                            R.layout.item_evolution, null);

                    fillCard(card, secondEvolution);
                    llEvolution2.addView(card);

                    // Add TextView with evolution method
                    TextView tvEvolutionMethod2 = new TextView(PokemonDetailActivity.this);
                    tvEvolutionMethod2.setText(secondEvolution
                            .getEvolutionMethod(PokemonDetailActivity.this));
                    tvEvolutionMethod2.setGravity(Gravity.CENTER);
                    llEvolution2.addView(tvEvolutionMethod2);
                    LinearLayout.LayoutParams llParams2 = (LinearLayout.LayoutParams)
                            tvEvolutionMethod2.getLayoutParams();
                    llParams2.bottomMargin = (int) getResources().getDimension(R.dimen.margin_large);
                    llParams2.topMargin = (int) getResources().getDimension(R.dimen.margin_small);

                    List<EvolutionDetail> thirdEvolutions = secondEvolution.getNextPokemon();

                    if (thirdEvolutions != null) {
                        llEvolution3.setVisibility(View.VISIBLE);
                        int thirdEvoNumber = 0;

                        for (EvolutionDetail thirdEvolution : thirdEvolutions) {
                            thirdEvoNumber++; // Increase the counter of third evolutions
                            // Add card corresponding to third evolution
                            card = (MaterialCardView) View
                                    .inflate(PokemonDetailActivity.this,
                                            R.layout.item_evolution, null);

                            fillCard(card, thirdEvolution);
                            llEvolution3.addView(card);

                            // Add TextView with evolution method
                            TextView tvEvolutionMethod3 = new TextView(PokemonDetailActivity.this);
                            tvEvolutionMethod3.setText(thirdEvolution
                                    .getEvolutionMethod(PokemonDetailActivity.this));
                            tvEvolutionMethod3.setGravity(Gravity.CENTER);
                            llEvolution3.addView(tvEvolutionMethod3);
                            LinearLayout.LayoutParams llParams3 = (LinearLayout.LayoutParams)
                                    tvEvolutionMethod3.getLayoutParams();
                            llParams3.bottomMargin = (int) getResources().getDimension(R.dimen.margin_large);
                            llParams3.topMargin = (int) getResources().getDimension(R.dimen.margin_small);
                        }
                        if (thirdEvoNumber > secondEvoNumber) {
                            // Center vertically other LinearLayouts
                            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)
                                    llEvolution2.getLayoutParams();
                            params.topToTop = R.id.llEvolution1;
                            params.bottomToBottom = 0;
                            params.topMargin = 0;

                            ConstraintLayout.LayoutParams params2 = (ConstraintLayout.LayoutParams)
                                    llEvolution1.getLayoutParams();
                            params2.verticalBias = 0.5f;
                        }
                    }
                }

                if (secondEvoNumber > 1) {
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)
                            llEvolution1.getLayoutParams();
                    params.verticalBias = 0.5f;
                }

            }
        }

        private void fillCard(MaterialCardView card, EvolutionDetail evolutionDetail) {

            Pokemon pokemon = new Pokemon();
            pokemon.setName(evolutionDetail.getName());

            DaoThread daoThread = new DaoThread();
            Handler handler = new Handler();

            TextView tvName = card.findViewById(R.id.tvName);
            ImageView ivPokemon = card.findViewById(R.id.ivPokemon);

            Runnable runnable = () -> {
                ivPokemon.setImageBitmap(imageManager.loadFromDisk(
                        PokemonDetailActivity.this.getFilesDir() + PokemonDetailActivity.this.getString(R.string.sprites_front),
                        pokemon.getId() + PokemonDetailActivity.this.getString(R.string.extension)));
                card.setCardBackgroundColor(Color.parseColor(pokemon.getAverageColor()));

                card.setOnClickListener(v -> {
                    Intent data = new Intent(PokemonDetailActivity.this, PokemonDetailActivity.class);
                    data.putExtra("pokemon", pokemon);

                    startActivityForResult(data, REQUEST_CODE);
                });
            };

            tvName.setText(StringManager.capitalize(pokemon.getName()));

            daoThread.getPokemonFromName(PokemonDetailActivity.this, pokemon, handler, runnable);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // if tap on favorite star
            if (buttonView.getId() == R.id.tbFavorite) {
                if (isChecked) {
                    favoritesManager.addPokemonToFav(pokemon);
                } else {
                    favoritesManager.removePokemonFromFav(pokemon);
                }
                updateIntent();
            }
        }
    }
}