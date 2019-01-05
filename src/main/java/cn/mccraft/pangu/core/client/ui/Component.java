package cn.mccraft.pangu.core.client.ui;

import cn.mccraft.pangu.core.util.render.Rect;
import javax.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.Minecraft;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Accessors(chain = true)
public abstract class Component implements Cloneable, Comparable<Component> {
  @Getter
  @Setter
  protected Component parent;

  @Getter
  @Setter
  protected int zLevel = 100;

  @Getter
  protected int height = 0, width = 0;

  @Getter
  protected float x = 0, y = 0;

  @Getter
  @Setter
  protected boolean hovered = false, visible = true, disabled = false;

  public Component() {
  }

  public void onDraw(float partialTicks, int mouseX, int mouseY) {}

  public void onMousePressed(int mouseButton, int mouseX, int mouseY) {}

  public void onMouseReleased(int mouseX, int mouseY) {}

  public void onKeyTyped(char typedChar, int keyCode) {}

  public void onUpdate(int mouseX, int mouseY) {
    this.hovered = isHovered(mouseX, mouseY);
  }

  public boolean isHovered(int mouseX, int mouseY) {
    return mouseX >= this.x
        && mouseY >= this.y
        && mouseX < this.x + this.width
        && mouseY < this.y + this.height;
  }

  public Component setPosition(float x, float y) {
    this.x = x;
    this.y = y;
    return this;
  }

  public Component setSize(int width, int height) {
    this.width = width;
    this.height = height;
    return this;
  }


  public void bindTexture(ResourceLocation resourceLocation) {
    Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
  }

  @Nullable
  public NonNullList<String> getToolTip() {
    return null;
  }

  public void drawComponentBox() {
    Rect.drawBox(x, y, width, height, 0xFFFFFFFF);
  }

  @Override
  public int compareTo(Component o) {
    return Integer.compare(this.getZLevel(), o.getZLevel());
  }
}
