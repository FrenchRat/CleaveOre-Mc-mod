package com.veinmine.mod.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

/**
 * OrePopParticle — a soft, glowing, shader-friendly particle that bursts out
 * when an ore is stage-1 mined.
 *
 * ── Design goals ────────────────────────────────────────────────────────────
 *
 * 1. SOFT EDGES: We use SpriteBillboardParticle as the base so the particle
 *    always faces the camera (billboard-aligned).  The texture uses a radial
 *    gradient with transparent edges — this is the "soft particle" look.
 *
 * 2. SHADER COMPATIBILITY: We render on the TRANSLUCENT layer (not OPAQUE),
 *    which means Iris/Optifine processes this particle through their
 *    gbuffers_textured_lit or gbuffers_particles shader pass.  Any shader that
 *    adds bloom, depth-of-field, or glow will automatically affect these
 *    particles because they're treated like any other semi-transparent entity.
 *
 *    Crucially, we set `collidesWithWorld = true` so the particle doesn't
 *    clip through blocks, and `colorMultiplier` to a warm gold to hint at
 *    precious ore inside without being garish.
 *
 * 3. LIFETIME & FADE: Particles live 18–26 ticks (~0.9–1.3 s at 20 TPS).
 *    Alpha goes 0→1 in the first 3 ticks (quick pop-in) and 1→0 over the
 *    last 8 ticks (soft fade-out).  This avoids the jarring "particle pops
 *    off" look that vanilla particles sometimes have.
 *
 * 4. MOTION: Each particle gets a random initial velocity from the server-side
 *    spawn call (the deltaX/Y/Z arguments become velocity in the vanilla
 *    particle system).  We also apply a slight upward drift and air drag each
 *    tick, mimicking dust rising from a struck rock face.
 */
@Environment(EnvType.CLIENT)
public class OrePopParticle extends SpriteBillboardParticle {

    /** Drag coefficient — lower = floatier, higher = falls faster. */
    private static final float AIR_DRAG = 0.86f;

    /** How many ticks the fade-in lasts. */
    private static final int FADE_IN_TICKS = 3;

    /** How many ticks before end the fade-out begins. */
    private static final int FADE_OUT_TICKS = 8;

    protected OrePopParticle(
        ClientWorld world,
        double x, double y, double z,
        double velocityX, double velocityY, double velocityZ,
        SpriteProvider spriteProvider
    ) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);

        // Pick a random sprite from our particle texture sheet (ore_pop.json defines
        // multiple frames so each particle looks slightly different)
        this.setSprite(spriteProvider);

        // ── Visual settings ──────────────────────────────────────────────────

        // Warm golden glow — works well for most ore types; specialised colours
        // would require per-ore particle types (a future enhancement).
        this.colorRed   = 1.00f;
        this.colorGreen = 0.88f;
        this.colorBlue  = 0.45f;
        this.colorAlpha = 0.0f; // starts transparent (fade-in)

        // Scale: slightly larger than vanilla crit particles for visibility
        this.scale = 0.18f + (float) this.random.nextFloat() * 0.12f;

        // Lifetime: randomise slightly so the burst looks organic, not simultaneous
        this.maxAge = 18 + this.random.nextInt(8);

        // Physics: particles collide with the world (no clipping through floors)
        this.collidesWithWorld = true;

        // Add a small upward bias to the velocity so particles drift up like dust
        this.velocityY += 0.04 + this.random.nextDouble() * 0.04;
    }

    @Override
    public void tick() {
        // Save previous position for motion blur (vanilla uses this for rendering)
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        // Apply air drag each tick so particles decelerate naturally
        this.velocityX *= AIR_DRAG;
        this.velocityY *= AIR_DRAG;
        this.velocityZ *= AIR_DRAG;

        // Move the particle (handles collision with blocks)
        this.move(this.velocityX, this.velocityY, this.velocityZ);

        // ── Alpha envelope (fade-in then fade-out) ───────────────────────────
        int age = this.age;
        int max = this.maxAge;

        if (age < FADE_IN_TICKS) {
            // Fade in: 0 → 1 over the first FADE_IN_TICKS ticks
            this.colorAlpha = (float) age / FADE_IN_TICKS;
        } else if (age > max - FADE_OUT_TICKS) {
            // Fade out: 1 → 0 over the last FADE_OUT_TICKS ticks
            float remaining = (float)(max - age) / FADE_OUT_TICKS;
            this.colorAlpha = Math.max(0f, remaining);
        } else {
            this.colorAlpha = 1.0f;
        }

        // Advance age and mark dead if lifetime exceeded
        if (++this.age >= this.maxAge) {
            this.markDead();
        }
    }

    /**
     * TRANSLUCENT layer — this is the key to shader compatibility.
     *
     * Iris and Optifine route particles in this layer through the same shader
     * pass as stained glass and water.  Any bloom/glow effect in the shader
     * will therefore also apply to our particles, giving the desired "glowing
     * ore chip" look in shaderpacks like BSL, Complementary, or Sildurs.
     */
    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    // ── Factory (registered in VeinMineModClient) ────────────────────────────

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {

        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(
            DefaultParticleType parameters,
            ClientWorld world,
            double x, double y, double z,
            double velocityX, double velocityY, double velocityZ
        ) {
            return new OrePopParticle(
                world,
                x, y, z,
                velocityX, velocityY, velocityZ,
                this.spriteProvider
            );
        }
    }
}
