package br.com.microservices.transactionsapi.enume;

public enum Operations {
	
	COMPRA_A_VISTA(1,2),
	COMPRA_PARCELADA(2,1),
	SAQUE(3,0),
	PAGAMENTO(4,0);
	
	private int id;
	private int charge_order;
	
	private Operations(int id, int charge_order) {
		this.id = id;
	    this.charge_order = charge_order;
	}
	
	public int getChargeOrder() {
	      return this.charge_order;
	}
	
	public int getId() {
		return this.id;
	}
	

}
