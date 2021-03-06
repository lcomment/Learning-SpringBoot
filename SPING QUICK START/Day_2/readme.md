# Class 01. 스프링 AOP
  
✎ 비즈니스 컴포턴트 개발에서 가장 중요한 두 가지 원칙: 낮은 결합도와 높은 응집도 유지  
✎ IoC → 낮은 결합도 유지  
✎ AOP → 높은 응집도 유지  

### 1.1 AOP 이해하기
  
❖ AOP(Aspect Oriented Programming): 어떤 로직을 기준으로 핵심적인 관점, 부가적인 관점으로 나누어서 보고 그 관점을 기준으로 각각 모듈화하겠다는 것 (관점 지향 프로그래밍)  
  
- AOP의 핵심 개념: **관심 분리**(Separation of Concerns)
  - 횡단 관심: 메서드마다 공통으로 등장하는 로깅이나 예외, 트랜잭션 처리 등의 코드
  - 핵심 관심: User Request에 따라 실제로 수행되는 핵심 비즈니스 로직
<img src="https://user-images.githubusercontent.com/56003992/152282250-42ab9393-6e8b-4a30-aa23-a346a17d74f0.png" width=512 height=200>  

❖ OOP에서 횡단 관심에 해당하는 공통 코드를 완벽하게 독립적인 모듈로 분리하기 힘든 이유
: 클래스로 분리시키면 해당 클래스들의 결합도가 높아짐  

### 1.2 AOP 시작하기

☛ pom.xml에 AOP 관렬 라이브러리 추가  
```xml
<!-- AspectJ -->
<dependency>
  <groupId>org.aspectj</groupId>
  <artifactId>aspectjrt</artifactId>
  <version>${aspectj.version}</version>
</dependency>
<dependency>
  <groupId>org.aspectj</groupId>
  <artifactId>aspectjweaver</artifactId>
  <version>1.9.7</version>
</dependency> 
```  
☛ [applicationContext.xml] → [Namespaces] 에서 aop 네임스페이스 추가  
☛ LogAdvice 클래스를 교체하고 싶으면 applicationContext.xml만 수정  
  
* * *

# Class 02. AOP 용어 및 기본 설정

### 2.1 AOP 용어 정리
  
#### ❖ JoinPoint
: Client가 호출되는 모든 비즈니스 메서드  
→ 실습에선 BoardServiceImpl, UserServiceImpl  
  
#### ❖ Pointcut  
: 필터링된 JoinPoint
→ 트랜잭션을 처리하는 공통 기능을 만들었다고 가정했을 때, 횡단 관심 기능은 CRUD 기능의 메서드에 대해서는 당연히 동작해야 하지만, 검색 기능은 메서드에 대해서는 동작할 필요가 없음  
→ <aop:pointcut id="id" expression="필터링에 대한 설정" />  
<img src="https://user-images.githubusercontent.com/56003992/152288685-d3e316f6-6738-4b59-b0a3-097f35fc77cf.png" width=256 height=100>  
- 상단은 리턴타입과 매개변수를 무시하고 com.muticampus.biz 패키지로 시작하는 클래스 중 Impl로 끝나는 클래스으 모든 메서드를 포인트컷으로 설정
- 하단은 상단과 같지만 get으로 시작하는 메서드만 포인트컷으로 설정
  
#### ❖ Advice  
: 횡당 관심에 해당하는 공통 기능의 코드  
→ 독립된 클래스의 메서드로 작성되며, 스프링 설정파일에서 동작 시점 지정  
→ <aop:[before | after | after-returning | after-throwing | around] pointcut-ref="id" method="메서드" />  
  
