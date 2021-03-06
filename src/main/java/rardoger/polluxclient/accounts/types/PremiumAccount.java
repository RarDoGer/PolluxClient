/*
 * This file is part of the Pollux Client distribution (https://github.com/PolluxDevelopment/pollux-client/).
 * Copyright (c) 2020 Pollux Development.
 */

package rardoger.polluxclient.accounts.types;

import com.google.gson.Gson;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import rardoger.polluxclient.accounts.Account;
import rardoger.polluxclient.accounts.AccountType;
import rardoger.polluxclient.accounts.ProfileResponse;
import rardoger.polluxclient.accounts.ProfileSkinResponse;
import rardoger.polluxclient.mixininterface.IMinecraftClient;
import rardoger.polluxclient.utils.misc.NbtException;
import rardoger.polluxclient.utils.network.HttpUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import net.minecraft.nbt.CompoundTag;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class PremiumAccount extends Account<PremiumAccount> {
    private static final Gson GSON = new Gson();

    private String password;

    public PremiumAccount(String name, String password) {
        super(AccountType.Premium, name);
        this.password = password;
    }

    @Override
    public boolean fetchInfo() {
        YggdrasilUserAuthentication auth = getAuth();

        try {
            auth.logIn();

            cache.username = auth.getSelectedProfile().getName();
            cache.uuid = auth.getSelectedProfile().getId().toString();

            return true;
        } catch (AuthenticationException e) {
            return false;
        }
    }

    @Override
    public boolean fetchHead() {
        String skinUrl = null;
        ProfileResponse response = HttpUtils.get("https://sessionserver.mojang.com/session/minecraft/profile/" + cache.uuid, ProfileResponse.class);
        String encodedTexturesJson = response.getTextures();
        if (encodedTexturesJson != null) {
            ProfileSkinResponse skin = GSON.fromJson(new String(Base64.getDecoder().decode(encodedTexturesJson), StandardCharsets.UTF_8), ProfileSkinResponse.class);
            if (skin.textures.SKIN != null) skinUrl = skin.textures.SKIN.url;
        }
        if (skinUrl == null) skinUrl = "https://polluxclient.com/steve.png";
        return cache.makeHead(skinUrl);
    }

    @Override
    public boolean login() {
        super.login();

        YggdrasilUserAuthentication auth = getAuth();

        try {
            auth.logIn();
            setSession(new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang"));

            cache.username = auth.getSelectedProfile().getName();
            return true;
        } catch (AuthenticationUnavailableException e) {
            System.out.println("[Pollux] Failed to contact the authentication server.");
            return false;
        } catch (AuthenticationException e) {
            if (e.getMessage().contains("Invalid username or password") || e.getMessage().contains("account migrated")) System.out.println("[Pollux] Wrong password.");
            else System.out.println("[Pollux] Failed to contact the authentication server.");
            return false;
        }
    }

    private YggdrasilUserAuthentication getAuth() {
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(((IMinecraftClient) MinecraftClient.getInstance()).getProxy(), "").createUserAuthentication(Agent.MINECRAFT);

        auth.setUsername(name);
        auth.setPassword(password);

        return auth;
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = super.toTag();

        tag.putString("password", password);

        return tag;
    }

    @Override
    public PremiumAccount fromTag(CompoundTag tag) {
        super.fromTag(tag);
        if (!tag.contains("password")) throw new NbtException();

        password = tag.getString("password");

        return this;
    }
}
