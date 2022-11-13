# ch4 스트림 소개

### 컬렉션 Collections

데이터를 그룹화 및 처리 가능

![Untitled](ch4%20%E1%84%89%E1%85%B3%E1%84%90%E1%85%B3%E1%84%85%E1%85%B5%E1%86%B7%20%E1%84%89%E1%85%A9%E1%84%80%E1%85%A2%207eb211e540644147a71baeee2dc7fd70/Untitled.png)

**Collection 인터페이스의 특징**

| 인터페이스 | 구현클래스 | 특징 |
| --- | --- | --- |
| Set | HashSet
TreeSet | 순서를 유지하지 않는 데이터의 집합으로 데이터의 중복을 허용하지 않는다. |
| List | LinkedList
Vector
ArrayList | 순서가 있는 데이터의 집합으로 데이터의 중복을 허용한다. |
| Queue | LinkedList
PriorityQueue | List와 유사 |
| Map | Hashtable
HashMap
TreeMap | 키(Key), 값(Value)의 쌍으로 이루어진 데이터으 집합으로,
순서는 유지되지 않으며 키(Key)의 중복을 허용하지 않으나 값(Value)의 중복은 허용한다. |

## 4.1 스트림이란 무엇인가?

**Stream**

- 자바 8 API 추가된 기능
- **선언형**(데이터를 처리하는 임시 구현 코드 대신 질의로 표현 가능)으로 컬렉션 데이터 처리 가능
- 멀티스레드 코드를 구현하지 않아도 데이터를 **투명하게** 병렬 처리 가능
    - `parallelStream()`: 멀티코어 아키텍처에서 병렬로 실행 가능(7장 추가 설명)

**선언형으로 코드 구현**

: loop와 if 조건문 등의 제어 블록을 사용해서 어떻게 동작을 구현할지 지정할 필요 없이 동작의 수행을 지정 가능

- 람다 표현식 사용
- filter, sorted, map, collect와 같은 빌딩 블록 연산 연결을 통해 복잡한 데이터 처리 파이프 라인 만들기 가능
    - 고수준 빌딩 블록  high-level building block: 내부적으로 단일 스레드 모델에 사용 + 멀티코어 아키텍처를 최대한 투명하게 활용 가능

**자바 7 이전**

```java
List<Dish> lowCaloricDishes = new ArrayList<>();
for (Dish dish : menu) { //누적자로 요소 필터링
    if (dish.getCalories() < 400) {
        lowCaloricDishes.add(dish);
    }
}
Collections.sort(lowCaloricDishes, new Comparator<Dish>() {
    public int compare(Dish dish1, Dish dish2) { //익명 클래스로 요리 정렬
        return Integer.compare(dish1.getCalories(), dish2.getCalories());
    }
});
List<String> lowCaloricDishesName = new ArrayList<>();
for (Dish dish : lowCaloricDishes) {
    lowCaloricDishesName.add(dish.getName()); //정렬된 리스트를 처리하면서 요리 이름 선택
}
```

**자바 8 이후(스트림 적용 후)**

```java
List<String> lowCaloricDishesName =
                    menu.stream().filter(d -> d.getCalories() < 400)
                        .filter(d -> d.getCalories() < 400) //400칼로리 이하의 요리 선택
                        .sorted(comparing(Dish::getCalories)) //칼로리로 요리 정렬
                        .map(Dish::getName) //요리명 추출
                        .collect(toList()); //모든 요리명을 리스트에 저장
```

**요약**

- 선언형: 더 간결하고 가독성이 좋아진다
- 조립 가능: 유연성이 좋아진다
- 병렬화: 성능이 좋아진다

+ 5장에서 복잡한 데이터 처리 질의를 표현하는 스트림 기능을 살펴 본다

## 4.2 스트림 시작하기

### 스트림

> 자바8에 컬렌션에는 스트림을 반환하는 `stream` 메서드 추가
데이터 처리 연산을 지원하도록 소스에서 추출된 `연속된 요소` Sequence of elements
> 

