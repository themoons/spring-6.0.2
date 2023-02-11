package org.springframework;

import org.apache.commons.logging.LogFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.ChildBeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.entity.Dog;
import org.springframework.entity.User;

import java.util.Arrays;

/**
 * bean的元数据：描述数据的数据
 * bean注入方式：
 * 		xml
 * 		注解
 * 		配置类
 * 		Import注解  TemportSelect
 *
 * 	<br>BeanDefinition</br>：所有的Bean都要首先提取元数据，将其封装为 BeanDefinition
 * 			保存了一下信息：
 * 					定义了id 别名 bean的对应关系
 * 					具体的工厂方法
 * 					构造方法 初始化方法
 * 					Bean的class对象
 * 					等......
 * 		todo：（模板方法设计模式）（generic 通用 spring大部分通用的类都以此开头）
 * 			BeanDefinition:接口，提供了 检索 修改Bean的元数据的能力，定义了一些基础数据
 * 			-----AbstractBeanDefinition:抽象类，提供了基本的方法实现，定义一系列属性
 * 				-----GenericBeanDefinition：spring2.5之后提供的通用BeanDefinition实现，该类通过parenetName属性灵活（动态）设置parent bean definition
 * 				-----RootBeanDefinition类：和ChildBeanDefinition配合构建父子关系的bean
 * 				-----ChildBeanefinition类：一种Bean definition，可以继承她父类的设置，对RootBeanDefinition有一定的依赖关系
 * 		    -----AnnotatedBeanDefinition接口：改接口拥有访问特定注解元数据的能力，不需要加载类就能访问，直接解析类文件 ASM
 * 		    	-----ConfigurationClassBeanDefinition：负责将@Bean注解的方法转换为对应类
 * 		    	-----ScannedGenericBeanDefinition：基于ASM扫描的类文件加载Bean的定义存储@Conpoment @Service @Controller等注解注释的类
 * 		    	------AnnotatedGenericBeanDefinition：基于注解驱动的spring应用，使用的非常多
 */
public class Main {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:Bean.xml");

		User userInfo = applicationContext.getBean(User.class);
//		System.out.println(userInfo.toString());
//		System.out.println("Hello world!");

