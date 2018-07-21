package cn.mccraft.pangu.core.client.input;

import cn.mccraft.pangu.core.PanguCore;
import cn.mccraft.pangu.core.loader.AnnotationInjector;
import cn.mccraft.pangu.core.loader.AnnotationStream;
import cn.mccraft.pangu.core.loader.AutoWired;
import cn.mccraft.pangu.core.loader.InstanceHolder;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

@SideOnly(Side.CLIENT)
@AutoWired(registerCommonEventBus = true)
public class KeyBindingInjector {
    private Map<KeyBinding, Method> bindingKeys = Maps.newHashMap();

    @AnnotationInjector.StaticInvoke
    public void bind(AnnotationStream<BindKeyPress> stream) {
        stream.methodStream()
                .filter(method -> method.getParameterCount() == 0)
                .forEach(method -> {
                    // check if there is an instance to invoke
                    if (!Modifier.isStatic(method.getModifiers()) && InstanceHolder.getCachedInstance(method.getDeclaringClass()) == null) {
                        PanguCore.getLogger().error("Unable to find any instance to bind key for method " + method.toString(), new NullPointerException());
                    }
                    // get annotation info
                    BindKeyPress bindKeyPress = method.getAnnotation(BindKeyPress.class);
                    // register key binding
                    KeyBinding key = KeyBindingHelper.of(bindKeyPress.description(), bindKeyPress.keyCode(), bindKeyPress.category());
                    // put into cache
                    bindingKeys.put(key, method);
                    System.out.println("Binding key " + bindKeyPress + " to method " + method);
                });

    }

    @SubscribeEvent
    public void handleKey(GuiScreenEvent.KeyboardInputEvent e) {
        handleKeyPress();
    }

    @SubscribeEvent
    public void handleKey(InputEvent.KeyInputEvent e) {
        handleKeyPress();
    }

    public void handleKeyPress() {
        for (Map.Entry<KeyBinding, Method> entry : bindingKeys.entrySet())
            // check if pressed
            if (entry.getKey().isPressed()) try {
                entry.getValue().invoke(InstanceHolder.getCachedInstance(entry.getValue().getDeclaringClass()));
            } catch (Exception e) {
                // catch all exception
                PanguCore.getLogger().error("Unable to bind key input for " + entry.getKey().getKeyDescription(), e);
            }
    }

    @BindKeyPress(description = "key.test", keyCode = Keyboard.KEY_O, category = KeyBindingHelper.CATEGORY_MISC)
    public void test() {
        System.out.println(1);
    }
}
