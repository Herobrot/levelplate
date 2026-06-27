package com.herobrot.levelplate.compat;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import net.minecraft.world.entity.Mob;

public class CobblemonAPI {
    public static int getLevel(Mob mob, int fallback) {
        if (mob instanceof PokemonEntity pokemon) {
            return pokemon.getPokemon().getLevel();
        }
        return fallback;
    }

    public static String getName(Mob mob, String fallback) {
        if (mob instanceof PokemonEntity pokemon) {
            PokedexEntryProgress progress = CobblemonClient.INSTANCE.getClientPokedexData().getKnowledgeForSpecies(
                    pokemon.getPokemon().getSpecies().getResourceIdentifier()
            );

            if (progress == PokedexEntryProgress.NONE) {
                return "???";
            }
        }
        return fallback;
    }
}