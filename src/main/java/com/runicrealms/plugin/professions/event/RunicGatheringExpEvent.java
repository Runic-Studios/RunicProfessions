package com.runicrealms.plugin.professions.event;

import com.runicrealms.plugin.professions.gathering.GatheringSkill;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * This custom event is called when a player gains experience for a gathering skill.
 *
 * @author Skyfallin
 */
public class RunicGatheringExpEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final int amount;
    private final boolean applyBonuses;
    private final Player player;
    private final GatheringSkill skill;
    private final Map<BonusType, Double> bonuses = new HashMap<>();
    private boolean isCancelled;
    private int count;

    /**
     * Give a player experience through our custom calculators.
     *
     * @param amount       initial amount of experience before bonuses
     * @param applyBonuses whether this experience should be boosted by bonuses
     * @param player       to receive experience
     * @param skill        which gathering skill
     * @param count        the amount of times this item has been gathered
     */
    public RunicGatheringExpEvent(int amount, boolean applyBonuses, @NotNull Player player, @NotNull GatheringSkill skill, int count) {
        this.amount = amount;
        this.applyBonuses = applyBonuses;
        this.player = player;
        this.skill = skill;
        this.isCancelled = false;
        this.count = Math.max(count, 1);
    }

    public RunicGatheringExpEvent(int amount, boolean applyBonuses, @NotNull Player player, @NotNull GatheringSkill skill) {
        this(amount, applyBonuses, player, skill, 1);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public int getRawAmount() {
        return amount;
    }

    public int getAmountNoBonuses() {
        return amount;
    }

    public int getTotalAmount() {
        return this.getFinalAmount() * this.count;
    }

    /**
     * Bonuses are a value starting from 0, where 0.25 would be an additional 25% EXP bonus.
     */
    public void setBonus(BonusType type, double bonus) {
        this.bonuses.put(type, bonus);
    }

    public int getExpFromBonus(BonusType type) {
        Double bonus = this.bonuses.get(type);
        if (bonus == null) return 0;
        return (int) Math.round(amount * bonus);
    }

    public int getFinalAmount() {
        return getFinalAmount(this.amount);
    }

    private int getFinalAmount(int expAmount) {
        if (!applyBonuses) return expAmount;
        return (int) Math.round(expAmount * (1 + this.bonuses.values().stream().mapToDouble((bonus) -> bonus).sum()));
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return this.player;
    }

    public GatheringSkill getSkill() {
        return this.skill;
    }

    public boolean shouldApplyBonuses() {
        return this.applyBonuses;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean arg0) {
        this.isCancelled = arg0;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = Math.max(count, 1);
    }

    public enum BonusType {
        BOOST,
        OUTLAW
    }
}