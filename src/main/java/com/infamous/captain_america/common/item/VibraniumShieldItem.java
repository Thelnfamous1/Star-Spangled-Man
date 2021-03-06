package com.infamous.captain_america.common.item;

import com.infamous.captain_america.client.renderer.CAItemStackTileEntityRenderer;
import com.infamous.captain_america.common.advancements.CACriteriaTriggers;
import com.infamous.captain_america.common.capability.CapabilityHelper;
import com.infamous.captain_america.common.capability.falcon_ability.IFalconAbility;
import com.infamous.captain_america.common.entity.projectile.VibraniumShieldEntity;
import com.infamous.captain_america.common.registry.EffectRegistry;
import com.infamous.captain_america.common.util.FalconFlightHelper;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.stats.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class VibraniumShieldItem extends ShieldItem implements IHasRenderMaterial {

    public static final Predicate<Item> SHIELD_PREDICATE =
            item -> item instanceof VibraniumShieldItem;
    public static final Predicate<Enchantment> SHIELD_ENCHANTMENT_PREDICATE =
            enchantment -> enchantment == Enchantments.POWER_ARROWS
                    || enchantment == Enchantments.PUNCH_ARROWS
                    || enchantment == Enchantments.LOYALTY;

    private final Supplier<EntityType<? extends VibraniumShieldEntity>> registeredShieldType;
    private final Supplier<net.minecraft.client.renderer.model.RenderMaterial> renderMaterialSupplier;

    public VibraniumShieldItem(Supplier<EntityType<? extends VibraniumShieldEntity>> registeredShieldType,
                               Properties properties,
                               DistExecutor.SafeCallable<RenderMaterial> safeRenderMaterial) {
        super(properties
                .stacksTo(1)
                .fireResistant()
                .setISTER(CAItemStackTileEntityRenderer::getISTER));
        this.registeredShieldType = registeredShieldType;
        this.renderMaterialSupplier = () -> net.minecraftforge.fml.DistExecutor.safeCallWhenOn(Dist.CLIENT, () -> safeRenderMaterial);
    }

    public static boolean isShieldStack(ItemStack stack) {
        return SHIELD_PREDICATE.test(stack.getItem());
    }

    public boolean throwShield(ItemStack itemStack, World world, LivingEntity thrower, VibraniumShieldEntity.ThrowType throwType, int shieldCharge) {
        if (thrower instanceof PlayerEntity) {
            PlayerEntity playerThrower = (PlayerEntity)thrower;

            if (!itemStack.isEmpty()) {
                float shieldChargingScale = shieldCharge / 100.0F;

                boolean threwShield = false;
                if (!world.isClientSide) {

                    Optional<? extends VibraniumShieldEntity> optionalShieldEntity =
                            createShield(world, itemStack, playerThrower, throwType);
                    if(!optionalShieldEntity.isPresent()) return false;

                    VibraniumShieldEntity shieldEntity = optionalShieldEntity.get();

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
                        shieldEntity.pickup = VibraniumShieldEntity.PickupStatus.CREATIVE_ONLY;
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
        return thrower.hasEffect(EffectRegistry.SUPER_SOLDIER.get());
    }

    private static boolean isFlipFlying(LivingEntity thrower){
        IFalconAbility falconAbilityCap = CapabilityHelper.getFalconAbilityCap(thrower);
        return falconAbilityCap != null & FalconFlightHelper.isFlipFlying(thrower);
    }

    private static float getThrowFactor(LivingEntity thrower, ItemStack stack) {
        float throwFactor = 2.0F;
        if(isSuperSoldier(thrower)) throwFactor += 2.0F;
        if(isFlipFlying(thrower)) throwFactor += 2.0F;
        return throwFactor;
    }

    public static boolean hasVibraniumShield(LivingEntity living) {
        return living.isHolding(SHIELD_PREDICATE);
    }

    public static Hand getShieldHoldingHand(LivingEntity living) {
        return SHIELD_PREDICATE.test(living.getMainHandItem().getItem()) ? Hand.MAIN_HAND : Hand.OFF_HAND;
    }

    public static Optional<? extends VibraniumShieldEntity> createShield(World world, ItemStack stack, LivingEntity thrower, VibraniumShieldEntity.ThrowType throwType){
        Optional<EntityType<? extends VibraniumShieldEntity>> shieldType = getShieldType(stack);
        if(shieldType.isPresent()){
            VibraniumShieldEntity shieldEntity = new VibraniumShieldEntity(shieldType.get(), thrower, world, throwType);
            return Optional.of(shieldEntity);
        }
        return Optional.empty();
    }

    public static Optional<EntityType<? extends VibraniumShieldEntity>> getShieldType(ItemStack stack){
        Item item = stack.getItem();
        if(item instanceof VibraniumShieldItem){
            VibraniumShieldItem shieldItem = (VibraniumShieldItem) item;
            return Optional.of(shieldItem.getShieldType());
        }
        else return Optional.empty();
    }

    public EntityType<? extends VibraniumShieldEntity> getShieldType(){
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
        if(SHIELD_ENCHANTMENT_PREDICATE.test(enchantment)){
            return true;
        }
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repairItem) {
        return false;
    }

    @Override
    @Nullable
    public net.minecraft.client.renderer.model.RenderMaterial getRenderMaterial(){
        return this.renderMaterialSupplier != null ? this.renderMaterialSupplier.get() : null;
    }

    @Override
    public boolean isShield(ItemStack stack, @Nullable LivingEntity entity) {
        return SHIELD_PREDICATE.test(stack.getItem());
    }
}
