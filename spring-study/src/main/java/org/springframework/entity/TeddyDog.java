package org.springframework.entity;

public class TeddyDog extends Dog{
	private String name;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}


	@Override
	public String toString() {
		return "TeddyDog{" +
				"name='" + name + '\'' +
				'}';
	}
}