#### ❖ Weaving
: Pointcut으로 지정된 핵심 관심 메서드가 호출될 때, Advice에 해당하는 횡단 관심 메서드가 삽입되는 과정  
→ Weaving을 통해 비즈니스 메서드를 수정하지 않고도 횡단 관심에 해당하는 기능을 추가하거나 변경할 수 있음  
→ 처리방식: 컴파일타임 위빙, 로딩타임 위빙, 런타임 위빙 (스프링에서는 런타임 위빙 방식만 지원)  
  
#### ⭐️ 그림을 통한 정리
<img src="https://user-images.githubusercontent.com/56003992/152290679-4d0a0c0b-972e-4a5a-9850-2e84f89183d7.png" width=512 height=170>  
  
  
### 2.2 AOP 엘리먼트
  
☛ AOP 관련 설정은 XML 방식과 annotation 방식이 있지만, XML부터 알아보자.  
  
#### ❖ &lt;aop:config&gt; 엘리먼트
: AOP 설정에서 루트 엘리먼트  

#### ❖ &lt;aop:pointcut&gt; 엘리먼트
: Pointcut 지정을 위해 사용
: <aop:config>나 <aop:aspect>의 자식 엘리먼트로 사용  
: but <aop:aspect> 하위에 설정된 Pointcut은 해당 <aop:aspect>에서만 사용 가능  

#### ❖ &lt;aop:aspect&gt; 엘리먼트 
: 핵심 관심에 해당하는 Pointcut 메서드와 횡단 관심에 해당하는 Advise 메서드를 결합하기 위해 사용  
: aspect를 어떻게 설정하느냐에 따라 위빙 결과가 달라짐  
```XML
<aop:aspect ref="log">
    <aop:before pointcut-ref="getPintcut" method="printLog" />
</aop:aspect>
```  

#### ❖ &lt;aop:advisor&gt; 엘리먼트
: aspect와 같은 기능 수행  
: but, 트랜잭션 설정과 같은 몇몇 특수한 경우는 무조건 Advisor를 사용해야 함  
→ **Advise 객체의 id를 모르거나 메서드 이름을 확인할 수 없을 때는 aspect를 사용할 수 없다❗️**  

### 2.3 포인트컷 표현식
: expression="execution(①리턴타입 ②패키지경로. .③클래스명.④메서드명(⑤매개변수))"  
  
#### ① 리턴타입 지정
|표현식|설명|
|----|---|
|&#42;|모든 리턴타입 허용|
|void|리턴타입이 void인 메서드 선택|
|!void|리턴타입이 void가 아닌 메서드 선택|
  
#### ② 패키지 지정
|표현식|설명|
|----|---|
|com.springbook.biz|정확하게 com.springbook.biz 패키지만 선택|
|com.springbook.biz..|com.springbook.biz 패키지로 시작하는 모든 패키지 선택|
|com.springbook..impl|com.springbook 패키지로 시작하면서 마지막 패키지 이름이 impl로 끝나는 패키지 선택|
  
#### ③ 클래스 지정
|표현식|설명|
|----|---|
|BoardServiceImpl|정확하게 BoardServiceImpl 클래스만 선택|
|&#42;Impl|Impl로 끝나는 클래스 선택|
|BoardService+|클래스 이름 뒤에 +가 붙으면 해당 클래스로부터 파생된 모든 자식 클래스 선택 </br> 인터페이스 뒤에 +가 붙으면 해당 인터페이스를 구현한 모든 클래스 선택|
  
#### ④ 메서드 지정
|표현식|설명|
|----|---|
|&#42;(..)|가장 기본 설정으로 모든 메서드 선택|
|get&#42;(..)|메서드 이름이 get으로 시작하는 모든 메서드 선택|
  
