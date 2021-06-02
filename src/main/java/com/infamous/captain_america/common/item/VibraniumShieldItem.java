package com.infamous.captain_america.common.item;

import com.infamous.captain_america.client.renderer.VibraniumShieldISTER;
import com.infamous.captain_america.common.advancements.CACriteriaTriggers;
import com.infamous.captain_america.common.entity.VibraniumShieldEntity2;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.stats.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class VibraniumShieldItem extends ShieldItem {

    public static final Predicate<Item> SHIELD_PREDICATE =
            item -> item instanceof VibraniumShieldItem;
    public static final Predicate<Enchantment> ENCHANTMENT_PREDICATE =
            enchantment -> enchantment == Enchantments.POWER_ARROWS
                    || enchantment == Enchantments.PUNCH_ARROWS
                    || enchantment == Enchantments.LOYALTY;

    private final Supplier<EntityType<? extends VibraniumShieldEntity2>> registeredShieldType;

    public VibraniumShieldItem(Supplier<EntityType<? extends VibraniumShieldEntity2>> registeredShieldType,
                               Properties properties) {
        super(properties
                .stacksTo(1)
                .fireResistant()
                .setISTER(VibraniumShieldItem::getISTER));
        this.registeredShieldType = registeredShieldType;
    }

    public boolean throwShield(ItemStack itemStack, World world, LivingEntity thrower, VibraniumShieldEntity2.ThrowType throwType, int shieldCharge) {
        if (thrower instanceof PlayerEntity) {
            PlayerEntity playerThrower = (PlayerEntity)thrower;

            if (!itemStack.isEmpty()) {
                float shieldChargingScale = shieldCharge / 100.0F;

                boolean threwShield = false;
                if (!world.isClientSide) {

                    Optional<? extends VibraniumShieldEntity2> optionalShieldEntity =
                            createShield(world, itemStack, playerThrower, throwType);
                    if(!optionalShieldEntity.isPresent()) return false;

                    VibraniumShieldEntity2 shieldEntity = optionalShieldEntity.get();

                    float offset = 0.0F;
                    Vector3d upVector = thrower.getUpVector(1.0F);
                    Quaternion quaternion = new Quaternion(new Vector3f(upVector), offset, true);
                    Vector3d viewVector = thrower.getViewVector(1.0F);
                    Vector3f viewVectorFloat = new Vector3f(viewVector);
                    viewVectorFloat.transform(quaternion);
                    shieldEntity.shoot(
                            (double)viewVectorFloat.x(),
                            (double)viewVectorFloat.y(),
                            (double)viewVectorFloat.z(),
                            shieldChargingScale * getThrowFactor(thrower, itemStack),
                            offset);

                    if (shieldChargingScale >= 0.9F) {
                        shieldEntity.setCritShield(true);
                    }

                    int powerLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, itemStack);
                    if(isSuperSoldier(thrower)) powerLevel++;
                    if (powerLevel > 0) {
                        shieldEntity.setBaseDamage(shieldEntity.getBaseDamage() + (double)powerLevel * 0.5D + 0.5D);
                    }

                    int punchLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, itemStack);
                    if(isSuperSoldier(thrower)) punchLevel++;
                    if (punchLevel > 0) {
                        shieldEntity.setKnockback(punchLevel);
                    }

                    if (playerThrower.abilities.instabuild) {
                        shieldEntity.pickup = VibraniumShieldEntity2.PickupStatus.CREATIVE_ONLY;
                    }

                    threwShield = world.addFreshEntity(shieldEntity);
                    if(threwShield){
                        ItemStack singletonShield = itemStack.split(1);
                        shieldEntity.setShieldItem(singletonShield);

                        if (thrower instanceof ServerPlayerEntity) {
                            ServerPlayerEntity serverPlayer = (ServerPlayerEntity)thrower;
                            CACriteriaTriggers.THREW_SHIELD.trigger(serverPlayer, singletonShield);
                            serverPlayer.awardStat(Stats.ITEM_USED.get(singletonShield.getItem()));
                        }
                    }
                }

                world.playSound((PlayerEntity)null,
                        playerThrower.getX(),
                        playerThrower.getY(),
                        playerThrower.getZ(),
                        SoundEvents.SNOWBALL_THROW,
                        SoundCategory.PLAYERS,
                        1.0F,
                        1.0F / (random.nextFloat() * 0.4F + 1.2F) + shieldChargingScale * 0.5F);

                return threwShield;
            }
        }
        return false;
    }

    private static boolean isSuperSoldier(LivingEntity thrower) {
        return false;
    }

    private static float getThrowFactor(LivingEntity thrower, ItemStack stack) {
        return isSuperSoldier(thrower) ? 6.0F : 3.0F;
    }

    public static boolean hasVibraniumShield(LivingEntity living) {
        return living.isHolding(SHIELD_PREDICATE);
    }

    public static Hand getShieldHoldingHand(LivingEntity living) {
        return SHIELD_PREDICATE.test(living.getMainHandItem().getItem()) ? Hand.MAIN_HAND : Hand.OFF_HAND;
    }

    public static Optional<? extends VibraniumShieldEntity2> createShield(World world, ItemStack stack, LivingEntity thrower, VibraniumShieldEntity2.ThrowType throwType){
        Optional<EntityType<? extends VibraniumShieldEntity2>> shieldType = getShieldType(stack);
        if(shieldType.isPresent()){
            VibraniumShieldEntity2 shieldEntity = new VibraniumShieldEntity2(shieldType.get(), thrower, world, throwType);
            return Optional.of(shieldEntity);
        }
        return Optional.empty();
    }

    public static Optional<EntityType<? extends VibraniumShieldEntity2>> getShieldType(ItemStack stack){
        Item item = stack.getItem();
        if(item instanceof VibraniumShieldItem){
            VibraniumShieldItem shieldItem = (VibraniumShieldItem) item;
            return Optional.of(shieldItem.getShieldType());
        }
        else return Optional.empty();
    }

    public EntityType<? extends VibraniumShieldEntity2> getShieldType(){
        return this.registeredShieldType.get();
    }


    public static Optional<VibraniumShieldItem> getShieldItem(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if(item instanceof VibraniumShieldItem){
            VibraniumShieldItem shieldItem = (VibraniumShieldItem) item;
            return Optional.of(shieldItem);
        }
        else return Optional.empty();
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if(ENCHANTMENT_PREDICATE.test(enchantment)){
            return true;
        }
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repairItem) {
        return false;
    }

    private static Callable<ItemStackTileEntityRenderer> getISTER() {
        return VibraniumShieldISTER::new;
    }

    @Override
    public boolean isShield(ItemStack stack, @Nullable LivingEntity entity) {
        return SHIELD_PREDICATE.test(stack.getItem());
    }
}
