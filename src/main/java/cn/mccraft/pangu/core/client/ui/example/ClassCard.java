package cn.mccraft.pangu.core.client.ui.example;

import cn.mccraft.pangu.core.client.ui.Button;
import cn.mccraft.pangu.core.util.font.DefaultFontProvider;
import cn.mccraft.pangu.core.util.image.TextureProvider;
import cn.mccraft.pangu.core.util.render.Rect;
import cn.mccraft.pangu.core.util.resource.PanguResLoc;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ClassCard extends Button {
    public ResourceLocation texture = PanguResLoc.ofGui("class_choose.png");

    private String text;
    private TextureProvider icon;
    private int u, v;
    private List<String> toolTips = new ArrayList<String>() {{
        add("Hello");
    }};

    public ClassCard() {
        this("");
    }

    public ClassCard(String text) {
        this(text, 65, 80,  null, 0, 0);
    }

    public ClassCard(String name, int width, int height, TextureProvider textureProvider, int u, int v) {
        super(width, height);
        this.text = name;
        this.icon = textureProvider;
        this.u = u;
        this.v = v;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDraw(float partialTicks, int mouseX, int mouseY) {
        Rect.startDrawing();
        if (isDisabled()) GlStateManager.color(1, 1, 1, 0.5F);
        Rect.bind(texture);

        Rect.drawTextured(
                x, y,
                0, 0,
                width, height);

        if (icon != null && icon.getTexture() != null) {
            Rect.bind(icon.getTexture());

            Rect.drawTextured(
                    x, y,
                    u, v,
                    width, height);
        }
        DefaultFontProvider.INSTANCE.drawCenteredString(text, x + width / 2, y + 60, isDisabled()?0x888888:(isHovered()?0x2CC0A7:0xDDDDDD), false);
    }

    public String getText() {
        return text;
    }

    public ClassCard setText(String text) {
        this.text = text;
        return this;
    }

    public TextureProvider getIcon() {
        return icon;
    }

    public int getU() {
        return u;
    }

    public int getV() {
        return v;
    }

    @Nullable
    @Override
    public List<String> getToolTips() {
        return toolTips;
    }
}