- **연속된 요소:** 특정 요소 형식으로 이루어진 연속된 값 집합의 인터페이스 제공
    - 컬렉션 = 자료구조. 시/공간 복잡도와 저장 및 접근 연산에 중점. ex) ArrayList, LinkedList
    - 스트림 = 표현 계산식 filter, sorted, map
- **소스**: 컬렉션, 배열, I/O 자원 등의 데이터 제공 소스로부터 데이터 소비 → 같은 순서 유지
- **데이터 처리 연산**
    - 함수형 프로그래밍 언어에서 일반적으로 지원하는 연산 + 데이터베이스에서 지원하는 연산 지원
    - 순차적 + 병렬로 실행

### 2가지 주요 특징

1. **파이프라이닝 Pipelining** 스트림 연산끼리 연결해서 커다란 파이프라인을 구성할 수 있도록 스트림 자신 반환
    1. 최적화: 게으름(Laziness), 쇼트서킷(Short-circuiting)
    2. DB query와 유사
2. **내부 반복:** 컬렉션은 반복자를 이용해서 명시적으로 반복 but 스트림은 내부 반복 지원

```java
/** 
 * 데이터 소스 : 요리 메뉴 
 * 연속된 요소 : 내부 리스트 객체
 * */
List<DishVo> menu = Arrays.asList(
        new DishVo("pork", false, 800, Type.MEAT),
        new DishVo("beef", false, 700, Type.MEAT),
        new DishVo("chicken", false, 400, Type.MEAT),
        new DishVo("fries", true, 500, Type.OTHER),
        new DishVo("rice", true, 350, Type.OTHER),
        new DishVo("fruit", true, 120, Type.OTHER),
        new DishVo("pizza", false, 600, Type.OTHER),
        new DishVo("prawns", false, 300, Type.FISH),
        new DishVo("salmon", false, 450, Type.FISH)
);

/**
 * 스트림으로 데이터 처리
 * filter, map, limit, collect 데이터 처리 연산
 * filter, map, limit은 스트림을 반환(파이프라인 형성을 위해)하지만, collect는 리스트를 반환한다.
 */
List<String> threeHighCaloricDishNames = 
        menu.stream() // 메뉴에서 스트림을 얻는다.
             // 파이프라인 연산 만들기   
            .filter(dishVo -> dishVo.getCalories() > 300) // 고칼로리 요리 필터링, Stream<Dish> // 중간 연산
            .map(DishVo::getName) // 요리명 추출 Stream<String> // 중간 연산
            .limit(3) // 선착순 3개만 선택 Stream<String> // 중간 연산
            .collect(toList()); // 결과를 다른 리스트로 저장 List<String>
        
System.out.println(threeHighCaloricDishNames); // [pork, beef, chicken]
```

## 4.3 스트림과 컬렉션

자바의 컬렉션과 스트림 모두 **연속된 요소** 형식의 값을 저장하는 자료 구조의 인터페이스 제공

: 컬렉션과 스트림의 차이 → ***데이터를 언제 계산하는가?** 소수 집합*

- 컬렉션
    - 현재 자료구조가 포함한 모든 값을 메모리에 저장하는 구조 → 모든 값을 계산할 때까지 기다린다 = 적극적 생성
    - 생산자 중심
    - 컬렉션에 추가되기 전에 계산
    - 외부 반복 external iteration
- 스트림
    - 요청할 때만 요소를 계산하는 고정된 자료 구조 = 게으른 생성
    - 생산자 producer와 소비자 consumer 관계 형성
    - 내부 반복 internal iteration

### 4.3.1 딱 한 번만 탐색할 수 있다.

<aside>
💡 탐색된 스트림의 요소는 소비된다

</aside>

### 4.3.2 외부 반복과 내부 반복

**외부 반복** external iteration: 사용자가 직접 요소 반복 + 병렬성 스스로 관리

**내부 반복** internal iteration: 함수에 어떤 작업을 수행할지 지정하면 모든 것이 알아서 처리 + 병렬성 구현은 자동으로 선택

