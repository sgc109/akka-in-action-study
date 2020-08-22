# 액터를 사용한 테스트 주도 개발

## 액터 테스트 개요
* Actor 가 우리의 예상대로 동작하는지 살펴보기 위해서는 액터가 **어떤 상태를 갖는지**, **어떤 메시지를 보내는지**, **다른 객체의 상태를 어떻게 변화시키는지**를 테스트하고 싶을 것이다.
* Akka 에서 Actor 를 테스트할 때는 akka-testkit 이라는 패키지를 사용한다.
* testkit 에는 TestKit 이라는 클래스가 있는데, 여기에 testActor 라는 녀석이 있다.
* 액터의 테스트는 주로 우리의 액터가 이 testActor 액터에게 메시지를 보내게 하고, testActor 가 받은 메시지를 검사하는 식으로 진행된다.
* TestKit 클래스에는 testActor 를 비롯하여 testActor 가 받은 메시지를 검사하는 assert 함수(expectMsg, expectMsgPF, expectNoMsg, ...)가 많이 있다.
* 그래서 테스트를 위해서는 우리의 테스트 코드가 들어있는 클래스가 TestKit 를 상속 받도록 한다. 
* 단일 스레드 환경에서 액터의 상태만 확인하고 싶다면 testActor 에 메시지를 보내는 것이 아니라, TestActorRef 를 사용하여 직접 액터의 상태에 접근할 수도 있다.
* 하지만 대부분의 경우 멀티 스레드 환경에서 동작하기 때문에 testActor 에게 메시지를 보내도록 한다.
* 테스트의 끝에는 항상 액터 시스템을 종료해주어야 하므로 BeforeAndAfterAll 을 상속하여 afterAll() 함수를 이용하도록 한다.
* ScalaTest 의 WordSpecLike 와 MustMatchers 를 사용하여(테스트 클래스에서 상속하여 사용) BDD 스타일의 테스트가 가능하다.

## 테스트 환경
* 테스트 환경은 크게 3가지로 구분된다.
    * 싱글 스레드 Unit Test
        * 여러 스레드에 의해 액터의 상태가 변경될 여지가 없기 때문에 직접 액터의 상태에 접근하여 확인한다.
        * TestActorRef 를 사용한다.
    * 멀티 스레드 Unit Test
        * 액터의 상태나 메시지를 testActor 라는 테스트 전용 액터에 보내게하여 이 액터를 검사한다.
        * TestKit 나 TestProbe 클래스를 사용한다.
        * 대부분의 테스트는 이 방식으로 진행된다.
    * 멀티 JVM Test
        * 리모트 액터를 테스트한다.
* 싱글 JVM 에서 멀티 스레드로 동작하는 것이 기본이라고 할 수 있기 때문에 두번째 방식은 많이 사용된다.

## 액터의 유형 
* 액터는 역할에 따라 크게 3가지 유형으로 구분된다.
    * SilentActor
        * 말 그대로 조용한, 즉 외부에 메시지를 보내지 않는 액터
        * 따라서 내부 상태를 살펴보기 위해서는 TestActorRef 를 사용하거나(싱글 스레드 환경 테스트), testActor 에 자신의 상태에 대한 메시지를 보내도록 해야한다(멀티 스레드 환경 테스트).
        * TestActorRef 를 사용하는 경우에는 굳이 기존의 코드를 변경할 필요가 없지만,
        * testActor 를 사용하는 경우에는 별도의 메시지 타입을 정의하고, receive 함수를 수정하여 해당 메시지를 받으면 자신의 상태를 testActor 에게 보내도록 해야한다. 
    * SendingActor
        * 다른 액터에게 메시지를 보내는 액터
        * 일반적인 멀티 스레드 환경에서의 테스트처럼 testActor 를 사용하여 테스트한다.
        * 액터를 생성할 때 생성자로 testActor 를 넣어주고 확인하고자 메시지를 보내도록 할 수 있다.
        * 테스트 클래스가 ImplicitSender 를 상속하면, 테스트하고자 하는 액터 내에서 sender() 함수를 호출하면 testActor 가 반환되므로, 명시적으로 testActor 를 전달할 필요가 없다.
        * 둘 이상의 액터에게 메시지를 보내는 경우는 testActor 대신 TestProbe 를 사용한다.
    * SideEffectingActor
        * 위와 같은 방식으로 testActor 를 이용하여 테스트
