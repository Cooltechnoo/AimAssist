package de.Cooltechno.aas.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.util.math.MathHelper;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AimAssist {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public boolean enabled = true;
    public double range = 4.5;
    public double fov = 80.0;
    public float smoothness = 25.0f;
    public boolean aimPitch = true;
    public float aimCurve = 1.5f;

    public void onRenderTick() {
        if (!enabled || mc.player == null || mc.world == null || mc.currentScreen != null) return;

        ItemStack stack = mc.player.getMainHandStack();
        if (!stack.contains(DataComponentTypes.WEAPON)) return;

        Entity target = getBestTarget();
        if (target != null) {
            applyCurvedAim(target);
        }
    }

    private void applyCurvedAim(Entity en) {
        double diffX = en.getX() - mc.player.getX();
        double diffZ = en.getZ() - mc.player.getZ();

        double diffY = (en.getY() + en.getStandingEyeHeight() * 0.7) - (mc.player.getY() + mc.player.getStandingEyeHeight());
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float targetYaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(diffY, dist));

        float yawDiff = MathHelper.wrapDegrees(targetYaw - mc.player.getYaw());
        float pitchDiff = MathHelper.wrapDegrees(targetPitch - mc.player.getPitch());

        if (Math.abs(yawDiff) > 0.05) {
            float pctOfFov = (float) Math.abs(yawDiff) / (float) (fov / 2);

            float strengthMultiplier = (float) Math.pow(pctOfFov, aimCurve);
            float randomSlowdown = (float) ThreadLocalRandom.current().nextDouble(0.85, 1.15);

            float stepYaw = (yawDiff / smoothness) * (strengthMultiplier + 0.1f) * randomSlowdown;
            mc.player.setYaw(mc.player.getYaw() + stepYaw);
        }

        if (aimPitch) {
            float stepPitch = pitchDiff / (smoothness * 2.0f);
            mc.player.setPitch(MathHelper.clamp(mc.player.getPitch() + stepPitch, -90, 90));
        }
    }

    private Entity getBestTarget() {
        List<PlayerEntity> players = mc.world.getEntitiesByClass(
                PlayerEntity.class,
                mc.player.getBoundingBox().expand(range),
                entity -> entity != mc.player && entity.isAlive() && !entity.isInvisible() && mc.player.canSee(entity)
        );

        return players.stream()
                .filter(this::isInFov)
                .min(Comparator.comparingDouble(p -> mc.player.distanceTo(p)))
                .orElse(null);
    }

    private boolean isInFov(Entity en) {
        double diffX = en.getX() - mc.player.getX();
        double diffZ = en.getZ() - mc.player.getZ();
        float targetYaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float yawDiff = MathHelper.wrapDegrees(targetYaw - mc.player.getYaw());
        return Math.abs(yawDiff) <= fov / 2;
    }
}