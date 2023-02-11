package org.springframework.entity;

import org.springframework.stereotype.Component;

@Component
public class Dog {
	private String color;

	private Integer age;

	@Override
	public String toString() {
		return "Dog{" +
				"color='" + color + '\'' +
				", age=" + age +
				'}';
	}

	public String getColor() {
		return color;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
