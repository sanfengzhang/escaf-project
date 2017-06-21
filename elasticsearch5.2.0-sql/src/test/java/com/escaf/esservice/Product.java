package com.escaf.esservice;

class Product
{
	private String productID;

	private long price;

	public String getProductID()
	{
		return productID;
	}

	public void setProductID(String productID)
	{
		this.productID = productID;
	}

	public long getPrice()
	{
		return price;
	}

	public void setPrice(long price)
	{
		this.price = price;
	}

	@Override
	public String toString()
	{
		return "Product [productID=" + productID + ", price=" + price + "]";
	}

}