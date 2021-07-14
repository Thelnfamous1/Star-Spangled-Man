package com.infamous.captain_america.client.sound;

import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;
import com.infamous.captain_america.common.util.FalconFlightHelper;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HoverSound extends TickableSound {
   private final ClientPlayerEntity player;
   private int time;

   public HoverSound(ClientPlayerEntity clientPlayer) {
      super(SoundEvents.ELYTRA_FLYING, SoundCategory.PLAYERS);
      this.player = clientPlayer;
      this.looping = true;
      this.delay = 0;
      this.volume = 0.1F;
   }

   public void tick() {
      ++this.time;
      IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(this.player);
      if(falconAbilityCap == null){
         this.stop();
         return;
      }
      boolean isHovering = falconAbilityCap.isHovering() && FalconFlightHelper.canHover(this.player);
      if (this.player.isAlive() && (this.time <= 20 || isHovering)) {
         this.x = (double)((float)this.player.getX());
         this.y = (double)((float)this.player.getY());
         this.z = (double)((float)this.player.getZ());
         float deltaMoveLengthSqr = (float)this.player.getDeltaMovement().lengthSqr();
         if ((double)deltaMoveLengthSqr >= 1.0E-7D) {
            this.volume = MathHelper.clamp(deltaMoveLengthSqr / 4.0F, 0.0F, 1.0F);
         } else {
            this.volume = 0.0F;
         }

         if (this.time < 20) {
            this.volume = 0.0F;
         } else if (this.time < 40) {
            this.volume = (float)((double)this.volume * ((double)(this.time - 20) / 20.0D));
         }

         float volumeOffset = 0.8F;
         if (this.volume > volumeOffset) {
            this.pitch = 1.0F + (this.volume - volumeOffset);
         } else {
            this.pitch = 1.0F;
         }

      } else {
         this.stop();
      }
   }
}