package cn.mccraft.pangu.core.util.image;

import cn.mccraft.pangu.core.PanguCore;
import cn.mccraft.pangu.core.util.render.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.io.File;

public interface TextureProvider {
    int[] ID = new int[]{0};

    static TextureProvider of(String path, ResourceLocation missing) {
        if (path.startsWith("http://") || path.startsWith("https://")) {
            if (path.endsWith(".gif")) return RemoteGif.of(path, missing);
            else return RemoteImage.of(path, missing);
        } else if (path.startsWith("location:")) {
            return new BuiltinImage(new ResourceLocation(path.substring(9)));
        } else if (path.startsWith("file:")) {
            File file = new File(path.substring(5));
            if (!file.exists()) {
                PanguCore.getLogger().error("couldn't load image from " + path + ", file not exists");
            } else {
                try {
                    ResourceLocation location = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("pangu_file_image_" + ID[0], new DynamicTexture(ImageIO.read(file)));
                    ID[0]++;
                    return new BuiltinImage(location);
                } catch (Exception e) {
                    PanguCore.getLogger().error("error while loading image from file " + path, e);
                }
            }
        } else if (path.startsWith("gif:")) {
            return new BuiltinGif(new ResourceLocation(path.substring("gif:".length())));
        }

        return new BuiltinImage(path);
    }

    default ResourceLocation getTexture() {
        return getTexture(null, null);
    }

    default ResourceLocation getTexture(ResourceLocation loading) {
        return getTexture(loading, loading);
    }

    default ResourceLocation getTexture(ResourceLocation loading, ResourceLocation error) {
        return error;
    }

    default int getTextureID() {
        return 0;
    }

    default boolean isReady() {
        return true;
    }

    default boolean isLoaded() {
        return true;
    }

    default void bind() {
        Rect.bind(this.getTexture());
    }

    default void bind(ResourceLocation loading) {
        Rect.bind(this.getTexture(loading));
    }

    default void bind(ResourceLocation loading, ResourceLocation error) {
        Rect.bind(this.getTexture(loading, error));
    }

    default TextureAtlasSprite asAtlasSprite() {
        return null;
    }

    default void remove() {
    }

    default void refresh() {
    }

    default boolean free() {
        return false;
    }

    default int getWidth() {
        return 0;
    }

    default int getHeight() {
        return 0;
    }
}