package com.gamma.parser;

import com.gamma.Creature;

public interface IEvaluator<T>
{	
	public T resolve(Creature creature);
	
	public static class IDspeed implements IEvaluator<Float> {
		public Float resolve(Creature creature){
			return creature.getSpeed();
		}
	}
	
	public static class IDstrength implements IEvaluator<Float> {
		public Float resolve(Creature creature){
			return creature.getStrength();
		}
	}
	
	public static class IDstepHeight implements IEvaluator<Float> {
		public Float resolve(Creature creature){
			return creature.getStepHeight();
		}
	}
	
	public static class IDmaxEnergy implements IEvaluator<Float> {
		public Float resolve(Creature creature){
			return creature.getMaxEnergy();
		}
	}
	
	public static class IDkills implements IEvaluator<Float> {
		public Float resolve(Creature creature){
			return (float) creature.getKills();
		}
	}
	
	public static class IDdeaths implements IEvaluator<Float> {
		public Float resolve(Creature creature){
			return (float) creature.getDeaths();
		}
	}
	
	public static class IDtotalDamageOutput implements IEvaluator<Float> {
		public Float resolve(Creature creature){
			return creature.getTotalDamageOutput();
		}
	}
	
	public static class IDtotalDamageTaken implements IEvaluator<Float> {
		public Float resolve(Creature creature){
			return creature.getTotalDamageTaken();
		}
	}
	
	public static class IDred implements IEvaluator<Float> {
		public Float resolve(Creature creature){
			return creature.getColorRed();
		}
	}
	
	public static class IDblue implements IEvaluator<Float> {
		public Float resolve(Creature creature){
			return creature.getColorBlue();
		}
	}
	
	public static class IDgreen implements IEvaluator<Float> {
		public Float resolve(Creature creature){
			return creature.getColorGreen();
		}
	}

	public static class AdditionFloat implements IEvaluator<Float> {
		
		private final IEvaluator<Float> left, right;
		
		public AdditionFloat(IEvaluator<Float> left, IEvaluator<Float> right) {
			this.left = left;
			this.right = right;
		}
		
		public Float resolve(Creature creature){
			return left.resolve(creature) + right.resolve(creature);
		}
	}
	
	
	public static class AdditionInteger implements IEvaluator<Integer>{
		private final IEvaluator<Integer> left, right;
		
		public AdditionInteger(IEvaluator<Integer> left, IEvaluator<Integer> right) {
			this.left = left;
			this.right = right;
		}
		
		public Integer resolve(Creature creature){
			return left.resolve(creature) + right.resolve(creature);
		}
	}
	
	public static class AdditionDouble implements IEvaluator<Double>{
		private final IEvaluator<Double> left, right;
		
		public AdditionDouble(IEvaluator<Double> left, IEvaluator<Double> right) {
			this.left = left;
			this.right = right;
		}
		
		public Double resolve(Creature creature){
			return left.resolve(creature) + right.resolve(creature);
		}
	}
	public static class SubstractionFloat implements IEvaluator<Float> {
		
		private final IEvaluator<Float> left, right;
		
		public SubstractionFloat(IEvaluator<Float> left, IEvaluator<Float> right) {
			this.left = left;
			this.right = right;
		}
		
		public Float resolve(Creature creature){
			return left.resolve(creature) - right.resolve(creature);
		}
	}
	
	
	public static class SubstractionInteger implements IEvaluator<Integer>{
		private final IEvaluator<Integer> left, right;
		
		public SubstractionInteger(IEvaluator<Integer> left, IEvaluator<Integer> right) {
			this.left = left;
			this.right = right;
		}
		
		public Integer resolve(Creature creature){
			return left.resolve(creature) - right.resolve(creature);
		}
	}
	
	public static class SubstractionDouble implements IEvaluator<Double>{
		private final IEvaluator<Double> left, right;
		
		public SubstractionDouble(IEvaluator<Double> left, IEvaluator<Double> right) {
			this.left = left;
			this.right = right;
		}
		
		public Double resolve(Creature creature){
			return left.resolve(creature) - right.resolve(creature);
		}
	}
	
	public static class MultFloat implements IEvaluator<Float> {
		
		private final IEvaluator<Float> left, right;
		
		public MultFloat(IEvaluator<Float> left, IEvaluator<Float> right) {
			this.left = left;
			this.right = right;
		}
		
		public Float resolve(Creature creature){
			return left.resolve(creature) * right.resolve(creature);
		}
	}
	
	
	public static class MultInteger implements IEvaluator<Integer>{
		private final IEvaluator<Integer> left, right;
		
		public MultInteger(IEvaluator<Integer> left, IEvaluator<Integer> right) {
			this.left = left;
			this.right = right;
		}
		
		public Integer resolve(Creature creature){
			return left.resolve(creature) * right.resolve(creature);
		}
	}
	
	public static class MultDouble implements IEvaluator<Double>{
		private final IEvaluator<Double> left, right;
		
		public MultDouble(IEvaluator<Double> left, IEvaluator<Double> right) {
			this.left = left;
			this.right = right;
		}
		
		public Double resolve(Creature creature){
			return left.resolve(creature) * right.resolve(creature);
		}
	}
	
	public static class DivFloat implements IEvaluator<Float> {
		
		private final IEvaluator<Float> left, right;
		
		public DivFloat(IEvaluator<Float> left, IEvaluator<Float> right) {
			this.left = left;
			this.right = right;
		}
		
		public Float resolve(Creature creature){
			return left.resolve(creature) / right.resolve(creature);
		}
	}
	
	
	public static class DivInteger implements IEvaluator<Integer>{
		private final IEvaluator<Integer> left, right;
		
		public DivInteger(IEvaluator<Integer> left, IEvaluator<Integer> right) {
			this.left = left;
			this.right = right;
		}
		
		public Integer resolve(Creature creature){
			return left.resolve(creature) / right.resolve(creature);
		}
	}
	
	public static class DivDouble implements IEvaluator<Double>{
		private final IEvaluator<Double> left, right;
		
		public DivDouble(IEvaluator<Double> left, IEvaluator<Double> right) {
			this.left = left;
			this.right = right;
		}
		
		public Double resolve(Creature creature){
			return left.resolve(creature) / right.resolve(creature);
		}
	}

}
