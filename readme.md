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

## ID Generators

Previously, we have hardcoded the value for the id of our product. JPA provides four id generation strategies: auto, identity, sequence and table. To use Id Generators, we use the annotation **@GeneratedValue** from JPA. For **auto**, the persistence providers like Hibernate checks the underlying database for the supported strategies. In **identity**, the persistence provider relies on the auto increment field. In **sequence**, a custom logic is defined to generate a value using sequences. For **table**, a special table will be used by the persistence provider and uses the column value as a primary key.

For this section, we created an Employee table. We also created the Spring Boot project idgenerators with dependencies Spring Data JPA and MySQL Driver.

```sql
create table employee(
id int,
name varchar(20)
);
```

We also create the entity and repository

```java
@Entity
public class Employee {
	@Id
	private long id;
	private String name;
}

public interface EmployeeRepository extends CrudRepository<Employee, Long> {

}
```

#### Identity

To use the Identity generator type, we need to modify our database column to make it a primary key and auto increment

```sql
create table employee(
id int PRIMARY KEY AUTO_INCREMENT,
name varchar(20)
);
```

We now have to specify the id generation strategy in our entity

```java
@Entity
public class Employee {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String name;
}
```

This will cause the database to automatically create the id and automatically increment it when new rows are created.

#### Table

To use the table strategy, we need to create a new table id_gen. The gen_name column will store the sequence name and gen_val will store the value that gets generated by the table generator. This table will serve as a placeholder to store the id values.

```sql
create table id_gen(
gen_name varchar(60) PRIMARY KEY,
gen_val int(20)
);

create table employee(
id int PRIMARY KEY,
name varchar(20)
);
```

We then configure table as the generation strategy. We can do this using the **@TableGenerator** annotation. The allocationSize attribute specifies by how much the sequence should be incremented.

```java
@Entity
public class Employee {
	@TableGenerator(name = "employee_gen", table = "id_gen", pkColumnName = "gen_name", valueColumnName = "gen_val", allocationSize = 100)
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="employee_gen")
	private long id;
	private String name;
}
```

#### Custom Generator

We can also create a custom random id generator class. This needs to implement hte interface **IdentifierGenerator** from hibernate. The generate() method of this interface needs to return a value that will be used as an id.

```java
public class CustomRandomIDGenerator implements IdentifierGenerator {
	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
		Random random = null;
		int id = 0;
		random = new Random();
		id = random.nextInt(1000000);
		return new Long(id);
	}
}
```

We then confgure our own **@GenericGenerator** wherein we pass the package name plus class name of our custom generator class.

```java
@Entity
public class Employee {
	@GenericGenerator(name="emp_id", strategy="com.demiglace.springdata.idgenerators.CustomRandomIDGenerator")
	@GeneratedValue(generator="emp_id")
	@Id
	private long id;
	private String name;
```
