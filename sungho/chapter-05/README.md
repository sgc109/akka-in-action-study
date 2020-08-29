Akka 5장

## Future
* 아카는 처음에 자체적으로 퓨처 타입을 제공
* 유용성이 알려지면서 scalaz 같은 다른 라이브러리도 퓨처를 제공하기 시작
* 결국 퓨처는 표준 스칼라 라이브러리가 되었다(SIP-14)
* 액터는 객체로, 퓨처는 비동기 함수로 기능을 제공
* 액터와 퓨처 중 하나만 선택할 필요는 없고 둘 다 사용 가능
* 아카는 둘을 조합하여 사용할 수 있는 기능을 제공
* 퓨쳐는 함수 호출의 결과가 사용 가능해 질 때 꺼내 쓸 수 있는 placeholder
* 여러번 읽을 수 있으며, 읽기 전용
* 한 함수의 결과가 다음 함수의 입력이 되고, 여러 함수가 병렬로 연결되며, 나중에 최종 결과를 조합해야하는 **파이프라이닝**에 유용
* 액터는 상태의 세밀한 처리, 예외 감시, retry 등이 필요할 때는 더 유용할 수 있음
* 대신 작성해줘야 할 코드가 많고 여러번 메시지를 주고받아야해서 불필요하게 복잡함
* 위와 같은것은 필요없고 예외 발생 시 단순히 기본 값을 반환하거나, 여러 함수를 병렬로 실행하거나, 한 함수의 출력을 다른 함수의 입력으로 넣는 시나리오에선 퓨쳐가 나음
* 스레드 X 는 블로킹 함수를 스레드 Y 가 실행하도록 하고, 결과를 담은 용기인 Future 만 받음. 결과값을 직접 사용할 순 없음.
* 다음과 같이 사용한다. Future 의 인자로 call-by-name 으로 함수를 넣는다.
```scala
val request = MakeRequest(someValue)
val future = Future {
	val response = someHttpRequest(request)
	response.data
}
```
* 퓨쳐가 담은 데이터는 foreach(), map() 같은 함수로 사용할 수 있음
* foreach 는 데이터가 준비되면 실행될, 코드 블락을 인자로 받음
* 만약 코드 블락에서 또 다른 Future 를 반환하게 하려면 map() 을 써야함
* 만약 코드 블락에서 동기로 함수 호출을 하려면 foreach 를 써도 됨
* flatMap 은 코드 블락에서 Future 를 반환하는 경우 Future[Future[타입]] 을 Future[타입] 으로 바꿔줌
* Future.map(), Future.flatMap() 등은 퓨처가 실패하면 인자로 받은 코드블락을 실행하지 않음
* Future 를 사용할 땐 implicit 으로 ExecutionContext 를 넣어줘야함
* ExecutionContext 는 스레드풀 안에서 작업을 실행하는것을 추상화한것
i* mport scala.concurrent.Implicits.global 을 하면됨(전역 실행 컨텍스트)(global execution context)

## Promise
* Future 가 **읽기 전용**이라면, Promise 는 **쓰기 전용**이다.

## Future 의 Exception Handling
* Future.apply 에 넘겨지는 코드 블락은 다른 스레드에서 실행되므로 여기서 발생한 예외를 받기 위해서는 onComplete() 같은 함수를 사용해야함
* onComplete 에 넘긴 코드블락은 나중에 Try 를 전달 받는데, Try 는 Success 혹은 Failure 임
* 퓨처는 OOM, ThreadDeath 등 fatal exception 은 결코 처리하지 않는다. 심지어 Future 만들어 지지도 않음
* 이런 오류를 무시하거나, 발생하지 않았던 것처럼 꾸밀 수 있게 허용하는 것은 아주 나쁜 생각일 수 있음
* onComplete 말고 onFailure 도 있음.
* 퓨처를 사용할 땐 항상 Immutable 를 사용해야함
* 그러지 않으면 여러 스레드가 같은 객체를 사용하기 때문에 문제가 생길 수 있음
* Future.recover() 는 예외가 발생했을 때 다른 퓨처를 반환하고 싶을 때 사용
* 퓨처가 실패했을 때 기본값을 반환하여 흐름이 계속되게 할 수 있음
* 어떤 비동기 함수가 실패했을 때 기본값을 주고 싶을 때 유용

## Future 조합하기
* Future.firstCompletedOf() 는 여러 서비스 중 가장 빨리 응답한 서비스의 결과를 사용할 때 유용
* 인자로 같은 타입의 값을 담는 List 를 넣으면 됨
* Future.sequence() 로 Seq[Future[타입]] 을 Future[Seq[타입]] 으로 변환할 수 있음
* 애초에 map 과 Future.sequence() 의 조합 대신 Future.traverse() 를 써도 됨

## Actor 와 Future 조합하기
* Actor 의 ask 는 Future 를 반환
* akka 의 pipe() 를 통해 Future 의 결과를 다른 Actor 로 전송 가능
* Future 안에서 Actor 의 변경 가능한 상태를 참조하는 일은 피해야함
* 그러려면 sender() 같이 변경될 수 있는 값은 미리 캡처해두거나, Future 를 pipe 로 sender() 에게 연결해야함
* 그리고 Immutable 데이터를 써야함