```java
// 외부 반복
List<String> names = new ArrayList<>();
for(Dish dish: menu) {
  names.add(dish.getName());
}

// 내부 반복
List<String> names = menu.stream()
  .map(Dish::getName)
  .collect(toList());
```

![Untitled](ch4%20%E1%84%89%E1%85%B3%E1%84%90%E1%85%B3%E1%84%85%E1%85%B5%E1%86%B7%20%E1%84%89%E1%85%A9%E1%84%80%E1%85%A2%207eb211e540644147a71baeee2dc7fd70/Untitled%201.png)

![Untitled](ch4%20%E1%84%89%E1%85%B3%E1%84%90%E1%85%B3%E1%84%85%E1%85%B5%E1%86%B7%20%E1%84%89%E1%85%A9%E1%84%80%E1%85%A2%207eb211e540644147a71baeee2dc7fd70/Untitled%202.png)

## 4.4 스트림 연산

스트림 인터페이스 연산은 크게 2가지로 구분

```java
List<Stirng> names = menu.stream() // 스트림 open
		.filter(dish -> dish.getCalories > 300) // 중간 연산 시작
		.map(Dish::getname) // 중간 연산
		.limit(3) // 중간 연산 끝(short-circuit)
		.collect(toList()); // 스트림을 리스트로 반환. 최종 연산
```

![Untitled](ch4%20%E1%84%89%E1%85%B3%E1%84%90%E1%85%B3%E1%84%85%E1%85%B5%E1%86%B7%20%E1%84%89%E1%85%A9%E1%84%80%E1%85%A2%207eb211e540644147a71baeee2dc7fd70/Untitled%203.png)

### 4.4.1 중간 연산 intermediate operatioin

> 연결할 수 있는 스트림 연산
> 

다른 스트림 반환

여러 중간 연산을 연결해서 질의 만들기 가능

단말 연산을 스트림 파이프라인에 실행하기 전까지 연산 수행 X = **게으르다 lazy**

- 쇼트 서킷 short circuit: 모든 연산을 다 하기 전에 조건을 만족하는 경우 추가적인 불필요한 연산 수행 X.
ex) `limit(3)`
- 루프 퓨전 loop fusion: 2개 이상의 연산이 합쳐 하나의 연산으로 처림.
ex) print을 통해 filter와 map 연산이 서로 다른 연산이지만 한 과정으로 병합처리되는 것 확인 가능

### 4.4.2 최종 연산 terminal operation

> 스트림을 닫는 연산
> 

스트림 파이프 라인에서 결과 도출

List, Integer, void 등 스트림 이외의 결과 반환

ex) `forEach`: 각 요소에 람다를 적용한 다음에 void를 반환하는 최종 연산

### 4.4.3 스트림 이용하기

1. 질의를 수행할(컬렉션 같은) 데이터 소스
2. 스트림 파이프라인을 구성할 중간 연산 연결
3. 스트림 파이프라인을 실행하고 결과를 만들 최종 연산

스트림 파이프라인의 개념은 `빌더 패턴(builder pattern)`과 유사 → builder 패턴은 호출을 연결해서 설정을 만들고 마지막 build() 메서드를 호출해서 닫는다.

## 4.5 로드맵

5장에서 스트림이 제공하는 연산 및 어떤 종류의 질의가 표현 가능한지 사용 사례 확인

## 4.6 마치며

- 스트림은 소스에서 추출된 연속 요소로 데이터 처리 연산을 지원한다.
- 스트림은 내부 반복을 지원한다. 내부 반복은 filter, map, sorted 등의 연산으로 반복을 추상화한다
- 스트림에는 중간 연산과 최종 연산이 있다.
- 중간 여난을 filter와 map 처럼 스트림을 반환하면서 다른 연산과 연결되는 연산이다. 중간 연산을 이용해서 파이프라인을 구성할 수 있지만 중간 연산으로는 어떤 결과도 생성할 수 없다.
- forEach나 count처럼 스트림 파이프 라인을 처리해서 스트림이 아닌 결과를 반환하는 연산을 최종 연산이라고 한다.
- 스트림의 요소는 요청할 때 게으르게 계산된다.