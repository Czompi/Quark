package vazkii.quark.base.client;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConfirmOpenLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ServerListScreen;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import vazkii.quark.base.Quark;

@EventBusSubscriber(modid = Quark.MOD_ID)
@OnlyIn(Dist.CLIENT)
public class BLMHandler {

	private static boolean didTheThing = false;

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void clientTick(ClientTickEvent event) {
		didTheThing = false;

		Minecraft mc = Minecraft.getInstance();
		if(!didTheThing && mc.getLanguageManager().getCurrentLanguage().getName().equals("English")) {
			Screen curr = mc.currentScreen;

			if(curr instanceof WorldSelectionScreen || curr instanceof ServerListScreen) {
				mc.displayGuiScreen(new BLMScreen());
				didTheThing = true;
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	private static class BLMScreen extends Screen {

		private static final int TOTAL_TIME = (8 * 60 * 20) + (46 * 20);

		int ticksElapsed = 0;
		boolean attemptedEsc = false;
		boolean openedWebsite = false;

		protected BLMScreen() {
			super(new StringTextComponent(""));
		}

		@Override
		public void render(int mx, int my, float pticks) {
			super.render(mx, my, pticks);

			fill(0, 0, width, height, 0xFF000000);
			int middle = width / 2;

			int timeLeft = TOTAL_TIME - ticksElapsed;
			int secs = timeLeft / 20;
			int mins = secs / 60; 
			secs -= (mins * 60);

			RenderSystem.pushMatrix();
			RenderSystem.scalef(3, 3, 3);
			drawCenteredString(font, String.format("%dm%02ds", mins, secs), middle / 3, 10, 0xFFFFFF);
			RenderSystem.popMatrix();
			
			String[] message = new String[0];
			int dist = 15;

			if(attemptedEsc) {
				message = new String[] {
						"Before you go, remember: You can skip this.",
						"George Floyd couldn't.",
						"While you're waiting to play, he was waiting to die.",
						"",
						"Black people are disproportionately killed by the police",
						"in the United States. It's time to stand up.",
						"Click anywhere on screen to find out how to help.",
						"#BlackLivesMatter",
						"(Press ESC again to leave)"
				};
			} else {
				message = new String[] {
						"Before you start playing, please read this message provided by Quark.",
						"George Floyd was killed by a police officer, who stood on him for 8m46s.",
						"All he'd done was attempt to pay with a fake $20 bill.",
						"Innocent black lives are being taken by police officers all over the USA.",
						"It's time to make history.",
						"",
						"Click anywhere on screen to find out how to help.",
						"#BlackLivesMatter",
						"",
						"(You may press ESC to skip)"
				};
			}
			
			for(int i = 0; i < message.length; i++) {
				if(attemptedEsc || (ticksElapsed - 20) > i * 70) 
					drawCenteredString(font, message[i], middle, 70 + i * dist, 0xFFFFFF);
			}
		}

		@Override
		public void tick() {
			super.tick();

			ticksElapsed++;
			if(ticksElapsed > TOTAL_TIME)
				minecraft.displayGuiScreen(null);
		}

		@Override
		public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
			if(p_keyPressed_1_ == 256 && !attemptedEsc) {
				attemptedEsc = true;
				return false;
			}

			return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
		}

		@Override
		public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
			if(p_mouseClicked_5_ == 0 && !openedWebsite) {
				minecraft.displayGuiScreen(new ConfirmOpenLinkScreen(this::consume, "https://blacklivesmatter.carrd.co/", true));
				return true;
			}
			
			return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
		}
		
		private void consume(boolean b) {
			minecraft.displayGuiScreen(this);
			if(b)
				Util.getOSType().openURI("https://blacklivesmatter.carrd.co/");
			openedWebsite = b;
		}
		
	}


}
