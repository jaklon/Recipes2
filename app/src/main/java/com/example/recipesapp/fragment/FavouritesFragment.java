package com.example.recipesapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.recipes.databinding.FragmentFavouritesBinding;
import com.example.recipesapp.adapters.RecipeAdapter;
import com.example.recipesapp.models.FavouriteRecipe;
import com.example.recipesapp.models.Recipe;
import com.example.recipesapp.room.RecipeRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FavouritesFragment extends Fragment {
    FragmentFavouritesBinding binding;
    RecipeRepository recipesRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadFavorites();
    }

    private void loadFavorites() {
        recipesRepository = new RecipeRepository(requireActivity().getApplication());
        List<FavouriteRecipe> favouriteRecipes = recipesRepository.getAllFavourites();
        if (favouriteRecipes.isEmpty()) {
            Toast.makeText(requireContext(), "No Favourites", Toast.LENGTH_SHORT).show();
            binding.rvFavourites.setVisibility(View.GONE);
        } else {
            binding.rvFavourites.setLayoutManager(new GridLayoutManager(requireContext(), 2));
            binding.rvFavourites.setAdapter(new RecipeAdapter());
            List<Recipe> recipes = new ArrayList<>();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Recipes");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChildren()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            for (FavouriteRecipe favouriteRecipe : favouriteRecipes) {
                                if (dataSnapshot.getKey().equals(favouriteRecipe.getRecipeId())) {
                                    recipes.add(dataSnapshot.getValue(Recipe.class));
                                }
                            }
                        }
                        binding.rvFavourites.setVisibility(View.VISIBLE);
                        RecipeAdapter adapter = (RecipeAdapter) binding.rvFavourites.getAdapter();
                        if (adapter != null) {
                            adapter.setRecipeList(recipes);
                        }

                    } else {
                        binding.rvFavourites.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FavouritesFragment", "onCancelled: " + error.getMessage());
                }
            });
        }
    }
}