#### ⑤ 매개변수 지정
|표현식|설명|
|----|---|
|(..)|..은 매개변수 개수와 타입에 제약이 없음|
|(&#42;)|반드시 1개의 매개변수를 가진 메서드만 선택|
|(com.springbook.user.UserVO)|매개변수로 UserVo를 가지는 메서드만 선택|
|(!com.springbook.user.UserVO)|매개변수로 UserVo를 가지지 않는 메서드만 선택|
|(Integer, ..)|한 개 이상의 매개변수를 가지되, 첫 번째 매개변수의 타입이 Integer인 메서드만 선택|
|(Integer, &#42;)|두 개 이상의 매개변수를 가지되, 첫 번째 매개변수의 타입이 Integer인 메서드만 선택|
  
* * *

# Class 03. 어드바이스 동작 시점
  
✎ Advise는 각 JoinPoint에 삽입돼 동작할 횡단 관심에 해당하는 공통 기능  
✎ <aop:aspect> 엘리먼트 하위에 동작시점 지정  

#### 동작시점
|동작 시점|설명|
|-------|---|
|Before|비즈니스 메서드 실행 전 동작|
|After|- After Returning: 비즈니스 메서드가 성공적으로 리턴되면 동작 </br> - After Throwing: 비즈니스 메서드 실행 중 예외가 발생하면 동작 </br> - After: 비즈니스 메서드가 실행된 후 무조건 실행|
|Around|메서드 호출 자체를 가로채 비즈니스 메서드 실행 전후에 처리할 로직을 삽입할 수 있음|

* * *

# Class 04. JoinPoint와 바인드 변수
  
⭐️ 횡단 관심에 해당하는 Advise 메서드를 의미 있게 구현하려면 클라이언트가 호출한 비즈니스 메서드의 정보가 필요  
➡️ 스프링에서는 이런 다양한 정보들을 이용할 수 있도록 JoinPoint 인터페이스 제공  
  
### 4.1 JoinPoint 메서드
  
|메서드|설명|
|----|---|
|Signature getSignature()|클라이언트가 호출한 메서드의 시그니처 정보가 저장된 Signature(리턴 타입, 이름, 매개변수) 객체 반환|
|Object getTarget()|클라이언트가 호출한 비즈니스 메서드를 포함하는 비즈니스 객체 반환|
|Object[] getArgs()|클라이언트가 메서드를 호출할 때 넘겨준 인자 목록을 Object 배열로 반환|
  
**→ [이전에 실습했던 예제](https://github.com/khsexk/Spring-Boot_Study/blob/main/SPING%20QUICK%20START/Day_2/BoardAOP/src/main/java/com/springbook/biz/common/AroundAdvice.java)에서 사용했던 ProceedingJoinPoint 인터페이스는 JoinPoint를 상속했고, 따라서 JoinPoint가 가진 모든 메서드를 지원하며, proceed() 메서드를 추가했다고 보면 됨**  
**→ Around Advise를 제외한 다른 Advise 또한 JoinPoint를 사용해야 하고, proceed() 메서드가 필요한 Around Advise만 ProceedingJoinPoint를 사용함**  
  
#### ❖ Signiture 메서드
|메서드|설명|
|----|---|
|String getName()|클라이언트가 호출한 메서드 이름 반환|
|String toLongString()|클라이언트가 호출한 메서드의 리턴타입, 이름, 매개변수를 패키지 경로까지 포함하여 반환|
|String toShortString()|클라이언트가 호출한 메서드 시그니처를 축약한 문자열로 반환|
  
#### ❖ JoinPoint 객체 사용하기
- JoinPoint를 어드바이스 메서드 매개변수로 선언만 하면 됨
- 클라이언트가 비즈니스 메서드를 호출할 때 스프링 컨테이너 JoinPoint 객체 생성
- 메서드 호출과 관련된 모든 정보를 JoinPoint 객체에 담아 Advise 메서드를 호출할 때 인자로 넘겨줌  
  
### 4.2 Before 어드바이스
  
: 비즈니스 메서드가 실행되기 전에 동작할 로직 구현  
☛ 호출된 메서드 시그니처만 알 수 있으면 다양한 사전 처리 로직 구현 가능  
→ 예제: [BeforeAdvice.java](https://github.com/khsexk/Spring-Boot_Study/blob/main/SPING%20QUICK%20START/Day_2/BoardAOP/src/main/java/com/springbook/biz/common/BeforeAdvice.java)
  
### 4.3 After Returning 어드바이스
  
: 비즈니스 메서드가 수행되고 나서, 결과 데이터를 리턴할 때 동작하는 어드바이스  
☛ 어떤 메서드가 어떤 값을 리턴했는지를 알아야 사후 처리 기능을 다양하게 구현 가능  
→ 예제: [AfterReturning.java](https://github.com/khsexk/Spring-Boot_Study/blob/main/SPING%20QUICK%20START/Day_2/BoardAOP/src/main/java/com/springbook/biz/common/AfterReturningAdvice.java)  
- 클라이언트가 호출한 비즈니스 메서드 정보를 알아내기 위해 JoinPoint 객체를 첫번째 매개변수, Object 타입의 변수(바인드 변수)를 두번째 매개변수로 선언  
- 바인드 변수는 비즈니스 메서드가 리턴한 결괏값을 바인딩할 목적으로 사용되며 어떤 값이 리턴될지 모르기 때문에 Object 타입으로 선언
- 바인드 변수는 스프링 설정 파일에서 <aop:after-returning> 엘리먼트의 returning 속성을 사용하여 매핑  
  
### 4.4 After Throwing 어드바이스
  
: 비즈니스 메서드가 수행되다가 예외가 발생할 때 동작하는 어드바이스  
☛ 어떤 메서드에서 어떤 예외가 발생했는지 알아야 함  
→ 예제: [AfterThrowing.java](https://github.com/khsexk/Spring-Boot_Study/blob/main/SPING%20QUICK%20START/Day_2/BoardAOP/src/main/java/com/springbook/biz/common/AfterThrowingAdvice.java)  
- 바인드 변수는 스프링 설정 파일에서 <aop:after-throwing> 엘리먼트의 throwing 속성을 사용하여 매핑  
  
### 4.5 Around 어드바이스
  
: proceed() 메서드를 사용하기 위해 ProceedingJoinPoint 객체 사용  
→ 예제: [AroundAdvice.java](https://github.com/khsexk/Spring-Boot_Study/blob/main/SPING%20QUICK%20START/Day_2/BoardAOP/src/main/java/com/springbook/biz/common/AroundAdvice.java)
  
* * *
  
# Class 05. 어노테이션 기반 AOP

☛  XML과 어노테이션 설정을 적절히 혼합하여 사용하면 XML 설정을 최소화하면서 객체들을 효율적으로 관리할 수 있음  
  
### 5.1 어노테이션 기반 AOP 설정
  
✎ AOP 어노테이션 설정을 위해스프링 설정 파일에 &lt;aop:aspectj-autoproxy&gt; 엘리먼트 선언  
✎ AOP 관련 어노테이션들은 Advice 클래스에 설정하고, 스프링 컨테이너가 이를 처리하게 하려면 Advice 객체가 생성돼 있어야 함  
```
<aop:aspectj-autoproxy></aop:aspectj-autoproxy>
```  
- Annotation 설정
  - @Service
  - public class LogAdvice { } 
- XML 설정
  - &lt;bean id="log" class="com.springbook.biz.common.LogAdvice"&gt;&lt;/bean&gt;  

#### Pointcut 설정
: @Pointcut → 하나의 Advice 클래스 안에 여러 개의 Pointcut 선언 가능  
: @Pointcut이 붙으 참조 메서드 이름을 이용하여 특정 Pointcut 지정 가능  

#### Advice 설정
|어노테이션|설명|
|-------|---|
|@Before|비즈니스 메서드 실행 전에 동작|
|@AfterReturning|비즈니스 메서드가 성공적으로 리턴되면 동작|
|@AfterThrowing|비즈니스 메서드 실행 중 예외 발생 시 동작|
|@After|비즈니스 메서드 실행 후 무조건 실행|
|@Around|호출 자체를 가로체 비즈니스 메서드 실행 전후에 처리할 로직 삽입 가능|
  
#### aspect 설정

: 클래스 위에 ***@Aspect***를 설정해주면, 스프링 컨테이너는 그 객체를 Aspect 객체로 인식  
: Advice 메서드와 Pointcut 메서드를 결합  

### 5.2 어드바이스 동작 시점

#### 어드바이스 별 어노테이션 선언
|Advice|Annotation 설정|
|-------|---|
|Before|@Before("Pointcut 메서드")|
|After Returning|@AfterReturning(pointcut="Pointcut 메서드", returning="바인드 변수")|
|After Throwing|@AfterThrowing(pointcut="Pointcut 메서드", throwing="바인드 변수")|
|After|@After("Pointcut 메서드")|
|Around|@Around("Pointcut 메서드")|
  
#### 외부 Pointcut 참조하기
☛ XML 설정과 다르게 annotation 설정을 하면서 비슷하거나 같은 Pointcut이 반복 선언되는 문제 발생!  
: Pointcut을 외부에 독립적인 클래스로 설정해주자.  
: 타 클래스에서 참조할 때는 ***"클래스 명.Pointcut메서드"***  
```java
@Aspect
public class PointcutCommon {
	@Pointcut("execution(* com.springbook.biz..*Impl.*(..))")
	public void allPointcut() {}
	
	@Pointcut("execution(* com.springbook.biz..*Impl.get*(..))")
	public void getPointcut() {}
}
```

* * *

# Class 06. 스프링 JDBC
  
❖ DBCP: DataBase Connection Pool 의 약자로 DB와 커넥션을 맺고 있는 객체를 관리하는 역할  
  
### 6.1 스프링 JDBC 개념
: JDBC는 가장 오랫동안 Java 개발자들이 사용한 DB 연동 기술이다. JDBC를 이용해 DB 연동 프로그램을 개발하면 DB에 비종속적인 연동 로직을 구현할 수 있다. 하지만 JDBC를 이용하면 적어야 할 코드가 너무 많아진다. JDBCUtil 클래스를 통해 연동 부분을 따로 분리할 수 있지만, 이 또한 불편한 점이 많다. 스프링은 JDBC 기반의 DB 연동 프로그램으 쉽게 개발할 수 있도록 JdbcTemplate 클래스를 지원한다.  

### 6.2 JdbcTemplate 클래스
☛ GoF 디자인 패턴 중 템플릿 메서드 패턴이 적용된 클래스  
✎ 템플릿 메서드 패턴은 복잡하고 반복되는 알고리즘을 캡슐화해서 재사용하는 패턴으로 정의할 수 있음  

#### 반복되는 DB 연동 로직은 JdbcTemplate 클래스의 템플릿 메서드가 제공하고, 개발자는 달라지는 SQL 구문과 설정값만 신경 쓰면 된다!
<img src="https://user-images.githubusercontent.com/56003992/152495800-0319c1bb-7650-41aa-a69a-12657ff16503.png" width=500 height="220">  
→ JdbcTemplate 클래스는 내부적으로 JDBC API를 이용하여 실제 DB 연동 작업 처리

### 6.3 스프링 JDBC 설정
☛ pom.xml 파일애 DBCP 관련 &lt;dependency&gt; 설정 추가  
```xml
<!-- Spring -->
<dependency>
	<groupId>org.springframework</groupId>
	<artifactId>spring-jdbc</artifactId>
	<version>5.3.15</version>
</dependency>
		
<!-- DBCP -->
<dependency>
	<groupId>commons-dbcp</groupId>
	<artifactId>commons-dbcp</artifactId>
	<version>1.4</version>
</dependency>
```

#### ❖ DataSource 설정

* * *

# Class 07.




* * *
