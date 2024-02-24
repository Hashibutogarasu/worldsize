package io.github.hashibutogarasu.worldsize.mixin.client;

import io.github.hashibutogarasu.worldsize.util.Util;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.text.Text;
import net.minecraft.world.level.storage.LevelSummary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;

@Mixin(WorldListWidget.WorldEntry.class)
public abstract class MixinSelectWorldScreen {
	@Shadow @Final
	LevelSummary level;
	@Unique
	private String size = "";

	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(WorldListWidget worldListWidget, WorldListWidget levelList, @NotNull LevelSummary level, CallbackInfo ci) {
        try {
            long size = Util.size(new File(level.getIconPath().getParent().toUri()));
        	this.size = Util.convertToStringRepresentation(size);
		} catch (IOException ignored) {

        }
    }

	@Redirect(method = "render",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I"))
	private int render(DrawContext context, TextRenderer textRenderer, String text, int x, int y, int color, boolean shadow) {
        long lastplayedvalue = level.getLastPlayed();
		String lastplayed = level.getName();
		if (lastplayedvalue != -1L) {
			lastplayed = lastplayed + " (" + WorldListWidget.DATE_FORMAT.format(Instant.ofEpochMilli(lastplayedvalue)) + ")";
		}
		if(Objects.equals(text, lastplayed)){
			return context.drawText(textRenderer, text + " " + Text.translatable("worldsize.approx", this.size).getString(), x, y, color, shadow);
		}
		return context.drawText(textRenderer, text, x, y, color, shadow);
	}
}