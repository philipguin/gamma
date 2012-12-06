package com.gamma.parser;

import com.gamma.Creature;

public interface IEvaluator<T>
{	
	public T resolve(Creature creature);
	
	public static class EvalNumber implements IEvaluator<Double> {
		private double value;
		
		public EvalNumber(int Value) {
			value = (double) Value;
		}
		
		public EvalNumber(float Value) {
			value = (double) Value;
		}
		
		public EvalNumber(double Value) {
			value = Value;
		}
		
		public Double resolve(Creature creature){
			return value;
		}
	}
	
	public static class IDspeed implements IEvaluator<Double> {
		public Double resolve(Creature creature){
			return (double) creature.getSpeed();
		}
	}
	
	public static class IDstrength implements IEvaluator<Double> {
		public Double resolve(Creature creature){
			return (double) creature.getStrength();
		}
	}
	
	public static class IDstepHeight implements IEvaluator<Double> {
		public Double resolve(Creature creature){
			return (double) creature.getStepHeight();
		}
	}
	
	public static class IDmaxEnergy implements IEvaluator<Double> {
		public Double resolve(Creature creature){
			return (double) creature.getMaxEnergy();
		}
	}
	
	public static class IDkills implements IEvaluator<Double> {
		public Double resolve(Creature creature){
			return (double) creature.getKills();
		}
	}
	
	public static class IDdeaths implements IEvaluator<Double> {
		public Double resolve(Creature creature){
			return (double) creature.getDeaths();
		}
	}
	
	public static class IDtotalDamageOutput implements IEvaluator<Double> {
		public Double resolve(Creature creature){
			return (double) creature.getTotalDamageOutput();
		}
	}
	
	public static class IDtotalDamageTaken implements IEvaluator<Double> {
		public Double resolve(Creature creature){
			return (double) creature.getTotalDamageTaken();
		}
	}
	
	public static class IDred implements IEvaluator<Double> {
		public Double resolve(Creature creature){
			return (double) creature.getColorRed();
		}
	}
	
	public static class IDblue implements IEvaluator<Double> {
		public Double resolve(Creature creature){
			return (double) creature.getColorBlue();
		}
	}
	
	public static class IDgreen implements IEvaluator<Double> {
		public Double resolve(Creature creature){
			return (double) creature.getColorGreen();
		}
	}

	public static class AdditionFloat implements IEvaluator<Double> {
		
		private final IEvaluator<Double> left, right;
		
		public AdditionFloat(IEvaluator<Double> left, IEvaluator<Double> right) {
			this.left = left;
			this.right = right;
		}
		
		public Double resolve(Creature creature){
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
	public static class SubtractionFloat implements IEvaluator<Double> {
		
		private final IEvaluator<Double> left, right;
		
		public SubtractionFloat(IEvaluator<Double> left, IEvaluator<Double> right) {
			this.left = left;
			this.right = right;
		}
		
		public Double resolve(Creature creature){
			return left.resolve(creature) - right.resolve(creature);
		}
	}
	
	
	public static class SubtractionInteger implements IEvaluator<Integer>{
		private final IEvaluator<Integer> left, right;
		
		public SubtractionInteger(IEvaluator<Integer> left, IEvaluator<Integer> right) {
			this.left = left;
			this.right = right;
		}
		
		public Integer resolve(Creature creature){
			return left.resolve(creature) - right.resolve(creature);
		}
	}
	
	public static class SubtractionDouble implements IEvaluator<Double>{
		private final IEvaluator<Double> left, right;
		
		public SubtractionDouble(IEvaluator<Double> left, IEvaluator<Double> right) {
			this.left = left;
			this.right = right;
		}
		
		public Double resolve(Creature creature){
			return left.resolve(creature) - right.resolve(creature);
		}
	}
	
	public static class MultFloat implements IEvaluator<Double> {
		
		private final IEvaluator<Double> left, right;
		
		public MultFloat(IEvaluator<Double> left, IEvaluator<Double> right) {
			this.left = left;
			this.right = right;
		}
		
		public Double resolve(Creature creature){
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
	
	public static class DivFloat implements IEvaluator<Double> {
		
		private final IEvaluator<Double> left, right;
		
		public DivFloat(IEvaluator<Double> left, IEvaluator<Double> right) {
			this.left = left;
			this.right = right;
		}
		
		public Double resolve(Creature creature){
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

	public static class NegateDouble implements IEvaluator<Double> {
		private final IEvaluator<Double> left;
		
		public NegateDouble(IEvaluator<Double> left) {
			this.left = left;
		}
		
		public Double resolve(Creature creature) {
			return -1 * left.resolve(creature);
		}
	}
	
}
