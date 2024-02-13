package com.strangesmell.noguievolution.event;

import com.strangesmell.noguievolution.Config;
import com.strangesmell.noguievolution.NoGuiEvolution;
import com.strangesmell.noguievolution.Util.Utils;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)

public class DamageAbsorbEvent {
    @SubscribeEvent
    public static void damageAbsorbEvent(LivingHurtEvent event){
        LivingEntity livingEntity = event.getEntity();
        if(livingEntity instanceof Player){
            Player player = (Player) livingEntity;
            double count=0;
            if(!player.level().isClientSide){
                ServerPlayer serverPlayer = (ServerPlayer) player;
                count = serverPlayer.getStats().getValue(Stats.CUSTOM.get(Stats.DAMAGE_TAKEN));
                AttributeModifier minedCountModifier = new AttributeModifier(" count ", count, AttributeModifier.Operation.ADDITION);
                serverPlayer.getAttribute(NoGuiEvolution.COUNT_ATTRIBUTE.get()).removeModifiers();
                serverPlayer.getAttribute(NoGuiEvolution.COUNT_ATTRIBUTE.get()).addPermanentModifier(minedCountModifier);
            }else{
                count =(int)player.getAttributeValue(NoGuiEvolution.COUNT_ATTRIBUTE.get());
            }
            double damage = event.getAmount();
            count = Math.min(count, Config.absorbNumberLimit);
            if(Config.isPercentage){
                event.setAmount((float) (damage*((Config.absorbNumberLimit-count*Config.absorbNumberCoefficient)/Config.absorbNumberLimit)));
            }else {
                float newDamge =(float) (damage - count * Config.absorbNumberCoefficient);
                if(newDamge<=0) newDamge =0;
                event.setAmount(newDamge);
            }


        }

    }
}
