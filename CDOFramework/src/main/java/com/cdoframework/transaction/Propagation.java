package com.cdoframework.transaction;
/**
 * 
 * @author Kenel
 *
 */
public enum Propagation{
	/**
	 * Support a current transaction, create a new one if none exists.
	 * description:
	 *   if there is an active transaction, then it creates a new one if nothing existed.
	 *   Otherwise, the business logic appends to the currently active transaction.
	 */
	REQUIRED(TransactionDefinition.PROPAGATION_REQUIRED),
	/**
	 * Support a current transaction, execute non-transactionally if none exists.
	 * description: 
	 * 	 first checks if an active transaction exists. 
	 * 	 If a transaction exists, then the existing transaction will be used.
	 *   If there isn't a transaction, it is executed non-transactional.
	 */
	SUPPORTS(TransactionDefinition.PROPAGATION_SUPPORTS),
	/**
	 * Support a current transaction, throw an exception if none exists.
	 * description: 
	 *   if there is an active transaction, then it will be used. 
	 *   If there isn't an active transaction,then throws an exception
	 */
	MANDATORY(TransactionDefinition.PROPAGATION_MANDATORY),
	/**
	 * Create a new transaction, and suspend the current transaction if one exists.
	 * description：
	 * 	 suspends the current transaction if it exists and then creates a new one
	 */
	REQUIRES_NEW(TransactionDefinition.PROPAGATION_REQUIRES_NEW),	
	/**
	 * Execute non-transactionally, suspend the current transaction if one exists.
	 * description：
	 * 	 at first suspends the current transaction if it exists, 
	 *   then the business logic is executed without a transaction
	 */
	NOT_SUPPORTED(TransactionDefinition.PROPAGATION_NOT_SUPPORTED),	
	/**
	 * Execute non-transactionally, throw an exception if a transaction exists.
	 * description：
	 * 	  transactional logic with NEVER propagation,throws an exception if there's an active transaction
	 */
	NEVER(TransactionDefinition.PROPAGATION_NEVER),
	/**
	 * Execute within a nested transaction if a current transaction exists, behave like REQUIRED otherwise.
	 * description：
	 * 	 checks if a transaction exists, then if yes, it marks a savepoint. 
	 * 	 This means if our business logic execution throws an exception, 
	 * 	 then transaction rollbacks to this savepoint. 
	 * 	 If there's no active transaction, it works like REQUIRED.
	 */
	NESTED(TransactionDefinition.PROPAGATION_NESTED);
	
	private final byte value;

	Propagation(byte value) { this.value = value; }

	public byte value() { return this.value; }
	
	public static Propagation getPropagation(String propagationName){
		Propagation propagation=null;
		switch(propagationName.toUpperCase()){
			case "REQUIRED":
				propagation=Propagation.REQUIRED;
				break;
			case "SUPPORTS":
				propagation=Propagation.SUPPORTS;
				break;	
			case "MANDATORY":
				propagation=Propagation.MANDATORY;
				break;	
			case "REQUIRES_NEW":
				propagation=Propagation.REQUIRES_NEW;
				break;	
			case "NOT_SUPPORTED":
				propagation=Propagation.NOT_SUPPORTED;
				break;
			case "NEVER":
				propagation=Propagation.NEVER;
				break;					
			case "NESTED":
				propagation=Propagation.NESTED;
				break;				
		}
		return propagation;
	}
}
