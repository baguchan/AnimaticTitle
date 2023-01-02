package baguchan.animatic_title.mixin.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
	@Shadow
	@Final
	public static Component COPYRIGHT_TEXT;

	@Shadow
	@Final
	private static ResourceLocation PANORAMA_OVERLAY;
	@Shadow
	@Final
	private boolean minceraftEasterEgg;
	@Nullable
	@Shadow
	private String splash;
	@Shadow
	@Final
	private static ResourceLocation MINECRAFT_LOGO;
	@Shadow
	@Final
	private static ResourceLocation MINECRAFT_EDITION;
	@Nullable
	@Shadow
	private RealmsNotificationsScreen realmsNotificationsScreen;
	@Shadow
	@Final
	private PanoramaRenderer panorama;
	@Shadow
	@Final
	private boolean fading;

	@Shadow
	private long fadeInStart;

	private RevampWarningLabel warningLabel;
	@Shadow(remap = false)
	private net.minecraftforge.client.gui.TitleScreenModUpdateIndicator modUpdateNotification;

	private int animationTick;

	protected TitleScreenMixin(Component p_96550_) {
		super(p_96550_);
	}

	@Inject(method = "init", at = @At("HEAD"))
	protected void init(CallbackInfo callbackInfo) {
		int i = this.font.width(COPYRIGHT_TEXT);
		int l = this.height / 4 + 48;
		if (!this.minecraft.is64Bit()) {
			this.warningLabel = new RevampWarningLabel(this.font, MultiLineLabel.create(this.font, Component.translatable("title.32bit.deprecation"), 350, 2), this.width / 2, l - 24);
		}
	}
	@Inject(method = "tick", at = @At("HEAD"))
	public void tick(CallbackInfo callbackInfo) {
		float f = this.fading ? (float)(Util.getMillis() - this.fadeInStart) / 1000.0F : 1.0F;

		if(f >= 1.0F) {
			++this.animationTick;
		}
	}

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	public void render(PoseStack p_96739_, int p_96740_, int p_96741_, float p_96742_, CallbackInfo callbackInfo) {
		if (this.fadeInStart == 0L && this.fading) {
			this.fadeInStart = Util.getMillis();
		}

		float f = this.fading ? (float)(Util.getMillis() - this.fadeInStart) / 1000.0F : 1.0F;
		this.panorama.render(p_96742_, Mth.clamp(f, 0.0F, 1.0F));
		int i = 274;
		int j = this.width / 2 - 137;
		int k = 30;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, PANORAMA_OVERLAY);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.fading ? (float)Mth.ceil(Mth.clamp(f, 0.0F, 1.0F)) : 1.0F);
		blit(p_96739_, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
		float f1 = this.fading ? Mth.clamp(f - 1.0F, 0.0F, 1.0F) : 1.0F;
		int l = Mth.ceil(f1 * 255.0F) << 24;
		if ((l & -67108864) != 0) {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, MINECRAFT_LOGO);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, f1);

			p_96739_.pushPose();
			p_96739_.translate(0, (Mth.cos(animationTick * 0.05F) * 5),0);
			if (this.minceraftEasterEgg) {
				this.blitOutlineBlack(j, (int) (30 * f1), (p_232776_, p_232777_) -> {
					this.blit(p_96739_, p_232776_ + 0, p_232777_, 0, 0, 99, 44);
					this.blit(p_96739_, p_232776_ + 99, p_232777_, 129, 0, 27, 44);
					this.blit(p_96739_, p_232776_ + 99 + 26, p_232777_, 126, 0, 3, 44);
					this.blit(p_96739_, p_232776_ + 99 + 26 + 3, p_232777_, 99, 0, 26, 44);
					this.blit(p_96739_, p_232776_ + 155, p_232777_, 0, 45, 155, 44);
				});
			} else {
				this.blitOutlineBlack(j, (int) (30 * f1), (p_210862_, p_210863_) -> {
					this.blit(p_96739_, p_210862_ + 0, p_210863_, 0, 0, 155, 44);
					this.blit(p_96739_, p_210862_ + 155, p_210863_, 0, 45, 155, 44);
				});
			}
			p_96739_.popPose();


			RenderSystem.setShaderTexture(0, MINECRAFT_EDITION);
			p_96739_.pushPose();
			p_96739_.translate(0, (Mth.cos(animationTick * 0.05F) * 5),0);
			//y 67
			blit(p_96739_, j + 88, (int) (37 + (30 * f1)), 0.0F, 0.0F, 98, 14, 128, 16);

			p_96739_.popPose();

			if (this.warningLabel != null) {
				this.warningLabel.render(p_96739_, l);
			}

			TitleScreen titleScreen = (TitleScreen) ((Object) this);
			net.minecraftforge.client.ForgeHooksClient.renderMainMenu(titleScreen, p_96739_, this.font, this.width, this.height, l);
			if (this.splash != null) {
				p_96739_.pushPose();
				p_96739_.translate((double)(this.width / 2 + 90), 70.0D, 0.0D);
				p_96739_.mulPose(Axis.ZP.rotationDegrees(-20.0F));
				float f2 = 1.8F - Mth.abs(Mth.sin((float)(Util.getMillis() % 1000L) / 1000.0F * ((float)Math.PI * 2F)) * 0.1F);
				f2 = f2 * 100.0F / (float)(this.font.width(this.splash) + 32);
				p_96739_.scale(f2, f2, f2);
				drawCenteredString(p_96739_, this.font, this.splash, 0, -8, 16776960 | l);
				p_96739_.popPose();
			}

			String s = "Minecraft " + SharedConstants.getCurrentVersion().getName();
			if (this.minecraft.isDemo()) {
				s = s + " Demo";
			} else {
				s = s + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType());
			}

			if (Minecraft.checkModStatus().shouldReportAsModified()) {
				s = s + I18n.get("menu.modded");
			}

			net.minecraftforge.internal.BrandingControl.forEachLine(true, true, (brdline, brd) ->
					drawString(p_96739_, this.font, brd, 2, this.height - ( 10 + brdline * (this.font.lineHeight + 1)), 16777215 | l)
			);

			net.minecraftforge.internal.BrandingControl.forEachAboveCopyrightLine((brdline, brd) ->
					drawString(p_96739_, this.font, brd, this.width - font.width(brd), this.height - (10 + (brdline + 1) * ( this.font.lineHeight + 1)), 16777215 | l)
			);


			for(GuiEventListener guieventlistener : this.children()) {
				if (guieventlistener instanceof AbstractWidget) {
					((AbstractWidget)guieventlistener).setAlpha(f1);
				}
			}

			super.render(p_96739_, p_96740_, p_96741_, p_96742_);
			if (this.realmsNotificationsEnabled() && f1 >= 1.0F) {
				RenderSystem.enableDepthTest();
				this.realmsNotificationsScreen.render(p_96739_, p_96740_, p_96741_, p_96742_);
			}
			if (f1 >= 1.0f) modUpdateNotification.render(p_96739_, p_96740_, p_96741_, p_96742_);

		}
		callbackInfo.cancel();
	}

	@Shadow
	private boolean realmsNotificationsEnabled() {
		return false;
	}
	@OnlyIn(Dist.CLIENT)
	static record RevampWarningLabel(Font font, MultiLineLabel label, int x, int y) {
		public void render(PoseStack p_232791_, int p_232792_) {
			this.label.renderBackgroundCentered(p_232791_, this.x, this.y, 9, 2, 1428160512);
			this.label.renderCentered(p_232791_, this.x, this.y, 9, 16777215 | p_232792_);
		}
	}

}
