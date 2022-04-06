# Spring Data JPA Using Hibernate

## Basics

When we develop Java EE applications, we spread our code across multiple classes. These classes goes across application layers. The **Data Access Layer** is responsible for connecting to the DB and executing the SQL statements. The **Services Layer** hosts the business logic of our application. The **Presentation Layer** presents the end result to the user. The **Integration Layer** allows other applications to connect to our application. This course is focused on the data access layer.

#### ORM

ORM stands for object relational mapping and is the process of mapping a Java class to a database table and its fields to the columns. Using this, we can synchronize class objects into database rows. Automatically, the ORM tools will do the SQL statements and using JDBC internally, it will do the insertion, update, deletion etc. We no longer need to deal with SQL or JDBC directly.

#### JPA

Java Persistence API is a standard from Oracle to perform ORM in Java EE applications. JPA comes with specifications for the JPA vendors and APIs for the developers. Some example of providers are Hibernate, OpenJPA, Eclipse Link etc.

#### Spring Data

Spring Data is a framework that removes configurations when it comes to dealing with DAL boilerplate. Internally Spring Data uses JPA and ORM tools like hibernate. To use Spring Data, we first need to define our object entity class and its fields and map it into a table using annotations. Afterwards we just need to define an interface class that extends **CRUDRepository** from Spring. This allows us to access the repository and invoke its methods.

## CRUD Operations

For this project, we worked on a Product object. We start with creating the database table. We created a Spring Boot project with dependencies Spring Data JPA and MySQL Driver.

```sql
create table product(
id int PRIMARY KEY,
name varchar(20),
description varchar(100),
price decimal(8,3)
);
```

We then proceed on creating the DAL. First we create a POJO entity class Product which we mark with annotations to map it to a database table. The **@Entity** annotation is mandatory, while the **@Table** is only used when the database table name is different from the class name. The **@Column** is used when the database column has a different name than the field.

```java
@Entity
@Table
public class Product {
	@Id
	private int id;
	private String name;
	@Column(name="description")
	private String desc;
	private Double price;
}
```

Afterwards, we create the Spring Data repository interface which will allow us to perform CRUD operations against the product database. This interface should extend the CrudRepository from Spring.

```java
public interface ProductRepository extends CrudRepository<Product, Integer> {

}
```

We also need to configure the data source as well using application.properties.

```
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
spring.datasource.username=root
spring.datasource.password=1234
```

We can see Spring Data in action by creating test methods. To do so we need to inject the ProductRepository into our test class with the help of **@Autowired**. Behind the scenes, Spring Data JPA will generate an insert query using hibernate. We can show the SQL query in the console by adding `spring.jpa.show-sql=true` in our applicaiton.properties.

```java
@SpringBootTest
class ProductdataApplicationTests {

	@Autowired
	ProductRepository repository;

	@Test
	void contextLoads() {
	}

	@Test
	public void testCreate() {
		Product product = new Product();
		product.setId(1);
		product.setName("Mac");
		product.setDesc("nice");
		product.setPrice(1000d);

		repository.save(product);
	}

	@Test
	public void testRead() {
		Product product = repository.findById(1).get();
		assertNotNull(product);
		assertEquals("Mac", product.getName());
	}

	@Test
	public void testUpdate() {
		Product product = repository.findById(1).get();
		product.setPrice(1200d);
		repository.save(product);
	}

	@Test
	public void testDelete() {
		repository.deleteById(1);
	}
}

```