		testRegistryByScannner();
	}

	//测试 BeanDefinition
	public static void testGenericBeanDefinition() {
		GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();

		genericBeanDefinition.setBeanClassName("org.springframework.entity.User");
		genericBeanDefinition.setScope("prototype");
		genericBeanDefinition.setInitMethodName("init");

		MutablePropertyValues propertyValues = new MutablePropertyValues();

		propertyValues.addPropertyValue("name", "zhangsan");
		genericBeanDefinition.setPropertyValues(propertyValues);
	}

	//todo GenericBeanDefinition 在很多场景可以替换以上的内容，由于历史原因 RootBeanDefinition一直存在，后期的归一处理 要将不同的 BeanDefinition zhuanhaun
		//todo 或合并值一个RootBeanDefinition；

	/**
	 * 	RootBeanDefinition和AbstractBeanDefinition是互补关系，RootBeanDefinition在其基础上定义了更多属性
	 * 	RootBeanDefinition不能有父BeanDefinition，可以和ChildBeanDefinition配置使用 构建父子关系，，bean可以继承
	 * 	目前最常用的是GenericBeanDefinition及其子类实现，很强大  可以轻松构建父子关系
	 * 	不同的BeanDefinition可以 合并 拷贝等
	 * 		todo  new GenericBeanDefinition(teddy)
	 * 				definition.overrideFrom(dog)
	 */
	public static void testRootGenericBeanDefinition() {
		RootBeanDefinition genericBeanDefinition = new RootBeanDefinition();

		genericBeanDefinition.setBeanClassName("org.springframework.entity.Dog");
		genericBeanDefinition.setScope("prototype");
		genericBeanDefinition.setInitMethodName("init");

		MutablePropertyValues propertyValues = new MutablePropertyValues();

		propertyValues.addPropertyValue("color", "yellow");
		propertyValues.addPropertyValue("age", "13");
		genericBeanDefinition.setPropertyValues(propertyValues);

		ChildBeanDefinition child = new ChildBeanDefinition("dog");
		child.setBeanClassName("org.springframework.entity.TeddyDog");

		propertyValues = new MutablePropertyValues();

		propertyValues.addPropertyValue("name", "lisi");
		child.setPropertyValues(propertyValues);
	}

	/**
	 * beanDefinition注册器
	 * 	有了统一标准的元数据后，统一管理 需要一个容器去存储，
	 * 	todo  BeadDefinitionRegistry，只要实现这个接口，就拥有注册BeanDefinition的能力
	 */
	public static void beanDefinitionRegistry() {
		//声明一个简单 注册器
		SimpleBeanDefinitionRegistry registry = new SimpleBeanDefinitionRegistry();

		RootBeanDefinition genericBeanDefinition = new RootBeanDefinition();

		genericBeanDefinition.setBeanClassName("org.springframework.entity.Dog");
		genericBeanDefinition.setScope("prototype");
		genericBeanDefinition.setInitMethodName("init");

		MutablePropertyValues propertyValues = new MutablePropertyValues();

		propertyValues.addPropertyValue("color", "yellow");
		propertyValues.addPropertyValue("age", "13");
		genericBeanDefinition.setPropertyValues(propertyValues);
		registry.registerBeanDefinition("dog", genericBeanDefinition);

		ChildBeanDefinition child = new ChildBeanDefinition("dog");
		child.setBeanClassName("org.springframework.entity.TeddyDog");

		propertyValues = new MutablePropertyValues();

		propertyValues.addPropertyValue("name", "lisi");
		child.setPropertyValues(propertyValues);
		registry.registerBeanDefinition("teddyDog", child);
	}

	/**
	 * 加载BeanDefinition
	 * 	我们不可能为每一个类手动编写与之相对应的BeanDefinition  元数据还是要从xml或注解或配置类中获取，spring提供了对应的工具
	 *
	 * 	todo
	 * 		1、读取xml配置文件
	 * 		2、加载注解的bean
	 */
	public static void testRegistryByXml() {
		//注册器 用来 注册BeanDefinition
		SimpleBeanDefinitionRegistry registry = new SimpleBeanDefinitionRegistry();

		//解析xml xml 读取器
		XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(registry);

		xmlReader.loadBeanDefinitions("classpath:Bean.xml");

		System.out.println("Dog ---> " + registry.getBeanDefinition("dog").getBeanClassName());
	}

	//基于注解的 bean注册
	public static void annoRegistry() {
		SimpleBeanDefinitionRegistry registry = new SimpleBeanDefinitionRegistry();

		AnnotatedBeanDefinitionReader annoReader = new AnnotatedBeanDefinitionReader(registry);

		annoReader.register(Dog.class);
	}

	/**
	 * todo 基于 配置类
	 * 	ConfigurationClassBeanDefinitionReader  读取配置类， 将读取的元数据封装为 ConfigurationClassBeanDefinition
	 */
	public static void configurationTest(){}

	/**
	 * todo 类路径扫描
	 * 	包扫描的过程
	 * 		无论是骚包还是其他方式，我们解析一个类 有以下几种方式
	 * 			--加载一个类到内存，获取class文件，通过 反射获取 元数据
	 * 			--直接操纵字节码文件 .class  读取字节码中的元数据
	 * 		spring选择第二种方式
	 * 			--首先第二种性能优于第一种
	 * 			--第一种会将扫描的类全部加载到堆内存，浪费空间  增加gc次数，第二种 按照元数据按需加载
	 * 			包扫描的scan（）方法为例 ClassPathBeanDefinitionScanner
	 * 			spring-context-indexer
	 *
	 * 			todo classpath* 与 classpath	前者会扫描  jar包下的bean 后者只会扫描工程目录下的bean
	 */
	public static void testRegistryByScannner() {
		//定义一个注册器。用来注册和管理BeanDefinition
		SimpleBeanDefinitionRegistry registry = new SimpleBeanDefinitionRegistry();
		
		//通过扫描方式注册
		ClassPathBeanDefinitionScanner classPath = new ClassPathBeanDefinitionScanner(registry);
		classPath.scan("org.springframework");

		System.out.println(Arrays.toString(registry.getBeanDefinitionNames()));
	}




















}