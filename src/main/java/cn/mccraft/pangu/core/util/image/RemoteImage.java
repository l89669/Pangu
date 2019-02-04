package cn.mccraft.pangu.core.util.image;

import cn.mccraft.pangu.core.PanguCore;
import cn.mccraft.pangu.core.util.LocalCache;
import com.trychen.bytedatastream.ByteDeserializable;
import com.trychen.bytedatastream.ByteSerializable;
import com.trychen.bytedatastream.DataOutput;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class RemoteImage implements TextureProvider, ByteDeserializable, ByteSerializable {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    @Getter
    private final String urlPath;

    @Getter
    private final URL url;

    @Getter
    private String id;

    @Getter
    private File cachedFilePath;

    @Getter
    private DynamicTexture dynamicTexture;

    private Future<BufferedImage> bufferedImage;
    private ResourceLocation resourceLocation;

    private RemoteImage(String urlPath) throws MalformedURLException {
        this.urlPath = urlPath;
        this.url = new URL(urlPath);
        this.id = Base64.getEncoder().encodeToString(urlPath.getBytes());
        this.cachedFilePath = LocalCache.getCachePath("images", id);

        bufferedImage = EXECUTOR.submit(() -> {
                if (!cachedFilePath.exists()) {
                    PanguCore.getLogger().info("Start fetching image from " + url.toString());
                    DataInputStream dataInputStream = new DataInputStream(url.openStream());
                    FileOutputStream fileOutputStream = new FileOutputStream(cachedFilePath);
                    ByteArrayOutputStream output = new ByteArrayOutputStream();

                    byte[] buffer = new byte[1024];
                    int length;

                    while ((length = dataInputStream.read(buffer)) > 0) {
                        output.write(buffer, 0, length);
                    }
                    fileOutputStream.write(output.toByteArray());
                    fileOutputStream.flush();
                    dataInputStream.close();
                    fileOutputStream.close();
                    PanguCore.getLogger().info("Saved " + urlPath + " to " + cachedFilePath.getAbsolutePath());
                } else PanguCore.getLogger().info("Loading image " + urlPath + " from local " + cachedFilePath.getAbsolutePath());
                return ImageIO.read(cachedFilePath);
            });
    }

    @Override
    public ResourceLocation getTexture() {
        if (!bufferedImage.isDone()) return null;
        if (resourceLocation == null) {
            LocalCache.markFileUsed(cachedFilePath.toPath());
            try {
                resourceLocation = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("custommenu_banner_" + id, this.dynamicTexture = new DynamicTexture(this.bufferedImage.get()));
            } catch (Exception e) {
                PanguCore.getLogger().error("Couldn't load remote image from url " + url.toString(), e);
                bufferedImage = new Future<BufferedImage>() {
                    @Override
                    public boolean cancel(boolean mayInterruptIfRunning) {
                        return false;
                    }

                    @Override
                    public boolean isCancelled() {
                        return false;
                    }

                    @Override
                    public boolean isDone() {
                        return false;
                    }

                    @Override
                    public BufferedImage get() {
                        return null;
                    }

                    @Override
                    public BufferedImage get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                        return null;
                    }
                };
                return null;
            }
        }
        return resourceLocation;
    }

    @Override
    public ResourceLocation getTexture(ResourceLocation loading) {
        ResourceLocation texture = getTexture();
        if (texture == null) return loading;
        return texture;
    }

    @Override
    public ResourceLocation getTexture(ResourceLocation loading, ResourceLocation error) {
        if (!bufferedImage.isDone()) return loading;
        return getTexture(error);
    }

    public BufferedImage getBufferedImage() {
        if (!bufferedImage.isDone()) return null;
        try {
            return bufferedImage.get();
        } catch (Exception e) {
        }
        return null;
    }

    private static Map<String, RemoteImage> cachedImages = new HashMap();

    public static TextureProvider of(String url, ResourceLocation missingTexture) {
        RemoteImage image = of(url);
        if (image == null) return new BuiltinImage(missingTexture);
        return image;
    }

    public static RemoteImage of(String url) {
        RemoteImage remoteImage = cachedImages.get(url);
        if (remoteImage == null) {
            try {
                remoteImage = new RemoteImage(url);
                cachedImages.put(url, remoteImage);
            } catch (Exception e) {
                PanguCore.getLogger().error("Couldn't load remote resourceLocation",  e);
                return null;
            }
        }
        return remoteImage;
    }

    @Override
    public void serialize(DataOutput out) throws IOException {
        out.writeUTF(urlPath);
    }

    public static RemoteImage deserialize(DataInput out) throws IOException {
        return of(out.readUTF());
    }
}